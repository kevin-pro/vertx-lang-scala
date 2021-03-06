/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.scala.core.file

import io.vertx.lang.scala.HandlerOps._
import scala.compat.java8.FunctionConverters._
import scala.collection.JavaConverters._
import io.vertx.scala.core.buffer.Buffer
import io.vertx.scala.core.streams.WriteStream
import io.vertx.scala.core.streams.ReadStream
import io.vertx.core.Handler

/**
  * Represents a file on the file-system which can be read from, or written to asynchronously.
  * 
  * This class also implements [[io.vertx.scala.core.streams.ReadStream]] and
  * [[io.vertx.scala.core.streams.WriteStream]]. This allows the data to be pumped to and from
  * other streams, e.g. an [[io.vertx.scala.core.http.HttpClientRequest]] instance,
  * using the [[io.vertx.scala.core.streams.Pump]] class
  */
class AsyncFile(private val _asJava: io.vertx.core.file.AsyncFile) 
    extends io.vertx.scala.core.streams.ReadStream[io.vertx.scala.core.buffer.Buffer] 
    with io.vertx.scala.core.streams.WriteStream[io.vertx.scala.core.buffer.Buffer] {

  def asJava: io.vertx.core.file.AsyncFile = _asJava

  /**
    * Same as [[io.vertx.scala.core.file.AsyncFile#end]] but writes some data to the stream before ending.
    */
  def end(t: io.vertx.scala.core.buffer.Buffer): Unit = {
    _asJava.end(t.asJava.asInstanceOf[io.vertx.core.buffer.Buffer])
  }

  /**
    * This will return `true` if there are more bytes in the write queue than the value set using [[io.vertx.scala.core.file.AsyncFile#setWriteQueueMaxSize]]
    * @return true if write queue is full
    */
  def writeQueueFull(): Boolean = {
    _asJava.writeQueueFull()
  }

  def handler(handler: io.vertx.scala.core.buffer.Buffer => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.handler(funcToMappedHandler(Buffer.apply)(handler))
    this
  }

  def pause(): io.vertx.scala.core.file.AsyncFile = {
    _asJava.pause()
    this
  }

  def resume(): io.vertx.scala.core.file.AsyncFile = {
    _asJava.resume()
    this
  }

  def endHandler(endHandler: () => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.endHandler(funcToMappedHandler[java.lang.Void, Unit](x => x.asInstanceOf[Unit])(_ => endHandler()))
    this
  }

  def write(data: io.vertx.scala.core.buffer.Buffer): io.vertx.scala.core.file.AsyncFile = {
    _asJava.write(data.asJava.asInstanceOf[io.vertx.core.buffer.Buffer])
    this
  }

  def setWriteQueueMaxSize(maxSize: Int): io.vertx.scala.core.file.AsyncFile = {
    _asJava.setWriteQueueMaxSize(maxSize)
    this
  }

  def drainHandler(handler: () => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.drainHandler(funcToMappedHandler[java.lang.Void, Unit](x => x.asInstanceOf[Unit])(_ => handler()))
    this
  }

  def exceptionHandler(handler: Throwable => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.exceptionHandler(funcToMappedHandler[java.lang.Throwable, Throwable](x => x)(handler))
    this
  }

  /**
    * Close the file, see [[io.vertx.scala.core.file.AsyncFile#close]].
    */
  def end(): Unit = {
    _asJava.end()
  }

  /**
    * Close the file. The actual close happens asynchronously.
    */
  def close(): Unit = {
    _asJava.close()
  }

  /**
    * Close the file. The actual close happens asynchronously.
    * The handler will be called when the close is complete, or an error occurs.
    * @param handler the handler
    */
  def close(handler: io.vertx.core.AsyncResult [Unit] => Unit): Unit = {
    _asJava.close(funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.Void], io.vertx.core.AsyncResult [Unit]](x => io.vertx.lang.scala.AsyncResult[java.lang.Void, Unit](x,(x => ())))(handler))
  }

  /**
    * Write a [[io.vertx.scala.core.buffer.Buffer]] to the file at position `position` in the file, asynchronously.
    * 
    * If `position` lies outside of the current size
    * of the file, the file will be enlarged to encompass it.
    * 
    * When multiple writes are invoked on the same file
    * there are no guarantees as to order in which those writes actually occur
    * 
    * The handler will be called when the write is complete, or if an error occurs.
    * @param buffer the buffer to write
    * @param position the position in the file to write it at
    * @param handler the handler to call when the write is complete
    * @return a reference to this, so the API can be used fluently
    */
  def writeWithHandler(buffer: io.vertx.scala.core.buffer.Buffer, position: Long)( handler: io.vertx.core.AsyncResult [Unit] => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.write(buffer.asJava.asInstanceOf[io.vertx.core.buffer.Buffer], position, funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.Void], io.vertx.core.AsyncResult [Unit]](x => io.vertx.lang.scala.AsyncResult[java.lang.Void, Unit](x,(x => ())))(handler))
    this
  }

  /**
    * Reads `length` bytes of data from the file at position `position` in the file, asynchronously.
    * 
    * The read data will be written into the specified `Buffer buffer` at position `offset`.
    * 
    * If data is read past the end of the file then zero bytes will be read.
    * When multiple reads are invoked on the same file there are no guarantees as to order in which those reads actually occur.
    * 
    * The handler will be called when the close is complete, or if an error occurs.
    * @param buffer the buffer to read into
    * @param offset the offset into the buffer where the data will be read
    * @param position the position in the file where to start reading
    * @param length the number of bytes to read
    * @param handler the handler to call when the write is complete
    * @return a reference to this, so the API can be used fluently
    */
  def readWithHandler(buffer: io.vertx.scala.core.buffer.Buffer, offset: Int, position: Long, length: Int)( handler: io.vertx.core.AsyncResult [io.vertx.scala.core.buffer.Buffer] => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.read(buffer.asJava.asInstanceOf[io.vertx.core.buffer.Buffer], offset, position, length, funcToMappedHandler[io.vertx.core.AsyncResult[io.vertx.core.buffer.Buffer], io.vertx.core.AsyncResult [io.vertx.scala.core.buffer.Buffer]](x => io.vertx.lang.scala.AsyncResult[io.vertx.core.buffer.Buffer, io.vertx.scala.core.buffer.Buffer](x,(x => if (x == null) null else Buffer.apply(x))))(handler))
    this
  }

  /**
    * Flush any writes made to this file to underlying persistent storage.
    * 
    * If the file was opened with `flush` set to `true` then calling this method will have no effect.
    * 
    * The actual flush will happen asynchronously.
    * @return a reference to this, so the API can be used fluently
    */
  def flush(): io.vertx.scala.core.file.AsyncFile = {
    _asJava.flush()
    this
  }

  /**
    * Same as [[io.vertx.scala.core.file.AsyncFile#flush]] but the handler will be called when the flush is complete or if an error occurs
    */
  def flush(handler: io.vertx.core.AsyncResult [Unit] => Unit): io.vertx.scala.core.file.AsyncFile = {
    _asJava.flush(funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.Void], io.vertx.core.AsyncResult [Unit]](x => io.vertx.lang.scala.AsyncResult[java.lang.Void, Unit](x,(x => ())))(handler))
    this
  }

  /**
    * Sets the position from which data will be read from when using the file as a [[io.vertx.scala.core.streams.ReadStream]].
    * @param readPos the position in the file
    * @return a reference to this, so the API can be used fluently
    */
  def setReadPos(readPos: Long): io.vertx.scala.core.file.AsyncFile = {
    _asJava.setReadPos(readPos)
    this
  }

  /**
    * Sets the position from which data will be written when using the file as a [[io.vertx.scala.core.streams.WriteStream]].
    * @param writePos the position in the file
    * @return a reference to this, so the API can be used fluently
    */
  def setWritePos(writePos: Long): io.vertx.scala.core.file.AsyncFile = {
    _asJava.setWritePos(writePos)
    this
  }

  /**
    * Sets the buffer size that will be used to read the data from the file. Changing this value will impact how much
    * the data will be read at a time from the file system.
    * @param readBufferSize the buffer size
    * @return a reference to this, so the API can be used fluently
    */
  def setReadBufferSize(readBufferSize: Int): io.vertx.scala.core.file.AsyncFile = {
    _asJava.setReadBufferSize(readBufferSize)
    this
  }

}

object AsyncFile {

  def apply(_asJava: io.vertx.core.file.AsyncFile): io.vertx.scala.core.file.AsyncFile =
    new io.vertx.scala.core.file.AsyncFile(_asJava)

}
