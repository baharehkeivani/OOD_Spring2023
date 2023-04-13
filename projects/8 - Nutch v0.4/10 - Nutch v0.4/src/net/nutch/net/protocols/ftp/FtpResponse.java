/* Copyright (c) 2004 The Nutch Organization.  All rights reserved.   */
/* Use subject to the conditions in http://www.nutch.org/LICENSE.txt. */

package net.nutch.net.protocols.ftp;

import net.nutch.net.protocols.Response;

import javax.activation.MimetypesFileTypeMap;
// 20040427, xing, disabled for now
// import xing.net.nutch.util.magicfile.*;

import net.nutch.net.protocols.http.MiscHttpAccounting;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

import java.net.InetAddress;
import java.net.URL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.lang.Exception;
import java.lang.StackTraceElement;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;


/************************************
 * FtpResponse.java mimics ftp replies as http response.
 * It tries its best to follow http's way for headers, response codes
 * as well as exceptions.
 *
 * Comments:
 * In this class, all FtpException*.java thrown by Client.java
 * and some important commons-net exceptions passed by Client.java
 * must have been properly dealt with. They'd better not be leaked
 * to the caller of this class.
 *
 * @author John Xing
 ***********************************/
public class FtpResponse implements Response {
  private URL url;
  private final Ftp ftp;
  private int code;
  private int numContinues;
  private Map headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
  private byte[] content;
  private byte[] compressedContent;
  // use httpAccounting since we mimic http response
  MiscHttpAccounting httpAccounting;

  public URL getUrl() { return url; }

  /** Returns the response code. */
  public int getCode() { return code; }

  /** Returns the value of a named header. */
  public String getHeader(String name) { return (String)headers.get(name); }

  /** Returns the full content of the response. */
  public byte[] getContent() { return content; }

  /** 
   * Returns the compressed version of the content if the server
   * transmitted a compressed version, or <code>null</code>
   * otherwise. 
   */
  public byte[] getCompressedContent() { 
    compressedContent = null;
    return compressedContent;
  }

  /**
   * Returns the number of 100/Continue headers encountered 
   */
  public int getNumContinues() {
    numContinues = 0;
    return numContinues;
  }

  FtpResponse(Ftp ftp, URL url) throws IOException, FtpException {
    this(ftp, url, null, null, -1);
  }

