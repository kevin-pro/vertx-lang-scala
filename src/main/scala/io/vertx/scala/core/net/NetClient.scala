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

package io.vertx.scala.core.net

import io.vertx.lang.scala.HandlerOps._
import scala.compat.java8.FunctionConverters._
import scala.collection.JavaConverters._
import io.vertx.scala.core.metrics.Measured
import io.vertx.core.Handler

/**
  * A TCP client.
  * 
  * Multiple connections to different servers can be made using the same instance.
  * 
  * This client supports a configurable number of connection attempts and a configurable
  * delay between attempts.
  */
class NetClient(private val _asJava: io.vertx.core.net.NetClient) 
    extends io.vertx.scala.core.metrics.Measured {

  def asJava: io.vertx.core.net.NetClient = _asJava

  /**
    * Whether the metrics are enabled for this measured object
    * @return true if the metrics are enabled
    */
  def isMetricsEnabled(): Boolean = {
    _asJava.isMetricsEnabled()
  }

  /**
    * Open a connection to a server at the specific `port` and `host`.
    * 
    * `host` can be a valid host name or IP address. The connect is done asynchronously and on success, a
    * [[io.vertx.scala.core.net.NetSocket]] instance is supplied via the `connectHandler` instance
    * @param port the port
    * @param host the host
    * @return a reference to this, so the API can be used fluently
    */
  def connectWithHandler(port: Int, host: String)( connectHandler: io.vertx.core.AsyncResult [io.vertx.scala.core.net.NetSocket] => Unit): io.vertx.scala.core.net.NetClient = {
    _asJava.connect(port, host, funcToMappedHandler[io.vertx.core.AsyncResult[io.vertx.core.net.NetSocket], io.vertx.core.AsyncResult [io.vertx.scala.core.net.NetSocket]](x => io.vertx.lang.scala.AsyncResult[io.vertx.core.net.NetSocket, io.vertx.scala.core.net.NetSocket](x,(x => if (x == null) null else NetSocket.apply(x))))(connectHandler))
    this
  }

  /**
    * Close the client.
    * 
    * Any sockets which have not been closed manually will be closed here. The close is asynchronous and may not
    * complete until some time after the method has returned.
    */
  def close(): Unit = {
    _asJava.close()
  }

}

object NetClient {

  def apply(_asJava: io.vertx.core.net.NetClient): io.vertx.scala.core.net.NetClient =
    new io.vertx.scala.core.net.NetClient(_asJava)

}
