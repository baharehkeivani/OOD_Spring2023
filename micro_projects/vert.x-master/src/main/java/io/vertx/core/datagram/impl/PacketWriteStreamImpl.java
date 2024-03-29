/*
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.core.datagram.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

/**
 * A write stream for packets.
 *
* @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
*/
class PacketWriteStreamImpl implements WriteStream<Buffer>, Handler<AsyncResult<Void>> {

  private DatagramSocketImpl datagramSocket;
  private Handler<Throwable> exceptionHandler;
  private final int port;
  private final String host;

  PacketWriteStreamImpl(DatagramSocketImpl datagramSocket, int port, String host) {
    this.datagramSocket = datagramSocket;
    this.port = port;
    this.host = host;
  }

  @Override
  public void handle(AsyncResult<Void> event) {
    if (event.failed() && exceptionHandler != null) {
      exceptionHandler.handle(event.cause());
    }
  }

  @Override
  public PacketWriteStreamImpl exceptionHandler(Handler<Throwable> handler) {
    exceptionHandler = handler;
    return this;
  }

  @Override
  public Future<Void> write(Buffer data) {
    Future<Void> fut = datagramSocket.send(data, port, host);
    fut.onComplete(PacketWriteStreamImpl.this);
    return fut;
  }

  @Override
  public PacketWriteStreamImpl setWriteQueueMaxSize(int maxSize) {
    return this;
  }

  @Override
  public boolean writeQueueFull() {
    return false;
  }

  @Override
  public PacketWriteStreamImpl drainHandler(Handler<Void> handler) {
    return this;
  }

  @Override
  public Future<Void> end() {
    Promise<Void> promise = Promise.promise();
    datagramSocket.close(promise);
    return promise.future();
  }
}