  FtpResponse(Ftp ftp, URL url, InetAddress addr,
           MiscHttpAccounting httpAccounting, int httpVersion) 
    throws IOException, FtpException {
    //throws Exception {

    this.url = url;
    this.httpAccounting = httpAccounting;
    this.ftp = ftp;

    if (!"ftp".equals(url.getProtocol()))
      throw new IOException("Not an FTP url:" + url);

    if (url.getPath() != url.getFile()) {
      Ftp.LOG.warning("url.getPath() != url.getFile(): " + url);
    }

    String path = "".equals(url.getPath()) ? "/" : url.getPath();

    try {

      if (ftp.followTalk) {
        Ftp.LOG.info("fetching "+url);
      } else {
        Ftp.LOG.fine("fetching "+url);
      }

      if (addr == null) {
        addr= InetAddress.getByName(url.getHost());
        if (this.httpAccounting != null)
          this.httpAccounting.setAddr(addr);
      }

      // idled too long, remote server or ourselves may have timed out,
      // should start anew.
      if (ftp.client != null && ftp.keepConnection
          && ftp.renewalTime < System.currentTimeMillis()) {
        Ftp.LOG.info("delete client because idled too long");
        ftp.client = null;
      }

      // start anew if needed
      if (ftp.client == null) {
        if (ftp.followTalk)
          Ftp.LOG.info("start client");
        // the real client
        ftp.client = new Client();
        // when to renew, take the lesser
        //ftp.renewalTime = System.currentTimeMillis()
        //  + ((ftp.timeout<ftp.serverTimeout) ? ftp.timeout : ftp.serverTimeout);

        // timeout for control connection
        ftp.client.setDefaultTimeout(ftp.timeout);
        // timeout for data connection
        ftp.client.setDataTimeout(ftp.timeout);

        // follow ftp talk?
        if (ftp.followTalk)
          ftp.client.addProtocolCommandListener(
            new PrintCommandListener(ftp.LOG));
      }

      // quit from previous site if at a different site now
      if (ftp.client.isConnected()) {
        InetAddress remoteAddress = ftp.client.getRemoteAddress();
        if (!addr.equals(remoteAddress)) {
          if (ftp.followTalk)
            Ftp.LOG.info("disconnect from "+remoteAddress
            +" before connect to "+addr);
          // quit from current site
          ftp.client.logout();
          ftp.client.disconnect();
        }
      }

      // connect to current site if needed
      if (!ftp.client.isConnected()) {

        if (ftp.followTalk)
          Ftp.LOG.info("connect to "+addr);

        ftp.client.connect(addr);
        if (!FTPReply.isPositiveCompletion(ftp.client.getReplyCode())) {
          ftp.client.disconnect();
          Ftp.LOG.warning("ftp.client.connect() failed: "
            + addr + " " + ftp.client.getReplyString());
          this.code = 500; // http Internal Server Error
          return;
        }

        if (ftp.followTalk)
          Ftp.LOG.info("log into "+addr);

        if (!ftp.client.login(ftp.userName, ftp.passWord)) {
          // login failed.
          // please note that some server may return 421 immediately
          // after USER anonymous, thus ftp.client.login() won't return false,
          // but throw exception, which then will be handled by caller
          // (not dealt with here at all) .
          ftp.client.disconnect();
          Ftp.LOG.warning("ftp.client.login() failed: "+addr);
          this.code = 401;  // http Unauthorized
          return;
        }

        // insist on binary file type
        if (!ftp.client.setFileType(FTP.BINARY_FILE_TYPE)) {
          ftp.client.logout();
          ftp.client.disconnect();
          Ftp.LOG.warning("ftp.client.setFileType() failed: "+addr);
          this.code = 500; // http Internal Server Error
          return;
        }

        if (ftp.followTalk)
          Ftp.LOG.info("set parser for "+addr);

        // SYST is valid only after login
        try {
          ftp.parser = null;
          String parserKey = ftp.client.getSystemName();
          // some server reports as UNKNOWN Type: L8, but in fact UNIX Type: L8
          if (parserKey.startsWith("UNKNOWN Type: L8"))
            parserKey = "UNIX Type: L8";
          ftp.parser = (new DefaultFTPFileEntryParserFactory())
            .createFileEntryParser(parserKey);
        } catch (FtpExceptionBadSystResponse e) {
          Ftp.LOG.warning("ftp.client.getSystemName() failed: "+addr+" "+e);
          ftp.parser = null;
        } catch (ParserInitializationException e) {
          // ParserInitializationException is RuntimeException defined in
          // org.apache.commons.net.ftp.parser.ParserInitializationException
          Ftp.LOG.warning("createFileEntryParser() failed. "+addr+" "+e);
          ftp.parser = null;
        } finally {
          if (ftp.parser == null) {
            // do not log as severe, otherwise
            // FetcherThread/RequestScheduler will abort
            Ftp.LOG.warning("ftp.parser is null: "+addr);
            ftp.client.logout();
            ftp.client.disconnect();
            this.code = 500; // http Internal Server Error
            return;
          }
        }

      } else {
        if (ftp.followTalk)
          Ftp.LOG.info("use existing connection");
      }

      this.content = null;

      if (path.endsWith("/")) {
        getDirAsHttpResponse(path);
      } else {
        getFileAsHttpResponse(path);
      }

      // reset next renewalTime, take the lesser
      if (ftp.client != null && ftp.keepConnection) {
        ftp.renewalTime = System.currentTimeMillis()
          + ((ftp.timeout<ftp.serverTimeout) ? ftp.timeout : ftp.serverTimeout);
        if (ftp.followTalk)
          Ftp.LOG.info("reset renewalTime to "
            +ftp.httpDateFormat.toString(ftp.renewalTime));
      }

      // getDirAsHttpResponse() or getFileAsHttpResponse() above
      // may have deleted ftp.client
      if (ftp.client != null && !ftp.keepConnection) {
        if (ftp.followTalk)
          Ftp.LOG.info("disconnect from "+addr);
        ftp.client.logout();
        ftp.client.disconnect();
      }
      
    } catch (Exception e) {
      ftp.LOG.warning(""+e);
      StackTraceElement stes[] = e.getStackTrace();
      for (int i=0; i<stes.length; i++) {
        ftp.LOG.warning("   "+stes[i].toString());
      }
      // for any un-foreseen exception (run time exception or not),
      // do ultimate clean and leave ftp.client for garbage collection
      if (ftp.followTalk)
        Ftp.LOG.info("delete client due to exception");
      ftp.client = null;
      // or do explicit garbage collection?
      // System.gc();
// can we be less dramatic, using the following instead?
// probably unnecessary for our practical purpose here
//      try {
//        ftp.client.logout();
//        ftp.client.disconnect();
//      }
      throw new FtpException(e);
      //throw e;
    }

  }

