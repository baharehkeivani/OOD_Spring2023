/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.core.jswdk;

import java.util.Properties;
import java.io.IOException;

import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileLock;

/** Provides ability to edit serlet execution parameters, such as name, mapping,
* initialization parameters, request parameters or request method (in the future).<br>
* Empty marker interface, the real functionality is somewhere else.<br>
* Can be implemented by a DataObject, Executor or a DebuggerType.
* @author  Petr Jiricka
* @version 1.00, Jun 03, 1999
*/
public class PropertiesUtil {

    public static Properties loadProperties(FileSystem fs, String resourceName) {
        Properties props = new Properties();
        FileObject file = fs.findResource(resourceName);
        if (file != null) {
            try {
                props.load(file.getInputStream());
            }
            catch (Exception e) {}
        }
        return props;
    }

    public static void saveProperties(Properties props, FileSystem fs, String resourceName) throws IOException {
        FileObject file = fs.findResource(resourceName);
        if (file == null) {
            file = FileUtil.createData(fs.getRoot(), resourceName);
        }
        FileLock lock = file.lock();
        try {
            props.store(file.getOutputStream(lock), "Generated by Forte4J IDE"); // NOI18N
        }
        finally {
            lock.releaseLock();
        }
    }

}

/*
 * Log
 *  6    Gandalf   1.5         1/13/00  Petr Jiricka    Properties.save -> 
 *       Properties.store
 *  5    Gandalf   1.4         1/12/00  Petr Jiricka    Fully I18n-ed
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/7/99  Petr Jiricka    
 *  2    Gandalf   1.1         10/7/99  Petr Jiricka    
 *  1    Gandalf   1.0         10/7/99  Petr Jiricka    
 * $
 */
