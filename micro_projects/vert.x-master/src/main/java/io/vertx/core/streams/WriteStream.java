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

package io.vertx.core.streams;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

/**
 *
 * Represents a stream of data that can be written to.
 * <p>
 * Any class that implements this interface can be used by a {@link Pipe} to pipe data from a {@code ReadStream}
 * to it.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@VertxGen(concrete = false)
public interface WriteStream<T> extends StreamBase {

  /**
   * Set an exception handler on the write stream.
   *
   * @param handler  the exception handler
   * @return a reference to this, so the API can be used fluently
   */
  @Override
  WriteStream<T> exceptionHandler(@Nullable Handler<Throwable> handler);

  /**
   * Write some data to the stream.
   *
   * <p> The data is usually put on an internal write queue, and the write actually happens
   * asynchronously. To avoid running out of memory by putting too much on the write queue,
   * check the {@link #writeQueueFull} method before writing. This is done automatically if
   * using a {@link Pipe}.
   *
   * <p> When the {@code data} is moved from the queue to the actual medium, the returned
   * {@link Future} will be completed with the write result, e.g the future is succeeded
   * when a server HTTP response buffer is written to the socket and failed if the remote
   * client has closed the socket while the data was still pending for write.
   *
   * @param data  the data to write
   * @return a future completed with the write result
   */
  Future<Void> write(T data);

  /**
   * Ends the stream.
   * <p>
   * Once the stream has ended, it cannot be used any more.
   *
   * @return a future completed with the result
   */
  Future<Void> end();

  /**
   * Same as {@link #end()} but writes some data to the stream before ending.
   *
   * @implSpec The default default implementation calls sequentially {@link #write(Object)} then {@link #end()}
   * @apiNote Implementations might want to perform a single operation
   * @param data the data to write
   * @return a future completed with the result
   */
  default Future<Void> end(T data) {
    return write(data).compose(v -> end());
  }

  /**
   * Set the maximum size of the write queue to {@code maxSize}. You will still be able to write to the stream even
   * if there is more than {@code maxSize} items in the write queue. This is used as an indicator by classes such as
   * {@link Pipe} to provide flow control.
   * <p/>
   * The value is defined by the implementation of the stream, e.g in bytes for a
   * {@link io.vertx.core.net.NetSocket}, etc...
   *
   * @param maxSize  the max size of the write stream
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  WriteStream<T> setWriteQueueMaxSize(int maxSize);

  /**
   * This will return {@code true} if there are more bytes in the write queue than the value set using {@link
   * #setWriteQueueMaxSize}
   *
   * @return {@code true} if write queue is full
   */
  boolean writeQueueFull();

  /**
   * Set a drain handler on the stream. If the write queue is full, then the handler will be called when the write
   * queue is ready to accept buffers again. See {@link Pipe} for an example of this being used.
   *
   * <p> The stream implementation defines when the drain handler, for example it could be when the queue size has been
   * reduced to {@code maxSize / 2}.
   *
   * @param handler the handler
   * @return a reference to this, so the API can be used fluently
   */
  @Fluent
  WriteStream<T> drainHandler(@Nullable Handler<Void> handler);

}