  // get ftp file as http response
  private void getFileAsHttpResponse(String path)
    throws IOException {

    ByteArrayOutputStream os = null;
    List list = null;

    try {
      // first get its possible attributes
      list = new LinkedList();
      ftp.client.retrieveList(path, list, ftp.maxContentLength, ftp.parser);

      os = new ByteArrayOutputStream(ftp.BUFFER_SIZE);
      ftp.client.retrieveFile(path, os, ftp.maxContentLength);

      FTPFile ftpFile = (FTPFile) list.get(0);
      this.headers.put("content-length",
        new Long(ftpFile.getSize()).toString());
      //this.headers.put("content-type", "text/html");
      this.headers.put("last-modified",
        ftp.httpDateFormat.toString(ftpFile.getTimestamp()));
      this.content = os.toByteArray();

      String contentType = null;
      // 20040427, xing, disabled for now
      //if (contentType == null && ftp.magic != null)
      //  contentType = ftp.magic.getMimeType(this.content);
      if (contentType == null && ftp.TYPE_MAP != null)
        contentType = ftp.TYPE_MAP.getContentType(path);
      if (contentType != null)
        this.headers.put("content-type", contentType);

      // approximate bytes sent and read
      if (this.httpAccounting != null) {
        this.httpAccounting.incrementBytesSent(path.length());
        this.httpAccounting.incrementBytesRead(this.content.length);
      }

      this.code = 200; // http OK

    } catch (FtpExceptionControlClosedByForcedDataClose e) {

      // control connection is off, clean up
      // ftp.client.disconnect();
      if (ftp.followTalk)
        Ftp.LOG.info("delete client because server cut off control channel: "+e);
      ftp.client = null;

      // in case this FtpExceptionControlClosedByForcedDataClose is
      // thrown by retrieveList() (not retrieveFile()) above,
      if (os == null) { // indicating throwing by retrieveList()
        //throw new FtpException("fail to get attibutes: "+path);
        Ftp.LOG.warning(
            "Please try larger maxContentLength for ftp.client.retrieveList(). "
          + e);
        // in a way, this is our request fault
        this.code = 400;  // http Bad request
        return;
      }

      FTPFile ftpFile = (FTPFile) list.get(0);
      this.headers.put("content-length",
        new Long(ftpFile.getSize()).toString());
      //this.headers.put("content-type", "text/html");
      this.headers.put("last-modified",
        ftp.httpDateFormat.toString(ftpFile.getTimestamp()));
      this.content = os.toByteArray();

      String contentType = null;
      // 20040427, xing, disabled for now
      //if (contentType == null && ftp.magic != null)
      //  contentType = ftp.magic.getMimeType(this.content);
      if (contentType == null && ftp.TYPE_MAP != null)
        contentType = ftp.TYPE_MAP.getContentType(path);
      if (contentType != null)
        this.headers.put("content-type", contentType);

      // approximate bytes sent and read
      if (this.httpAccounting != null) {
        this.httpAccounting.incrementBytesSent(path.length());
        this.httpAccounting.incrementBytesRead(this.content.length);
      }

      this.code = 200; // http OK

    } catch (FtpExceptionCanNotHaveDataConnection e) {

      if (FTPReply.isPositiveCompletion(ftp.client.cwd(path))) {
      // it is not a file, but dir, so redirect as a dir
        this.headers.put("location", path + "/");
        this.code = 300;  // http redirect
        // fixme, should we do ftp.client.cwd("/"), back to top dir?
      } else {
      // it is not a dir either
        this.code = 404;  // http Not Found
      }

    } catch (FtpExceptionUnknownForcedDataClose e) {
      // Please note control channel is still live.
      // in a way, this is our request fault
      Ftp.LOG.warning(
          "Unrecognized reply after forced close of data channel. "
        + "If this is acceptable, please modify Client.java accordingly. "
        + e);
      this.code = 400; // http Bad Request
    }

  }

