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

package io.vertx.scala.core

import io.vertx.lang.scala.HandlerOps._
import scala.compat.java8.FunctionConverters._
import scala.collection.JavaConverters._
import io.vertx.scala.core.datagram.DatagramSocket
import io.vertx.scala.core.http.HttpServer
import io.vertx.scala.core.shareddata.SharedData
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.scala.core.net.NetClient
import io.vertx.core.VertxOptions
import io.vertx.core.net.NetClientOptions
import io.vertx.scala.core.dns.DnsClient
import io.vertx.core.net.NetServerOptions
import io.vertx.scala.core.metrics.Measured
import io.vertx.scala.core.net.NetServer
import io.vertx.core.DeploymentOptions
import io.vertx.scala.core.file.FileSystem
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.Handler
import io.vertx.scala.core.http.HttpClient

/**
  * The entry point into the Vert.x Core API.
  * 
  * You use an instance of this class for functionality including:
  * <ul>
  *   <li>Creating TCP clients and servers</li>
  *   <li>Creating HTTP clients and servers</li>
  *   <li>Creating DNS clients</li>
  *   <li>Creating Datagram sockets</li>
  *   <li>Setting and cancelling periodic and one-shot timers</li>
  *   <li>Getting a reference to the event bus API</li>
  *   <li>Getting a reference to the file system API</li>
  *   <li>Getting a reference to the shared data API</li>
  *   <li>Deploying and undeploying verticles</li>
  * </ul>
  * 
  * Most functionality in Vert.x core is fairly low level.
  * 
  * To create an instance of this class you can use the static factory methods: [[io.vertx.scala.core.Vertx#vertx]],
  * [[io.vertx.scala.core.Vertx#vertx]] and [[io.vertx.scala.core.Vertx#clusteredVertx]].
  * 
  * Please see the user manual for more detailed usage information.
  */