  // get ftp dir list as http response
  private void getDirAsHttpResponse(String path)
    throws IOException {
    List list = new LinkedList();

    try {

      // change to that dir first
      if (!FTPReply.isPositiveCompletion(ftp.client.cwd(path))) {
        this.code = 404;  // http Not Found
        return;
      }

      // fixme, should we do ftp.client.cwd("/"), back to top dir?

      ftp.client.retrieveList(null, list, ftp.maxContentLength, ftp.parser);
      this.content = list2html(list, path, "/".equals(path) ? false : true);
      this.headers.put("Content-Length",
        new Integer(this.content.length).toString());
      this.headers.put("Content-Type", "text/html");
      // this.headers.put("Last-Modified", null);

      // approximate bytes sent and read
      if (this.httpAccounting != null) {
        this.httpAccounting.incrementBytesSent(path.length());
        this.httpAccounting.incrementBytesRead(this.content.length);
      }

      this.code = 200; // http OK

    } catch (FtpExceptionControlClosedByForcedDataClose e) {

      // control connection is off, clean up
      // ftp.client.disconnect();
      if (ftp.followTalk)
        Ftp.LOG.info("delete client because server cut off control channel: "+e);
      ftp.client = null;

      this.content = list2html(list, path, "/".equals(path) ? false : true);
      this.headers.put("Content-Length",
        new Integer(this.content.length).toString());
      this.headers.put("Content-Type", "text/html");
      // this.headers.put("Last-Modified", null);

      // approximate bytes sent and read
      if (this.httpAccounting != null) {
        this.httpAccounting.incrementBytesSent(path.length());
        this.httpAccounting.incrementBytesRead(this.content.length);
      }

      this.code = 200; // http OK

    } catch (FtpExceptionUnknownForcedDataClose e) {
      // Please note control channel is still live.
      // in a way, this is our request fault
      Ftp.LOG.warning(
          "Unrecognized reply after forced close of data channel. "
        + "If this is acceptable, please modify Client.java accordingly. "
        + e);
      this.code = 400; // http Bad Request
    } catch (FtpExceptionCanNotHaveDataConnection e) {
      Ftp.LOG.warning(""+ e);
      this.code = 500; // http Iternal Server Error
    }

  }

  // generate html page from ftp dir list
  private byte[] list2html(List list, String path, boolean includeDotDot) {

    //StringBuffer x = new StringBuffer("<!doctype html public \"-//ietf//dtd html//en\"><html><head>");
    StringBuffer x = new StringBuffer("<html><head>");
    x.append("<title>Index of "+path+"</title></head>\n");
    x.append("<body><h1>Index of "+path+"</h1><pre>\n");

    if (includeDotDot) {
      x.append("<a href='../'>../</a>\t-\t-\t-\n");
    }

    for (int i=0; i<list.size(); i++) {
      FTPFile f = (FTPFile) list.get(i);
      String name = f.getName();
      String time = ftp.httpDateFormat.toString(f.getTimestamp());
      if (f.isDirectory()) {
        // some ftp server LIST "." and "..", we skip them here
        if (name.equals(".") || name.equals(".."))
          continue;
        x.append("<a href='"+name+"/"+"'>"+name+"/</a>\t");
        x.append(time+"\t-\n");
      } else if (f.isFile()) {
        x.append("<a href='"+name+    "'>"+name+"</a>\t");
        x.append(time+"\t"+f.getSize()+"\n");
      } else {
        // ignore isSymbolicLink()
        // ignore isUnknown()
      }
    }

    x.append("</pre></body></html>\n");

    return new String(x).getBytes();
  }

}