class Vertx(private val _asJava: io.vertx.core.Vertx) 
    extends io.vertx.scala.core.metrics.Measured {

  def asJava: io.vertx.core.Vertx = _asJava

  /**
    * Whether the metrics are enabled for this measured object
    * @return true if the metrics are enabled
    */
  def isMetricsEnabled(): Boolean = {
    _asJava.isMetricsEnabled()
  }

  /**
    * Gets the current context, or creates one if there isn't one
    * @return The current context (created if didn't exist)
    */
  def getOrCreateContext(): io.vertx.scala.core.Context = {
    Context.apply(_asJava.getOrCreateContext())
  }

  /**
    * Create a TCP/SSL server using the specified options
    * @param options the options to usesee <a href="../../../../../../cheatsheet/NetServerOptions.html">NetServerOptions</a>
    * @return the server
    */
  def createNetServer(options: io.vertx.scala.core.net.NetServerOptions): io.vertx.scala.core.net.NetServer = {
    NetServer.apply(_asJava.createNetServer(options.asJava))
  }

  /**
    * Create a TCP/SSL server using default options
    * @return the server
    */
  def createNetServer(): io.vertx.scala.core.net.NetServer = {
    NetServer.apply(_asJava.createNetServer())
  }

  /**
    * Create a TCP/SSL client using the specified options
    * @param options the options to usesee <a href="../../../../../../cheatsheet/NetClientOptions.html">NetClientOptions</a>
    * @return the client
    */
  def createNetClient(options: io.vertx.scala.core.net.NetClientOptions): io.vertx.scala.core.net.NetClient = {
    NetClient.apply(_asJava.createNetClient(options.asJava))
  }

  /**
    * Create a TCP/SSL client using default options
    * @return the client
    */
  def createNetClient(): io.vertx.scala.core.net.NetClient = {
    NetClient.apply(_asJava.createNetClient())
  }

  /**
    * Create an HTTP/HTTPS server using the specified options
    * @param options the options to usesee <a href="../../../../../../cheatsheet/HttpServerOptions.html">HttpServerOptions</a>
    * @return the server
    */
  def createHttpServer(options: io.vertx.scala.core.http.HttpServerOptions): io.vertx.scala.core.http.HttpServer = {
    HttpServer.apply(_asJava.createHttpServer(options.asJava))
  }

  /**
    * Create an HTTP/HTTPS server using default options
    * @return the server
    */
  def createHttpServer(): io.vertx.scala.core.http.HttpServer = {
    HttpServer.apply(_asJava.createHttpServer())
  }

  /**
    * Create a HTTP/HTTPS client using the specified options
    * @param options the options to usesee <a href="../../../../../../cheatsheet/HttpClientOptions.html">HttpClientOptions</a>
    * @return the client
    */
  def createHttpClient(options: io.vertx.scala.core.http.HttpClientOptions): io.vertx.scala.core.http.HttpClient = {
    HttpClient.apply(_asJava.createHttpClient(options.asJava))
  }

  /**
    * Create a HTTP/HTTPS client using default options
    * @return the client
    */
  def createHttpClient(): io.vertx.scala.core.http.HttpClient = {
    HttpClient.apply(_asJava.createHttpClient())
  }

  /**
    * Create a datagram socket using the specified options
    * @param options the options to usesee <a href="../../../../../../cheatsheet/DatagramSocketOptions.html">DatagramSocketOptions</a>
    * @return the socket
    */
  def createDatagramSocket(options: io.vertx.scala.core.datagram.DatagramSocketOptions): io.vertx.scala.core.datagram.DatagramSocket = {
    DatagramSocket.apply(_asJava.createDatagramSocket(options.asJava))
  }

  /**
    * Create a datagram socket using default options
    * @return the socket
    */
  def createDatagramSocket(): io.vertx.scala.core.datagram.DatagramSocket = {
    DatagramSocket.apply(_asJava.createDatagramSocket())
  }

  /**
    * Get the filesystem object. There is a single instance of FileSystem per Vertx instance.
    * @return the filesystem object
    */
  def fileSystem(): io.vertx.scala.core.file.FileSystem = {
    if(cached_0 == null) {
      cached_0=    FileSystem.apply(_asJava.fileSystem())
    }
    cached_0
  }

  /**
    * Get the event bus object. There is a single instance of EventBus per Vertx instance.
    * @return the event bus object
    */
  def eventBus(): io.vertx.scala.core.eventbus.EventBus = {
    if(cached_1 == null) {
      cached_1=    EventBus.apply(_asJava.eventBus())
    }
    cached_1
  }

  /**
    * Create a DNS client to connect to a DNS server at the specified host and port
    * @param port the port
    * @param host the host
    * @return the DNS client
    */
  def createDnsClient(port: Int, host: String): io.vertx.scala.core.dns.DnsClient = {
    DnsClient.apply(_asJava.createDnsClient(port, host))
  }

  /**
    * Get the shared data object. There is a single instance of SharedData per Vertx instance.
    * @return the shared data object
    */
  def sharedData(): io.vertx.scala.core.shareddata.SharedData = {
    if(cached_2 == null) {
      cached_2=    SharedData.apply(_asJava.sharedData())
    }
    cached_2
  }

  /**
    * Set a one-shot timer to fire after `delay` milliseconds, at which point `handler` will be called with
    * the id of the timer.
    * @param delay the delay in milliseconds, after which the timer will fire
    * @param handler the handler that will be called with the timer ID when the timer fires
    * @return the unique ID of the timer
    */
  def setTimerWithHandler(delay: Long)( handler: Long => Unit): Long = {
    _asJava.setTimer(delay, funcToMappedHandler[java.lang.Long, Long](x => x)(handler))
  }

  /**
    * Returns a one-shot timer as a read stream. The timer will be fired after `delay` milliseconds after
    * the  has been called.
    * @param delay the delay in milliseconds, after which the timer will fire
    * @return the timer stream
    */
  def timerStream(delay: Long): io.vertx.scala.core.TimeoutStream = {
    TimeoutStream.apply(_asJava.timerStream(delay))
  }

  /**
    * Set a periodic timer to fire every `delay` milliseconds, at which point `handler` will be called with
    * the id of the timer.
    * @param delay the delay in milliseconds, after which the timer will fire
    * @param handler the handler that will be called with the timer ID when the timer fires
    * @return the unique ID of the timer
    */
  def setPeriodicWithHandler(delay: Long)( handler: Long => Unit): Long = {
    _asJava.setPeriodic(delay, funcToMappedHandler[java.lang.Long, Long](x => x)(handler))
  }

  /**
    * Returns a periodic timer as a read stream. The timer will be fired every `delay` milliseconds after
    * the  has been called.
    * @param delay the delay in milliseconds, after which the timer will fire
    * @return the periodic stream
    */
  def periodicStream(delay: Long): io.vertx.scala.core.TimeoutStream = {
    TimeoutStream.apply(_asJava.periodicStream(delay))
  }

  /**
    * Cancels the timer with the specified `id`.
    * @param id The id of the timer to cancel
    * @return true if the timer was successfully cancelled, or false if the timer does not exist.
    */
  def cancelTimer(id: Long): Boolean = {
    _asJava.cancelTimer(id)
  }

  /**
    * Puts the handler on the event queue for the current context so it will be run asynchronously ASAP after all
    * preceeding events have been handled.
    * @param action - a handler representing the action to execute
    */
  def runOnContext(action: () => Unit): Unit = {
    _asJava.runOnContext(funcToMappedHandler[java.lang.Void, Unit](x => x.asInstanceOf[Unit])(_ => action()))
  }

  /**
    * Stop the the Vertx instance and release any resources held by it.
    * 
    * The instance cannot be used after it has been closed.
    * 
    * The actual close is asynchronous and may not complete until after the call has returned.
    */
  def close(): Unit = {
    _asJava.close()
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#close]] but the completionHandler will be called when the close is complete
    * @param completionHandler The handler will be notified when the close is complete.
    */
  def close(completionHandler: io.vertx.core.AsyncResult [Unit] => Unit): Unit = {
    _asJava.close(funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.Void], io.vertx.core.AsyncResult [Unit]](x => io.vertx.lang.scala.AsyncResult[java.lang.Void, Unit](x,(x => ())))(completionHandler))
  }

  /**
    * Deploy a verticle instance given a name.
    * 
    * Given the name, Vert.x selects a  instance to use to instantiate the verticle.
    * 
    * For the rules on how factories are selected please consult the user manual.
    * @param name the name.
    */
  def deployVerticle(name: String): Unit = {
    _asJava.deployVerticle(name)
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#deployVerticle]] but the completionHandler will be notified when the deployment is complete.
    * 
    * If the deployment is successful the result will contain a String representing the unique deployment ID of the
    * deployment.
    * 
    * This deployment ID can subsequently be used to undeploy the verticle.
    * @param name The identifier
    * @param completionHandler a handler which will be notified when the deployment is complete
    */
  def deployVerticleWithHandler(name: String)( completionHandler: io.vertx.core.AsyncResult [String] => Unit): Unit = {
    _asJava.deployVerticle(name, funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.String], io.vertx.core.AsyncResult [String]](x => io.vertx.lang.scala.AsyncResult[java.lang.String, String](x,(x => x)))(completionHandler))
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#deployVerticle]] but <a href="../../../../../../cheatsheet/DeploymentOptions.html">DeploymentOptions</a> are provided to configure the
    * deployment.
    * @param name the name
    * @param options the deployment options.see <a href="../../../../../../cheatsheet/DeploymentOptions.html">DeploymentOptions</a>
    */
  def deployVerticle(name: String, options: io.vertx.scala.core.DeploymentOptions): Unit = {
    _asJava.deployVerticle(name, options.asJava)
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#deployVerticle]] but <a href="../../../../../../cheatsheet/DeploymentOptions.html">DeploymentOptions</a> are provided to configure the
    * deployment.
    * @param name the name
    * @param options the deployment options.see <a href="../../../../../../cheatsheet/DeploymentOptions.html">DeploymentOptions</a>
    * @param completionHandler a handler which will be notified when the deployment is complete
    */
  def deployVerticleWithHandler(name: String, options: io.vertx.scala.core.DeploymentOptions)( completionHandler: io.vertx.core.AsyncResult [String] => Unit): Unit = {
    _asJava.deployVerticle(name, options.asJava, funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.String], io.vertx.core.AsyncResult [String]](x => io.vertx.lang.scala.AsyncResult[java.lang.String, String](x,(x => x)))(completionHandler))
  }

  /**
    * Undeploy a verticle deployment.
    * 
    * The actual undeployment happens asynchronously and may not complete until after the method has returned.
    * @param deploymentID the deployment ID
    */
  def undeploy(deploymentID: String): Unit = {
    _asJava.undeploy(deploymentID)
  }

  /**
    * Like [[io.vertx.scala.core.Vertx #undeploy(String)]] but the completionHandler will be notified when the undeployment is complete.
    * @param deploymentID the deployment ID
    * @param completionHandler a handler which will be notified when the undeployment is complete
    */
  def undeployWithHandler(deploymentID: String)( completionHandler: io.vertx.core.AsyncResult [Unit] => Unit): Unit = {
    _asJava.undeploy(deploymentID, funcToMappedHandler[io.vertx.core.AsyncResult[java.lang.Void], io.vertx.core.AsyncResult [Unit]](x => io.vertx.lang.scala.AsyncResult[java.lang.Void, Unit](x,(x => ())))(completionHandler))
  }

  /**
    * Return a Set of deployment IDs for the currently deployed deploymentIDs.
    * @return Set of deployment IDs
    */
  def deploymentIDs(): Set[String] = {
    _asJava.deploymentIDs().asScala.map(x => x:String).toSet
  }

  /**
    * Is this Vert.x instance clustered?
    * @return true if clustered
    */
  def isClustered(): Boolean = {
    _asJava.isClustered()
  }

  /**
    * Safely execute some blocking code.
    * 
    * Executes the blocking code in the handler `blockingCodeHandler` using a thread from the worker pool.
    * 
    * When the code is complete the handler `resultHandler` will be called with the result on the original context
    * (e.g. on the original event loop of the caller).
    * 
    * A `Future` instance is passed into `blockingCodeHandler`. When the blocking code successfully completes,
    * the handler should call the [[io.vertx.scala.core.Future#complete]] or [[io.vertx.scala.core.Future#complete]] method, or the [[io.vertx.scala.core.Future#fail]]
    * method if it failed.
    * 
    * In the `blockingCodeHandler` the current context remains the original context and therefore any task
    * scheduled in the `blockingCodeHandler` will be executed on the this context and not on the worker thread.
    * @param blockingCodeHandler handler representing the blocking code to run
    * @param ordered if true then if executeBlocking is called several times on the same context, the executions for that context will be executed serially, not in parallel. if false then they will be no ordering guarantees
    * @param resultHandler handler that will be called when the blocking code is complete
    */
  def executeBlockingWithHandler[T](blockingCodeHandler: io.vertx.scala.core.Future[T] => Unit, ordered: Boolean)( resultHandler: io.vertx.core.AsyncResult [T] => Unit): Unit = {
    _asJava.executeBlocking(funcToMappedHandler(Future.apply[T])(blockingCodeHandler), ordered, funcToMappedHandler[io.vertx.core.AsyncResult[T], io.vertx.core.AsyncResult [T]](x => io.vertx.lang.scala.AsyncResult[T, T](x,(x => x)))(resultHandler))
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#executeBlocking]] called with ordered = true.
    */
  def executeBlockingWithHandler[T](blockingCodeHandler: io.vertx.scala.core.Future[T] => Unit)( resultHandler: io.vertx.core.AsyncResult [T] => Unit): Unit = {
    _asJava.executeBlocking(funcToMappedHandler(Future.apply[T])(blockingCodeHandler), funcToMappedHandler[io.vertx.core.AsyncResult[T], io.vertx.core.AsyncResult [T]](x => io.vertx.lang.scala.AsyncResult[T, T](x,(x => x)))(resultHandler))
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#createSharedWorkerExecutor]] but with the <a href="../../../../../../cheatsheet/VertxOptions.html">VertxOptions</a> `poolSize`.
    */
  def createSharedWorkerExecutor(name: String): io.vertx.scala.core.WorkerExecutor = {
    WorkerExecutor.apply(_asJava.createSharedWorkerExecutor(name))
  }

  /**
    * Like [[io.vertx.scala.core.Vertx#createSharedWorkerExecutor]] but with the <a href="../../../../../../cheatsheet/VertxOptions.html">VertxOptions</a> `maxExecuteTime`.
    */
  def createSharedWorkerExecutor(name: String, poolSize: Int): io.vertx.scala.core.WorkerExecutor = {
    WorkerExecutor.apply(_asJava.createSharedWorkerExecutor(name, poolSize))
  }

  /**
    * Create a named worker executor, the executor should be closed when it's not needed anymore to release
    * resources.<p/>
    *
    * This method can be called mutiple times with the same `name`. Executors with the same name will share
    * the same worker pool. The worker pool size and max execute time are set when the worker pool is created and
    * won't change after.
    *
    * The worker pool is released when all the [[io.vertx.scala.core.WorkerExecutor]] sharing the same name are closed.
    * @param name the name of the worker executor
    * @param poolSize the size of the pool
    * @param maxExecuteTime the value of max worker execute time, in ms
    * @return the named worker executor
    */
  def createSharedWorkerExecutor(name: String, poolSize: Int, maxExecuteTime: Long): io.vertx.scala.core.WorkerExecutor = {
    WorkerExecutor.apply(_asJava.createSharedWorkerExecutor(name, poolSize, maxExecuteTime))
  }

  /**
    * Set a default exception handler for [[io.vertx.scala.core.Context]], set on  at creation.
    * @param handler the exception handler
    * @return a reference to this, so the API can be used fluently
    */
  def exceptionHandler(handler: Throwable => Unit): io.vertx.scala.core.Vertx = {
    _asJava.exceptionHandler(funcToMappedHandler[java.lang.Throwable, Throwable](x => x)(handler))
    this
  }

  private var cached_0: io.vertx.scala.core.file.FileSystem = _
  private var cached_1: io.vertx.scala.core.eventbus.EventBus = _
  private var cached_2: io.vertx.scala.core.shareddata.SharedData = _
}

object Vertx {

  def apply(_asJava: io.vertx.core.Vertx): io.vertx.scala.core.Vertx =
    new io.vertx.scala.core.Vertx(_asJava)

  def vertx(): io.vertx.scala.core.Vertx = {
    Vertx.apply(io.vertx.core.Vertx.vertx())
  }

  def vertx(options: io.vertx.scala.core.VertxOptions): io.vertx.scala.core.Vertx = {
    Vertx.apply(io.vertx.core.Vertx.vertx(options.asJava))
  }

  def clusteredVertxWithHandler(options: io.vertx.scala.core.VertxOptions)( resultHandler: io.vertx.core.AsyncResult [io.vertx.scala.core.Vertx] => Unit): Unit = {
    io.vertx.core.Vertx.clusteredVertx(options.asJava, funcToMappedHandler[io.vertx.core.AsyncResult[io.vertx.core.Vertx], io.vertx.core.AsyncResult [io.vertx.scala.core.Vertx]](x => io.vertx.lang.scala.AsyncResult[io.vertx.core.Vertx, io.vertx.scala.core.Vertx](x,(x => if (x == null) null else Vertx.apply(x))))(resultHandler))
  }

  def currentContext(): scala.Option[io.vertx.scala.core.Context] = {
        scala.Option(Context.apply(io.vertx.core.Vertx.currentContext()))
  }

}
