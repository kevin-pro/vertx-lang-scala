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

package io.vertx.lang.scala.tck

import com.acme.scala.pkg.MyInterface
import io.vertx.codegen.testmodel._
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.{JsonArray, JsonObject}
import io.vertx.core.{Future, VertxException}
import io.vertx.lang.scala.json.Json
import io.vertx.lang.scala.json.Json.arr
import io.vertx.scala.codegen.testmodel
import io.vertx.scala.codegen.testmodel.{ConcreteHandlerUserTypeExtension, Factory, RefedInterface1, TestDataObject, TestInterface}
import org.junit.ComparisonFailure
import org.junit.runner.RunWith
import org.scalatest.concurrent.AsyncAssertions._
import org.scalatest.concurrent.AsyncAssertions.Waiter
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.DurationLong
import scala.language.postfixOps

/**
  * @author <a href="mailto:jochen.mader@codecentric.de">Jochen Mader</a
  */
@RunWith(classOf[JUnitRunner])
class ApiTest extends FlatSpec with Matchers {

  val obj = TestInterface(new TestInterfaceImpl())

  "testMethodWithBasicParams" should "work" in {
    obj.methodWithBasicParams(123, 12345, 1234567, 1265615234l, 12.345f, 12.34566d, true, 'X', "foobar")
  }

  "testMethodWithBasicBoxedParams" should "work" in {
    obj.methodWithBasicBoxedParams(123, 12345, 1234567, 1265615234l, 12.345f, 12.34566d, true, 'X')
  }

  "testMethodWithHandlerBasicTypes" should "work" in {
    obj.methodWithHandlerBasicTypesWithHandler(
      b => assert(123 == b))(
      s => assert(12345 == s))(
      i => assert(1234567 == i))(
      l => assert(1265615234l == l))(
      f => assert(12.345f == f))(
      d => assert(12.34566d == d))(
      b => assert(b))(
      c => assert('X' == c))(
      s => assert("quux!" == s)
    )
  }

  "testMethodWithHandlerAsyncResultBasicTypes" should "work" in {
    obj.methodWithHandlerAsyncResultByteWithHandler(false)(b => assert(123 == b.result()))
    obj.methodWithHandlerAsyncResultShortWithHandler(false)(s => assert(12345 == s.result()))
    obj.methodWithHandlerAsyncResultIntegerWithHandler(false)(i => assert(1234567 == i.result()))
    obj.methodWithHandlerAsyncResultLongWithHandler(false)(l => assert(1265615234l == l.result()))
    obj.methodWithHandlerAsyncResultFloatWithHandler(false)(f => assert(12.345f == f.result()))
    obj.methodWithHandlerAsyncResultDoubleWithHandler(false)(d => assert(12.34566d == d.result()))
    obj.methodWithHandlerAsyncResultBooleanWithHandler(false)(b => assert(true == b.result()))
    obj.methodWithHandlerAsyncResultCharacterWithHandler(false)(c => assert('X' == c.result()))
    obj.methodWithHandlerAsyncResultStringWithHandler(false)(s => assert("quux!" == s.result()))

    obj.methodWithHandlerAsyncResultByteWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultShortWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultIntegerWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultLongWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultFloatWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultDoubleWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultBooleanWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultCharacterWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))
    obj.methodWithHandlerAsyncResultStringWithHandler(true)(r => assert(r.cause().getMessage == "foobar!"))

  }

  "testMethodWithUserTypes" should "work" in {
    val refed = RefedInterface1(new RefedInterface1Impl())
    refed.setString("aardvarks")
    obj.methodWithUserTypes(refed)
  }


  "testObjectParam" should "work" in {
    obj.methodWithObjectParam("null", null)
    obj.methodWithObjectParam("string", "wibble")
    obj.methodWithObjectParam("true", true.asInstanceOf[Object])
    obj.methodWithObjectParam("false", false.asInstanceOf[Object])
    obj.methodWithObjectParam("long", 123.toLong.asInstanceOf[Object])
    obj.methodWithObjectParam("double", 123.456.asInstanceOf[Object])
    obj.methodWithObjectParam("JsonObject", Json.obj(("foo", "hello"), ("bar", 123)))
    val hello = "hello"
    obj.methodWithObjectParam("JsonObject", Json.obj(("foo", hello), ("bar", 123)))
    val foo = "foo"
    obj.methodWithObjectParam("JsonArray", arr(foo, "bar", "wib"))
  }

  "testDataObjectParam" should "work" in {
    obj.methodWithDataObjectParam(testmodel.TestDataObject.fromJson(Json.obj().put("bar",123).put("foo", "hello").put("wibble", 1.23)))
  }

  "testListOfDataObjectsParam" should "work" in {
    obj.methodWithListOfDataObjectsParam(mutable.Buffer(testmodel.TestDataObject.fromJson(Json.obj().put("bar",123).put("foo", "hello").put("wibble", 1.23)), testmodel.TestDataObject.fromJson(Json.obj().put("bar",123).put("foo", "world").put("wibble", 1.23))))
  }

  "testSetOfDataObjectsParam" should "work" in {
    obj.methodWithSetOfDataObjectsParam(Set(testmodel.TestDataObject.fromJson(Json.obj().put("bar",123).put("foo", "hello").put("wibble", 1.23)), testmodel.TestDataObject.fromJson(Json.obj().put("bar",123).put("foo", "world").put("wibble", 1.23))))
  }

  "testNullDataObjectParam" should "work" in {
      obj.methodWithNullDataObjectParam(None)
  }

  "testMethodWithHandlerDataObject" should "work" in {
    val dataObject = TestDataObject.fromJson(Json.obj().put("foo", "foo").put("bar", 123))

    val w = new Waiter
    obj.methodWithHandlerDataObject(it => {
      w {
        assert(dataObject.getFoo == it.getFoo)
        assert(dataObject.getBar == it.getBar)
      }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultDataObject" should "work" in {
    val dataObject = TestDataObject.fromJson(Json.obj().put("foo", "foo").put("bar",123))

    val w = new Waiter
    obj.methodWithHandlerAsyncResultDataObjectWithHandler(false)(result => {
      w {
        assert(result.succeeded())
        assert(!result.failed())
        val res = result.result()
        assert(dataObject.getFoo == res.getFoo)
        assert(dataObject.getBar == res.getBar)
        assert(null == result.cause())
      }
      w.dismiss()
    })
    w.await(timeout(50 millis))
    val w2 = new Waiter
    obj.methodWithHandlerAsyncResultDataObjectWithHandler(true)(result => {
      w2 {
        assert(result.failed())
        assert("foobar!" == result.cause().getMessage)
      }
      w2.dismiss()
    })
    w2.await(timeout(50 millis))
  }

  "testMethodWithHandlerStringReturn" should "work" in {
    val handler = obj.methodWithHandlerStringReturn("the-result")
    handler("the-result")
    def failed = false
    intercept[ComparisonFailure](
      handler("not-expected")
    )
  }

  "testMethodWithHandlerGenericReturn" should "work" in {
    obj.methodWithHandlerGenericReturn[String](res =>
      assert("the-result" == res))("the-result")
    obj.methodWithHandlerGenericReturn[TestInterface](res =>
      assert(obj == res))(obj)
  }

  "testMethodWithHandlerVertxGenReturn" should "work" in {
    val handler = obj.methodWithHandlerVertxGenReturn("wibble")
    handler(RefedInterface1(new RefedInterface1Impl().setString("wibble")))
  }

  "testMethodWithHandlerAsyncResultStringReturn" should "work" in {
    val succeedingHandler = obj.methodWithHandlerAsyncResultStringReturn("the-result", false)
    succeedingHandler(Future.succeededFuture("the-result"))
    intercept[ComparisonFailure](
      succeedingHandler(Future.succeededFuture("not-expected"))
    )

    val failingHandler = obj.methodWithHandlerAsyncResultStringReturn("an-error", true)
    failingHandler(Future.failedFuture("an-error"))
    intercept[ComparisonFailure](
      succeedingHandler(Future.succeededFuture("whatever"))
    )
  }

  "testMethodWithHandlerAsyncResultGenericReturn" should "work" in {
    val w = new Waiter
    def stringHandler = obj.methodWithHandlerAsyncResultGenericReturn[String](ar =>
       {w { assert("the-result" == ar.result()) };w.dismiss()})
    stringHandler(Future.succeededFuture("the-result"))
    w.await(timeout(50 millis))

    val w2 = new Waiter
    def objHandler = obj.methodWithHandlerAsyncResultGenericReturn[TestInterface](ar =>
      { w2 { assert(obj == ar.result())}; w2.dismiss()} )
    objHandler(Future.succeededFuture(obj))
    w2.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultVertxGenReturn" should "work" in {
    var handler = obj.methodWithHandlerAsyncResultVertxGenReturn("wibble", false)
    handler(Future.succeededFuture(RefedInterface1(new RefedInterface1Impl().setString("wibble"))))
    handler = obj.methodWithHandlerAsyncResultVertxGenReturn("oh-no", true)
    handler(Future.failedFuture("oh-no"))
  }

  "testMethodWithHandlerListAndSet" should "work" in {
    obj.methodWithHandlerListAndSetWithHandler(
      it => assert(List("foo", "bar", "wibble") == it))(
      it => assert(List(5, 12, 100) == it))(
      it => assert(Set("foo", "bar", "wibble") == it))(
      it => assert(Set(5, 12, 100) == it)
    )
  }

  "testMethodWithHandlerAsyncResultListAndSet" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListString(it => {w {assert(List("foo", "bar", "wibble").diff(it.result()).isEmpty)}; w.dismiss()})
    obj.methodWithHandlerAsyncResultListInteger(it => {w {assert(List(5, 12, 100).diff(it.result()).isEmpty)}; w.dismiss()})
    obj.methodWithHandlerAsyncResultSetString(it => {w {assert(Set("foo", "bar", "wibble").diff(it.result()).isEmpty)}; w.dismiss()})
    obj.methodWithHandlerAsyncResultSetInteger(it => {w {assert(Set(5, 12, 100).diff(it.result()).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis), dismissals(4))
  }

  "testMethodWithHandlerListVertxGen" should "work" in {
    obj.methodWithHandlerListVertxGen(it => assert(it.map(_.getString()) == List("foo", "bar")))
  }

  "testMethodWithHandlerListAbstractVertxGen" should "work" in {
    obj.methodWithHandlerListAbstractVertxGen(it => assert(it.map(_.getString()) == List("abstractfoo", "abstractbar")))
  }

  "testMethodWithHandlerAsyncResultListVertxGen" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListVertxGen(it => { w {assert(it.result().map(_.getString()) == ArrayBuffer("foo", "bar"))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListAbstractVertxGen" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListAbstractVertxGen(it => { w {assert(it.result().map(_.getString()) == ArrayBuffer("abstractfoo", "abstractbar"))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerSetVertxGen" should "work" in {
    obj.methodWithHandlerSetVertxGen(it => assert(it.map(_.getString()) == Set("bar", "foo")))
  }

  "testMethodWithHandlerSetAbstractVertxGen" should "work" in {
    obj.methodWithHandlerSetAbstractVertxGen(it => assert(it.map(_.getString()) == Set("abstractfoo", "abstractbar")))
  }

  "testMethodWithHandlerAsyncResultSetVertxGen" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetVertxGen(it => { w {assert(it.result().map(_.getString()) == Set("bar", "foo"))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultSetAbstractVertxGen" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetAbstractVertxGen(it => { w {assert(it.result().map(_.getString()) == Set("abstractbar", "abstractfoo"))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerListJsonObject" should "work" in {
    obj.methodWithHandlerListJsonObject(it => assert(it == List(Json.obj(("cheese", "stilton")), Json.obj(("socks", "tartan")))))
  }

  "testMethodWithHandlerListNullJsonObject" should "work" in {
    obj.methodWithHandlerListNullJsonObject(it => assert(it == List(null)))
  }

  "testMethodWithHandlerListComplexJsonObject" should "work" in {
    obj.methodWithHandlerListComplexJsonObject(it => assert(it == List(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))))))
  }

  "testMethodWithHandlerAsyncResultListJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListJsonObject(it => { w {assert(List(Json.obj(("cheese", "stilton")), Json.obj(("socks", "tartan"))).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListNullJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListNullJsonObject(it => { w {assert(List(null).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListComplexJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListComplexJsonObject(it => { w {assert(List(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue")))).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerSetJsonObject" should "work" in {
    obj.methodWithHandlerSetJsonObject(it => assert(it == Set(Json.obj(("cheese", "stilton")), Json.obj(("socks", "tartan")))))
  }

  "testMethodWithHandlerSetNullJsonObject" should "work" in {
    obj.methodWithHandlerSetNullJsonObject(it => assert(it == Set(null)))
  }

  "testMethodWithHandlerSetComplexJsonObject" should "work" in {
    obj.methodWithHandlerSetComplexJsonObject(it => assert(it == Set(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))))))
  }

  "testMethodWithHandlerAsyncResultSetJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetJsonObject(it => { w {assert(Set(Json.obj(("cheese", "stilton")), Json.obj(("socks", "tartan"))).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultSetNullJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetNullJsonObject(it => { w {assert(Set(null).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultSetComplexJsonObject" should "work" in {
    import scala.collection.JavaConversions._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetComplexJsonObject(it => { w {assert(Set(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue")))).sameElements(it.result()))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerListJsonArray" should "work" in {
    obj.methodWithHandlerListJsonArray(it => assert(it == List(arr("green", "blue"), arr("yellow", "purple"))))
  }

  "testMethodWithHandlerListNullJsonArray" should "work" in {
    obj.methodWithHandlerListNullJsonArray(it => assert(it == List(null)))
  }

  "testMethodWithHandlerListComplexJsonArray" should "work" in {
    obj.methodWithHandlerListComplexJsonArray(it => assert(it == List(arr(Json.obj(("foo", "hello"))), arr(Json.obj(("bar", "bye"))))))
  }

  "testMethodWithHandlerListDataObject" should "work" in {
    obj.methodWithHandlerListDataObject(it => {
      assert("String 1" == it.head.getFoo)
      assert(1 == it.head.getBar)
      assert(1.1 == it.head.getWibble)
      assert("String 2" == it(1).getFoo)
      assert(2 == it(1).getBar)
      assert(2.2 == it(1).getWibble)
    })
  }

  "testMethodWithHandlerNullListDataObject" should "work" in {
    obj.methodWithHandlerListNullDataObject(it => assert(List(null) == it))
  }


  "testMethodWithHandlerSetDataObject" should "work" in {
    var checkVar = 0
    obj.methodWithHandlerSetDataObject(it => {
      it.forall(td => {
        if ("String 1" == td.getFoo) {
          assert(1 == td.getBar)
          assert(1.1 == td.getWibble)
          checkVar += 1
        }
        else if ("String 2" == td.getFoo) {
          assert(2 == td.getBar)
          assert(2.2 == td.getWibble)
          checkVar -= 1
        }
        true
      })
    })
    assert(checkVar == 0)
  }

  "testMethodWithHandlerNullSetDataObject" should "work" in {
    obj.methodWithHandlerSetNullDataObject(it => assert(Set(null) == it))
  }

  "testMethodWithHandlerAsyncResultListJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListJsonArray(it => { w {assert(it.result().diff(List(arr("green", "blue"), arr("yellow", "purple"))).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListNullJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListNullJsonArray(it => { w {assert(it.result() == List(null))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListComplexJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListComplexJsonArray(it => { w {assert(it.result().diff(List(arr(Json.obj(("foo", "hello"))), arr(Json.obj(("bar", "bye"))))).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerSetJsonArray" should "work" in {
    obj.methodWithHandlerSetJsonArray(it => assert(it == Set(arr("green", "blue"), arr("yellow", "purple"))))
  }

  "testMethodWithHandlerSetNullJsonArray" should "work" in {
    obj.methodWithHandlerSetNullJsonArray(it => assert(it == Set(null)))
  }

  "testMethodWithHandlerSetComplexJsonArray" should "work" in {
    obj.methodWithHandlerSetComplexJsonArray(it => assert(it == Set(arr(Json.obj(("foo", "hello"))), arr(Json.obj(("bar", "bye"))))))
  }

  "testMethodWithHandlerAsyncResultSetJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetJsonArray(it => { w {assert(it.result().diff(Set(arr("green", "blue"), arr("yellow", "purple"))).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultNullSetJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetNullJsonArray(it => { w {assert(it.result() == Set(null))}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultSetComplexJsonArray" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetComplexJsonArray(it => { w {assert(it.result().diff(Set(arr(Json.obj(("foo", "hello"))), arr(Json.obj(("bar", "bye"))))).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultListDataObject" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListDataObject(it => {
      w {
        assert("String 1" == it.result()(0).getFoo)
        assert(1 == it.result()(0).getBar)
        assert(1.1 == it.result()(0).getWibble)

        assert("String 2" == it.result()(1).getFoo)
        assert(2 == it.result()(1).getBar)
        assert(2.2 == it.result()(1).getWibble)
      }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultNullListDataObject" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListNullDataObject(it => {
      w { assert(List(null) == it.result()) }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultSetDataObject" should "work" in {
    import collection.JavaConversions._
    val w = new Waiter
    var checkVar = 0
    obj.methodWithHandlerAsyncResultSetDataObject(it => {
      val coll = it.result()
      coll.forall(td => {
        if ("String 1" == td.getFoo) {
          assert(1 == td.getBar)
          assert(1.1 == td.getWibble)
          checkVar += 1
          w.dismiss()
        }
        else if ("String 2" == td.getFoo) {
          assert(2 == td.getBar)
          assert(2.2 == td.getWibble)
          checkVar -= 1
          w.dismiss()
        }
        true
      })
    })
    w.await(timeout(50 millis))
    assert(checkVar == 0)
  }


  "testMethodWithHandlerAsyncResultNullSetDataObject" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetNullDataObject(it => {
      w{ assert(Set(null) == it.result())}
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }


  "testMethodWithHandlerUserTypes" should "work" in {
    obj.methodWithHandlerUserTypes(it => assert(it.getString == "echidnas"))
  }

  "testMethodWithHandlerAsyncResultUserTypes" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultUserTypes(it => {
      w{assert(it.result.getString == "cheetahs")}
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithConcreteHandlerUserTypesSubtype" should "work" in {
    obj.methodWithConcreteHandlerUserTypeSubtype(Factory.createConcreteHandlerUserType(it => assert(it.getString == "echidnas")))
  }

  "testMethodWithAbstractHandlerUserTypesSubtype" should "work" in {
    obj.methodWithAbstractHandlerUserTypeSubtype(Factory.createAbstractHandlerUserType(it => assert(it.getString == "echidnas")))
  }

  "testMethodWithConcreteHandlerUserTypesSubtypeExtension" should "work" in {
    obj.methodWithConcreteHandlerUserTypeSubtypeExtension(
      new ConcreteHandlerUserTypeExtension(new io.vertx.codegen.testmodel.ConcreteHandlerUserTypeExtension() {
        override def handle(event: io.vertx.codegen.testmodel.RefedInterface1): Unit = {
          assert("echidnas" == event.getString)
        }
      }))
  }

  "testMethodWithHandlerVoid" should "work" in {
    obj.methodWithHandlerVoid(() => assert(true))
  }

  "testMethodWithHandlerAsyncResultVoid" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultVoidWithHandler(false)((res) => {
      w { assert(res.succeeded()) }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerAsyncResultVoidFails" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultVoidWithHandler(true)((res) => {
      w {
        assert(res.failed())
        assert(res.cause().getMessage == "foo!")
      }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithHandlerThrowable" should "work" in {
    obj.methodWithHandlerThrowable((res) => {
      assert(res.isInstanceOf[VertxException])
      assert("cheese!" == res.getMessage)
    })
  }

  "testMethodWithHandlerGenericUserType" should "work" in {
    obj.methodWithHandlerGenericUserTypeWithHandler[String]("string_value")((res) => {
      assert("string_value" == res.getValue())
    })
  }

  "testMethodWithHandlerAsyncResultGenericUserType" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultGenericUserTypeWithHandler[String]("string_value_2")((res) => {
      w {assert(res.result.getValue == "string_value_2") }
      w.dismiss()
    })
    w.await(timeout(50 millis))
  }

  "testMethodWithGenericParam" should "work" in {
    obj.methodWithGenericParam("String", "foo")
    obj.methodWithGenericParam("Ref", new RefedInterface1Impl().setString("bar"))
    obj.methodWithGenericParam("JsonObject", Json.obj(("foo", "hello"), ("bar", 123)))
    obj.methodWithGenericParam("JsonArray", arr("foo", "bar", "wib"))
  }

  "testMethodWithGenericHandler" should "work" in {
    obj.methodWithGenericHandlerWithHandler[String]("String")((res) => assert(res == "foo"))
    obj.methodWithGenericHandlerWithHandler[io.vertx.codegen.testmodel.RefedInterface1]("Ref")((res) => assert(res.getString == "bar"))
    obj.methodWithGenericHandlerWithHandler[JsonObject]("JsonObject")((res) => assert(res == Json.obj(("foo", "hello"), ("bar", 123))))
    obj.methodWithGenericHandlerWithHandler[JsonArray]("JsonArray")((res) => assert(res == arr("foo", "bar", "wib")))
    obj.methodWithGenericHandlerWithHandler[JsonObject]("JsonObjectComplex")((res) => assert(res == Json.obj(("outer", Json.obj(("foo", "hello"))), ("bar", arr("this", "that")))))
  }

  "testMethodWithGenericHandlerAsyncResult" should "work" in {
    val w = new Waiter
    obj.methodWithGenericHandlerAsyncResultWithHandler[String]("String")((res) => { w {assert(res.result() == "foo")}; w.dismiss()})
    obj.methodWithGenericHandlerAsyncResultWithHandler[io.vertx.codegen.testmodel.RefedInterface1]("Ref")((res) => { w {assert(res.result().getString == "bar")}; w.dismiss()})
    obj.methodWithGenericHandlerAsyncResultWithHandler[JsonObject]("JsonObject")((res) => { w { assert(res.result() == Json.obj(("foo", "hello"), ("bar", 123)))}; w.dismiss()})
    obj.methodWithGenericHandlerAsyncResultWithHandler[JsonArray]("JsonArray")((res) => { w { assert(res.result() == arr("foo", "bar", "wib"))}; w.dismiss()})
    obj.methodWithGenericHandlerAsyncResultWithHandler[JsonObject]("JsonObjectComplex")((res) => { w {assert(res.result() == Json.obj(("outer", Json.obj(("foo", "hello"))), ("bar", arr("this", "that"))))}; w.dismiss()})
    w.await(timeout(50 millis), dismissals(5))
  }

  "testMethodListParams" should "work" in {
    val refed1 = new RefedInterface1(new RefedInterface1Impl())
    refed1.setString("foo")
    val refed2 = new RefedInterface1(new RefedInterface1Impl())
    refed2.setString("bar")

    obj.methodWithListParams(
      mutable.Buffer("foo", "bar"),
      mutable.Buffer(2.toByte, 3.toByte),
      mutable.Buffer(12.toShort, 13.toShort),
      mutable.Buffer(1234, 1345),
      mutable.Buffer(123l, 456l),
      mutable.Buffer(Json.obj(("foo", "bar")), Json.obj(("eek", "wibble"))),
      mutable.Buffer(arr("foo"), arr("blah")),
      mutable.Buffer(refed1, refed2),
      mutable.Buffer(TestDataObject.fromJson(Json.obj().put("bar", 1).put("wibble", 1.1).put("foo", "String 1")), TestDataObject.fromJson(Json.obj().put("bar", 2).put("wibble", 2.2).put("foo", "String 2"))),
      mutable.Buffer(TestEnum.JULIEN, TestEnum.TIM))
  }

  "testMethodSetParams" should "work" in {
    val refed1 = new RefedInterface1(new RefedInterface1Impl())
    refed1.setString("foo")
    val refed2 = new RefedInterface1(new RefedInterface1Impl())
    refed2.setString("bar")

    obj.methodWithSetParams(
      Set("foo", "bar"),
      Set(2.toByte, 3.toByte),
      Set(12.toShort, 13.toShort),
      Set(1234, 1345),
      Set(123l, 456l),
      Set(Json.obj(("foo", "bar")), Json.obj(("eek", "wibble"))),
      Set(arr("foo"), arr("blah")),
      Set(refed1, refed2),
      Set(TestDataObject.fromJson(Json.obj().put("bar", 1).put("wibble", 1.1).put("foo", "String 1")), TestDataObject.fromJson(Json.obj().put("bar", 2).put("wibble", 2.2).put("foo", "String 2"))),
      Set(TestEnum.JULIEN, TestEnum.TIM))
  }

  "testMethodMapParams" should "work" in {
    val refed1 = new RefedInterface1(new RefedInterface1Impl())
    refed1.setString("foo")
    val refed2 = new RefedInterface1(new RefedInterface1Impl())
    refed2.setString("bar")
    obj.methodWithMapParams(
      Map("foo" -> "bar", "eek" -> "wibble"),
      Map("foo" -> 2.toByte, "eek" -> 3.toByte),
      Map("foo" -> 12.toShort, "eek" -> 13.toShort),
      Map("foo" -> 1234, "eek" -> 1345),
      Map("foo" -> 123l, "eek" -> 456l),
      Map("foo" -> Json.obj(("foo", "bar")), "eek" -> Json.obj(("eek", "wibble"))),
      Map("foo" -> arr("foo"), "eek" -> arr("blah")),
      Map("foo" -> refed1, "eek" -> refed2)
    )
  }

  "testMethodWithHandlerListEnum" should "work" in {
    obj.methodWithHandlerListEnum(it => assert(it == List(TestEnum.TIM, TestEnum.JULIEN)))
  }

  "testMethodWithHandlerSetEnum" should "work" in {
    obj.methodWithHandlerSetEnum(it => assert(it == Set(TestEnum.TIM, TestEnum.JULIEN)))
  }

  "testMethodWithHandlerAsyncResultListEnum" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultListEnum(it => { w{assert(it.result().toSet.diff(Set(TestEnum.TIM, TestEnum.JULIEN)).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis), dismissals(1))
  }

  "testMethodWithHandlerAsyncResultSetEnum" should "work" in {
    import collection.JavaConverters._
    val w = new Waiter
    obj.methodWithHandlerAsyncResultSetEnum(it => { w{assert(it.result().diff(Set(TestEnum.TIM, TestEnum.JULIEN)).isEmpty)}; w.dismiss()})
    w.await(timeout(50 millis), dismissals(1))
  }

  "testBasicReturns" should "work" in {
    assert(123 == obj.methodWithByteReturn())
    assert(12345 == obj.methodWithShortReturn())
    assert(12345464 == obj.methodWithIntReturn())
    assert(65675123 == obj.methodWithLongReturn())
    assert(1.23f == obj.methodWithFloatReturn())
    assert(3.34535 == obj.methodWithDoubleReturn())
    assert(obj.methodWithBooleanReturn())
    assert('Y' == obj.methodWithCharReturn())
    assert("orangutan" == obj.methodWithStringReturn())
  }

  "testVertxGenReturn" should "work" in {
    val r = obj.methodWithVertxGenReturn()
    assert("chaffinch" == r.getString())
  }

  "testVertxGenNullReturn" should "work" in {
    val r = obj.methodWithVertxGenNullReturn()
    assert(null == r.asJava)
  }

  "testVertxAbstractGenReturn" should "work" in {
    val r = obj.methodWithAbstractVertxGenReturn()
    assert("abstractchaffinch" == r.getString())
  }

  "testDataObjectReturn" should "work" in {
    val r = obj.methodWithDataObjectReturn()
    assert("foo" == r.getFoo)
    assert(123 == r.getBar)
  }

  "testDataObjectNullReturn" should "work" in {
    val r = obj.methodWithDataObjectNullReturn()
    assert(null == r)
  }

  "testListStringReturn" should "work" in {
    assert(List("foo", "bar", "wibble") == obj.methodWithListStringReturn())
  }

  "testListLongReturn" should "work" in {
    assert(List(123l, 456l) == obj.methodWithListLongReturn())
  }

  "testListJsonObjectReturn" should "work" in {
    val list = obj.methodWithListJsonObjectReturn()
    assert(2 == list.size)
    assert("bar" == list.head.getString("foo"))
    assert("eek" == list(1).getString("blah"))
  }

  "testListComplexJsonObjectReturn" should "work" in {
    val list = obj.methodWithListComplexJsonObjectReturn()
    assert(1 == list.size)
    val json1 = list.head
    assert(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))) == json1)
  }

  "testListJsonArrayReturn" should "work" in {
    var list = obj.methodWithListJsonArrayReturn()
    assert(2 == list.size)
    val json1 = list.head
    assert("foo" == json1.getString(0))
    val json2 = list(1)
    assert("blah" == json2.getString(0))
  }

  "testListComplexJsonArrayReturn" should "work" in {
    val list = obj.methodWithListComplexJsonArrayReturn()
    assert(2 == list.size)
    val json1 = list.head
    assert(arr(Json.obj(("foo", "hello"))) == json1)
    val json2 = list(1)
    assert(arr(Json.obj(("bar", "bye"))) == json2)
  }

  "testListVertxGenReturn" should "work" in {
    val list = obj.methodWithListVertxGenReturn()
    assert(2 == list.size)
    val refed1 = list.head
    val refed2 = list(1)
    assert("foo" == refed1.getString())
    assert("bar" == refed2.getString())
  }

  "testListDataObjectReturn" should "work" in {
    val list = obj.methodWithListDataObjectReturn()
    assert("String 1" == list.head.getFoo)
    assert(1 == list.head.getBar)
    assert(1.1 == list.head.getWibble)
    assert("String 2" == list(1).getFoo)
    assert(2 == list(1).getBar)
    assert(2.2 == list(1).getWibble)
  }

  "testSetStringReturn" should "work" in {
    assert(Set("foo", "bar", "wibble") == obj.methodWithSetStringReturn())
  }

  "testSetLongReturn" should "work" in {
    assert(Set(123l, 456l) == obj.methodWithSetLongReturn())
  }

  "testSetJsonObjectReturn" should "work" in {
    val set = obj.methodWithSetJsonObjectReturn()
    assert(2 == set.size)
    val json1 = Json.obj(("foo", "bar"))
    assert(set.contains(json1))
    val json2 = Json.obj(("blah", "eek"))
    assert(set.contains(json2))
  }


  "testSetComplexJsonObjectReturn" should "work" in {
    val set = obj.methodWithSetComplexJsonObjectReturn()
    assert(1 == set.size)
    assert(set.contains(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue")))))
  }

  "testSetJsonArrayReturn" should "work" in {
    val set = obj.methodWithSetJsonArrayReturn()
    assert(2 == set.size)
    assert(set.contains(Json.arr("foo")))
    assert(set.contains(Json.arr("blah")))
  }

  "testSetComplexJsonArrayReturn" should "work" in {
    val set = obj.methodWithSetComplexJsonArrayReturn()
    assert(2 == set.size)
    assert(set.contains(arr(Json.obj(("foo", "hello")))))
    assert(set.contains(arr(Json.obj(("bar", "bye")))))
  }

  "testSetVertxGenReturn" should "work" in {
    val set = obj.methodWithSetVertxGenReturn()
    assert(2 == set.size)
    assert(set.map(_.getString()).size == 2)
  }

  "testSetDataObjectReturn" should "work" in {
    var checkVar = 0
    val coll = obj.methodWithSetDataObjectReturn()
    val allok = coll.forall(td => {
      if ("String 1" == td.getFoo) {
        assert(1 == td.getBar)
        assert(1.1 == td.getWibble)
        checkVar += 1
        true
      }
      else if ("String 2" == td.getFoo) {
        assert(2 == td.getBar)
        assert(2.2 == td.getWibble)
        checkVar -= 1
        true
      }
      else
        false
    })

    assert(allok)
    assert(checkVar == 0)
  }

  "testMapStringReturn" should "work" in {
    val map = obj.methodWithMapStringReturn(a => {})
    assert("bar" == map("foo"))
  }

  "testMapLongReturn" should "work" in {
    val map = obj.methodWithMapLongReturn(a => {})
    assert(123l == map("foo"))
  }

  "testMapJsonObjectReturn" should "work" in {
    val map = obj.methodWithMapJsonObjectReturn(a => {})
    val m = map("foo")
    assert("eek" == m.getString("wibble"))
  }

  "testMapComplexJsonObjectReturn" should "work" in {
    val map = obj.methodWithMapComplexJsonObjectReturn(a => {})
    val m = map("foo")
    assert(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))) == m)
  }

  "testMapJsonArrayReturn" should "work" in {
    val map = obj.methodWithMapJsonArrayReturn(a => {})
    val m = map("foo")
    assert("wibble" == m.getString(0))
  }

  "testMapComplexJsonArrayReturn" should "work" in {
    val map = obj.methodWithMapComplexJsonArrayReturn(a => {})
    val m = map("foo")
    assert(arr(Json.obj(("foo", "hello")), Json.obj(("bar", "bye"))) == m)
  }

  "testOverloadedMethods" should "work" in {
    val refed = new RefedInterface1(new RefedInterface1Impl())
    refed.setString("dog")
    assert("meth1" == obj.overloadedMethod("cat", refed))
    var counter = 0
    assert("meth2" == obj.overloadedMethodWithHandler("cat", refed, 12345)(it => {
      assert("giraffe" == it); counter += 1
    }))
    assert(counter == 1)
    assert("meth3" == obj.overloadedMethodWithHandler("cat")(it => {
      assert("giraffe" == it); counter += 1
    }))
    assert(counter == 2)
    //TODO reenable!
//    assert("meth4" == obj.overloadedMethod("cat", refed)(it => {
//      assert("giraffe" == it); counter += 1
//    }))
//    assert(counter == 3)
  }

  "testSuperInterfaces" should "work" in {
    obj.methodWithBasicParams(123.toByte, 12345.toShort, 1234567, 1265615234l, 12.345f, 12.34566d, true, 'X', "foobar")
    obj.otherSuperMethodWithBasicParams(123.toByte, 12345.toShort, 1234567, 1265615234l, 12.345f, 12.34566d, true, 'X', "foobar")
  }

  "testMethodWithGenericReturn" should "work" in {
    val ret = obj.methodWithGenericReturn[JsonObject]("JsonObject")
    assert(Json.obj(("foo", "hello"), ("bar", 123)) == ret)
    val ret2 = obj.methodWithGenericReturn[JsonArray]("JsonArray")
    assert(arr("foo", "bar", "wib") == ret2)
  }

  "testFluentMethod" should "work" in {
    assert(obj.eq(obj.fluentMethod("bar")))
  }

  "testStaticFactoryMethod" should "work" in {
    assert("bar" == TestInterface.staticFactoryMethod("bar").getString())
  }
  "testMethodWithCachedReturn" should "work" in {
    val ret1 = obj.methodWithCachedReturn("bar")
    assert("bar" == ret1.getString())
    val ret2 = obj.methodWithCachedReturn("bar")
    assert(ret1.eq(ret2))
    val ret3 = obj.methodWithCachedReturn("bar")
    assert(ret1.eq(ret3))
  }

  "testMethodWithCachedListReturn" should "work" in {
    val ret1 = obj.methodWithCachedListReturn()
    assert(2 == ret1.size)
    assert("foo" == ret1.head.getString)
    assert("bar" == ret1(1).getString)
    val ret2 = obj.methodWithCachedListReturn()
    assert(ret1.eq(ret2))
    val ret3 = obj.methodWithCachedListReturn()
    assert(ret1.eq(ret3))
  }

  "testJsonReturns" should "work" in {
    val ret = obj.methodWithJsonObjectReturn()
    assert(Json.obj(("cheese", "stilton")) == ret)
    val ret2 = obj.methodWithJsonArrayReturn()
    assert(arr("socks", "shoes") == ret2)
  }

  "testNullJsonReturns" should "work" in {
    val ret = obj.methodWithNullJsonObjectReturn()
    assert(null == ret)
    val ret2 = obj.methodWithNullJsonObjectReturn()
    assert(null == ret)
  }

  "testComplexJsonReturns" should "work" in {
    val ret = obj.methodWithComplexJsonObjectReturn()
    assert(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))) == ret)
    val ret2 = obj.methodWithComplexJsonArrayReturn()
    assert(arr(Json.obj(("foo", "hello")), Json.obj(("bar", "bye"))) == ret2)
  }

  "testJsonParams" should "work" in {
    obj.methodWithJsonParams(Json.obj(("cat", "lion"), ("cheese", "cheddar")), arr("house", "spider"))
  }

  "testNullJsonParams" should "work" in {
    obj.methodWithNullJsonParams(null, null)
  }

  "testJsonHandlerParams" should "work" in {
    var count = 0
    obj.methodWithHandlerJsonWithHandler(it => {
      assert(Json.obj(("cheese", "stilton")) == it)
      count += 1
    })( it => {
      assert(arr("socks", "shoes") == it)
      count -= 1
    })
    assert(0 == count)
  }

  "testNullJsonHandlerParams" should "work" in {
    var count = 0
    obj.methodWithHandlerNullJsonWithHandler(it => {
      assert(null == it)
      count += 1
    })( it => {
      assert(null == it)
      count -= 1
    })
    assert(0 == count)
  }

  "testComplexJsonHandlerParams" should "work" in {
    var count = 0
    obj.methodWithHandlerComplexJsonWithHandler(it => {
      assert(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))) == it)
      count += 1
    })( it => {
      assert(arr(arr(Json.obj(("foo", "hello"))), arr(Json.obj(("bar", "bye")))) == it)
      count -= 1
    })
    assert(0 == count)
  }

  "testJsonHandlerAsyncResultParams" should "work" in {
    val w = new Waiter
    obj.methodWithHandlerAsyncResultJsonObject(it => {
      w{assert(Json.obj(("cheese", "stilton")) == it.result())}
      w.dismiss()
    })
    w.await(timeout(50 millis))
    val w2 = new Waiter
    obj.methodWithHandlerAsyncResultJsonArray(it => {
      w2{assert(arr("socks", "shoes") == it.result())}
      w2.dismiss()
    })
    w2.await(timeout(50 millis))
  }

  "testNullJsonHandlerAsyncResultParams" should "work" in {
    val w = new Waiter

    obj.methodWithHandlerAsyncResultNullJsonObject(it => {
      w{assert(null == it.result())}
      w.dismiss()
    })

    w.await(timeout(50 millis))
    val w2 = new Waiter
    obj.methodWithHandlerAsyncResultNullJsonArray(it => {
      w2{assert(null == it.result())}
      w2.dismiss()
    })

    w2.await(timeout(50 millis))
  }

  "testComplexJsonHandlerAsyncResultParams" should "work" in {
    val w = new Waiter

    obj.methodWithHandlerAsyncResultComplexJsonObject(it => {
      w{assert(Json.obj(("outer", Json.obj(("socks", "tartan"))), ("list", arr("yellow", "blue"))) == it.result())}
      w.dismiss()
    })

    w.await(timeout(50 millis))
    val w2 = new Waiter
    obj.methodWithHandlerAsyncResultComplexJsonArray(it => {
      w{assert(arr(Json.obj(("foo", "hello")), Json.obj(("bar", "bye"))) == it.result())}
      w2.dismiss()
    })

    w2.await(timeout(50 millis))
  }

  "testMethodWithListEnumReturn" should "work" in {
    assert(List(TestEnum.JULIEN, TestEnum.TIM) == obj.methodWithListEnumReturn())
  }

  "testMethodWithSetEnumReturn" should "work" in {
    assert(Set(TestEnum.JULIEN, TestEnum.TIM) == obj.methodWithSetEnumReturn())
  }

  "testMethodWithEnumParam" should "work" in {
    val ret = obj.methodWithEnumParam("cabbages", TestEnum.JULIEN)
    assert("cabbagesJULIEN" == ret)
  }

  "testMethodWithThrowableParam" should "work" in {
    val ret = obj.methodWithThrowableParam(new Exception("the_exception"))
    assert("the_exception" == ret)
  }

  "testMethodWithEnumReturn" should "work" in {
    val ret = obj.methodWithEnumReturn("JULIEN")
    assert(TestEnum.JULIEN == ret)
  }

  "testMethodWithThrowableReturn" should "work" in {
    val ret = obj.methodWithThrowableReturn("bogies")
    assert("bogies" == ret.getMessage)
  }

  "testCustomModule" should "work" in {
    val my = MyInterface.create()
    val testInterface = my.method()
    testInterface.methodWithBasicParams(123.toByte, 12345.toShort, 1234567, 1265615234l, 12.345f, 12.34566d, true, 'X', "foobar")
    val sub = my.sub()
    assert("olleh" == sub.reverse("hello"))
  }

  //NullableTCK

  val nullableTCK = testmodel.NullableTCK(new NullableTCKImpl)

  "testNullableByte" should "work" in {
    val testByte = 67.toByte
    nullableTCK.methodWithNullableByteParam(true, None)
    nullableTCK.methodWithNullableByteParam(false, Option(testByte))
    nullableTCK.methodWithNullableByteHandlerWithHandler(true)(b => assert(testByte == b))
    nullableTCK.methodWithNullableByteHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableByteHandlerAsyncResultWithHandler(true)(b => {w{assert(testByte == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableByteHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableByteReturn(true)
    nullableTCK.methodWithNullableByteReturn(false)
  }

  "testNullableShort" should "work" in {
    val testShort = 1024.toShort
    nullableTCK.methodWithNullableShortParam(true, None)
    nullableTCK.methodWithNullableShortParam(false, Option(testShort))
    nullableTCK.methodWithNullableShortHandlerWithHandler(true)(b => assert(testShort == b))
    nullableTCK.methodWithNullableShortHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableShortHandlerAsyncResultWithHandler(true)(b => {w{assert(testShort == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableShortHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableShortReturn(true)
    nullableTCK.methodWithNullableShortReturn(false)
  }

  "testNullableInteger" should "work" in {
    val testInteger = 1234567
    nullableTCK.methodWithNullableIntegerParam(true, None)
    nullableTCK.methodWithNullableIntegerParam(false, Option(testInteger))
    nullableTCK.methodWithNullableIntegerHandlerWithHandler(true)(b => assert(testInteger == b))
    nullableTCK.methodWithNullableIntegerHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableIntegerHandlerAsyncResultWithHandler(true)(b => {w{assert(testInteger == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableIntegerHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableIntegerReturn(true)
    nullableTCK.methodWithNullableIntegerReturn(false)
  }

  "testNullableLong" should "work" in {
    val testLong = 9876543210l
    nullableTCK.methodWithNullableLongParam(true, None)
    nullableTCK.methodWithNullableLongParam(false, Option(testLong))
    nullableTCK.methodWithNullableLongHandlerWithHandler(true)(b => assert(testLong == b))
    nullableTCK.methodWithNullableLongHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableLongHandlerAsyncResultWithHandler(true)(b => {w{assert(testLong == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableLongHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableLongReturn(true)
    nullableTCK.methodWithNullableLongReturn(false)
  }

  "testNullableFloat" should "work" in {
    val testFloat = 3.14.toFloat
    nullableTCK.methodWithNullableFloatParam(true, None)
    nullableTCK.methodWithNullableFloatParam(false, Option(testFloat))
    nullableTCK.methodWithNullableFloatHandlerWithHandler(true)(b => assert(testFloat == b))
    nullableTCK.methodWithNullableFloatHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableFloatHandlerAsyncResultWithHandler(true)(b => {w{assert(testFloat == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableFloatHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableFloatReturn(true)
    nullableTCK.methodWithNullableFloatReturn(false)
  }

  "testNullableDouble" should "work" in {
    val testDouble = 3.1415926
    nullableTCK.methodWithNullableDoubleParam(true, None)
    nullableTCK.methodWithNullableDoubleParam(false, Option(testDouble))
    nullableTCK.methodWithNullableDoubleHandlerWithHandler(true)(b => assert(testDouble == b))
    nullableTCK.methodWithNullableDoubleHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableDoubleHandlerAsyncResultWithHandler(true)(b => {w{assert(testDouble == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableDoubleHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableDoubleReturn(true)
    nullableTCK.methodWithNullableDoubleReturn(false)
  }

  "testNullableBoolean" should "work" in {
    val testBoolean = true
    nullableTCK.methodWithNullableBooleanParam(true, None)
    nullableTCK.methodWithNullableBooleanParam(false, Option(testBoolean))
    nullableTCK.methodWithNullableBooleanHandlerWithHandler(true)(b => assert(testBoolean == b))
    nullableTCK.methodWithNullableBooleanHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableBooleanHandlerAsyncResultWithHandler(true)(b => {w{assert(testBoolean == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableBooleanHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableBooleanReturn(true)
    nullableTCK.methodWithNullableBooleanReturn(false)
  }

  "testNullableChar" should "work" in {
    val testChar = 'f'
    nullableTCK.methodWithNullableCharParam(true, None)
    nullableTCK.methodWithNullableCharParam(false, Option(testChar))
    nullableTCK.methodWithNullableCharHandlerWithHandler(true)(b => assert(testChar == b))
    nullableTCK.methodWithNullableCharHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableCharHandlerAsyncResultWithHandler(true)(b => {w{assert(testChar == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableCharHandlerAsyncResultWithHandler(false)(b => {w2{intercept[NullPointerException](null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableCharReturn(true)
    nullableTCK.methodWithNullableCharReturn(false)
  }

  "testNullableJsonObject" should "work" in {
    val testJsonObject = Json.obj(("foo","wibble"),("bar",3))
    nullableTCK.methodWithNullableJsonObjectParam(true, None)
    nullableTCK.methodWithNullableJsonObjectParam(false, Option(testJsonObject))
    nullableTCK.methodWithNullableJsonObjectHandlerWithHandler(true)(b => assert(testJsonObject == b))
    nullableTCK.methodWithNullableJsonObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableJsonObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testJsonObject == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableJsonObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableJsonObjectReturn(true)
    nullableTCK.methodWithNullableJsonObjectReturn(false)
  }

  "testNullableJsonArray" should "work" in {
    val testJsonArray = Json.arr("one","two","three")
    nullableTCK.methodWithNullableJsonArrayParam(true, None)
    nullableTCK.methodWithNullableJsonArrayParam(false, Option(testJsonArray))
    nullableTCK.methodWithNullableJsonArrayHandlerWithHandler(true)(b => assert(testJsonArray == b))
    nullableTCK.methodWithNullableJsonArrayHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableJsonArrayHandlerAsyncResultWithHandler(true)(b => {w{assert(testJsonArray == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableJsonArrayHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableJsonArrayReturn(true)
    nullableTCK.methodWithNullableJsonArrayReturn(false)
  }

  "testNullableApi" should "work" in {
    val testApi = RefedInterface1(new RefedInterface1Impl().setString("lovely_dae"))
    nullableTCK.methodWithNullableApiParam(true, None)
    nullableTCK.methodWithNullableApiParam(false, Option(testApi))
    nullableTCK.methodWithNullableApiHandlerWithHandler(true)(b => assert(testApi.asJava == b.asJava))
    nullableTCK.methodWithNullableApiHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableApiHandlerAsyncResultWithHandler(true)(b => {w{assert(testApi.asJava == b.result().asJava)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableApiHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableApiReturn(true)
    nullableTCK.methodWithNullableApiReturn(false)
  }

  "testNullableDataObject" should "work" in {
    val testDataObject = TestDataObject.fromJson(Json.obj(("foo","foo_value"), ("bar",12345), ("wibble", 3.5)))
    nullableTCK.methodWithNullableDataObjectParam(true, None)
    nullableTCK.methodWithNullableDataObjectParam(false, Option(testDataObject))
    nullableTCK.methodWithNullableDataObjectHandlerWithHandler(true)(b => assert(testDataObject.asJava.toJson == b.asJava.toJson))
    nullableTCK.methodWithNullableDataObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableDataObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testDataObject.asJava.toJson == b.result().asJava.toJson)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableDataObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableDataObjectReturn(true)
    nullableTCK.methodWithNullableDataObjectReturn(false)
  }

  "testNullableEnum" should "work" in {
    val testEnum = TestEnum.TIM
    nullableTCK.methodWithNullableEnumParam(true, None)
    nullableTCK.methodWithNullableEnumParam(false, Option(testEnum))
    nullableTCK.methodWithNullableEnumHandlerWithHandler(true)(b => assert(testEnum == b))
    nullableTCK.methodWithNullableEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testEnum == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableEnumReturn(true)
    nullableTCK.methodWithNullableEnumReturn(false)
  }

  "testNullableGenEnum" should "work" in {
    val testGenEnum = TestGenEnum.MIKE
    nullableTCK.methodWithNullableGenEnumParam(true, None)
    nullableTCK.methodWithNullableGenEnumParam(false, Option(testGenEnum))
    nullableTCK.methodWithNullableGenEnumHandlerWithHandler(true)(b => assert(testGenEnum == b))
    nullableTCK.methodWithNullableGenEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableGenEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testGenEnum == b.result())}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableGenEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableGenEnumReturn(true)
    nullableTCK.methodWithNullableGenEnumReturn(false)
  }

  "testNullableTypeVariable" should "work" in {
    nullableTCK.methodWithNullableTypeVariableParam(false, "whatever")
    nullableTCK.methodWithNullableTypeVariableParam(true, null)
    val w1 = new Waiter()
    nullableTCK.methodWithNullableTypeVariableHandlerWithHandler[String](true, "wibble")(a => {w1{assert(a == "wibble")}; w1.dismiss()})
    w1.await()
    val w2 = new Waiter()
    nullableTCK.methodWithNullableTypeVariableHandlerWithHandler[String](true, null)(b => {w1{assert(b == "sausages")}; w2.dismiss()})
    w2.await()
    val w3 = new Waiter()
    nullableTCK.methodWithNullableTypeVariableHandlerAsyncResultWithHandler[String](true, "sausages")(c => {w3{assert(c.result() == "sausages")};w3.dismiss()})
    w3.await()
    val w4 = new Waiter()
    nullableTCK.methodWithNullableTypeVariableHandlerAsyncResultWithHandler[String](true, null)(d => {w4{assert(d.result() == null)};w4.dismiss()})
    assert("fizz1" == nullableTCK.methodWithNullableTypeVariableReturn[String](true, "fizz1"))
    assert(null == nullableTCK.methodWithNullableTypeVariableReturn(false, "fizz2"))
    w4.await()
  }

  "testNullableObjectParam" should "work" in {
    nullableTCK.methodWithNullableObjectParam(true, null)
    nullableTCK.methodWithNullableObjectParam(false, "object_param")
  }








  "testNullableListByte" should "work" in {
    import collection.JavaConverters._
    val testListByte = mutable.Buffer(12.toByte,24.toByte,(-12).toByte)
    nullableTCK.methodWithNullableListByteParam(true, None)
    nullableTCK.methodWithNullableListByteParam(false, Option(testListByte))
    nullableTCK.methodWithNullableListByteHandlerWithHandler(true)(b => assert(testListByte == b))
    nullableTCK.methodWithNullableListByteHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListByteHandlerAsyncResultWithHandler(true)(b => {w{assert(testListByte.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListByteHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListByteReturn(true)
    nullableTCK.methodWithNullableListByteReturn(false)
  }

  "testNullableListShort" should "work" in {
    import collection.JavaConverters._
    val testListShort = mutable.Buffer(520.toShort,1040.toShort,(-520).toShort)
    nullableTCK.methodWithNullableListShortParam(true, None)
    nullableTCK.methodWithNullableListShortParam(false, Option(testListShort))
    nullableTCK.methodWithNullableListShortHandlerWithHandler(true)(b => assert(testListShort == b))
    nullableTCK.methodWithNullableListShortHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListShortHandlerAsyncResultWithHandler(true)(b => {w{assert(testListShort.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListShortHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListShortReturn(true)
    nullableTCK.methodWithNullableListShortReturn(false)
  }

  "testNullableListInteger" should "work" in {
    import collection.JavaConverters._
    val testListInteger = mutable.Buffer(12345,54321,-12345)
    nullableTCK.methodWithNullableListIntegerParam(true, None)
    nullableTCK.methodWithNullableListIntegerParam(false, Option(testListInteger))
    nullableTCK.methodWithNullableListIntegerHandlerWithHandler(true)(b => assert(testListInteger == b))
    nullableTCK.methodWithNullableListIntegerHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListIntegerHandlerAsyncResultWithHandler(true)(b => {w{assert(testListInteger.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListIntegerHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListIntegerReturn(true)
    nullableTCK.methodWithNullableListIntegerReturn(false)
  }

  "testNullableListLong" should "work" in {
    import collection.JavaConverters._
    val testListLong = mutable.Buffer(123456789l,987654321l,-123456789l)
    nullableTCK.methodWithNullableListLongParam(true, None)
    nullableTCK.methodWithNullableListLongParam(false, Option(testListLong))
    nullableTCK.methodWithNullableListLongHandlerWithHandler(true)(b => assert(testListLong == b))
    nullableTCK.methodWithNullableListLongHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListLongHandlerAsyncResultWithHandler(true)(b => {w{assert(testListLong.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListLongHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListLongReturn(true)
    nullableTCK.methodWithNullableListLongReturn(false)
  }

  "testNullableListFloat" should "work" in {
    import collection.JavaConverters._
    val testListFloat = mutable.Buffer(1.1f,2.2f,3.3f)
    nullableTCK.methodWithNullableListFloatParam(true, None)
    nullableTCK.methodWithNullableListFloatParam(false, Option(testListFloat))
    nullableTCK.methodWithNullableListFloatHandlerWithHandler(true)(b => assert(testListFloat == b))
    nullableTCK.methodWithNullableListFloatHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListFloatHandlerAsyncResultWithHandler(true)(b => {w{assert(testListFloat.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListFloatHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListFloatReturn(true)
    nullableTCK.methodWithNullableListFloatReturn(false)
  }

  "testNullableListDouble" should "work" in {
    import collection.JavaConverters._
    val testListDouble = mutable.Buffer(1.11,2.22,3.33)
    nullableTCK.methodWithNullableListDoubleParam(true, None)
    nullableTCK.methodWithNullableListDoubleParam(false, Option(testListDouble))
    nullableTCK.methodWithNullableListDoubleHandlerWithHandler(true)(b => assert(testListDouble == b))
    nullableTCK.methodWithNullableListDoubleHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListDoubleHandlerAsyncResultWithHandler(true)(b => {w{assert(testListDouble.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListDoubleHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListDoubleReturn(true)
    nullableTCK.methodWithNullableListDoubleReturn(false)
  }

  "testNullableListBoolean" should "work" in {
    import collection.JavaConverters._
    val testListBoolean = mutable.Buffer( true,false,true)
    nullableTCK.methodWithNullableListBooleanParam(true, None)
    nullableTCK.methodWithNullableListBooleanParam(false, Option(testListBoolean))
    nullableTCK.methodWithNullableListBooleanHandlerWithHandler(true)(b => assert(testListBoolean == b))
    nullableTCK.methodWithNullableListBooleanHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListBooleanHandlerAsyncResultWithHandler(true)(b => {w{assert(testListBoolean.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListBooleanHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListBooleanReturn(true)
    nullableTCK.methodWithNullableListBooleanReturn(false)
  }

  "testNullableListString" should "work" in {
    import collection.JavaConverters._
    val testListString = mutable.Buffer("first","second","third")
    nullableTCK.methodWithNullableListStringParam(true, None)
    nullableTCK.methodWithNullableListStringParam(false, Option(testListString))
    nullableTCK.methodWithNullableListStringHandlerWithHandler(true)(b => assert(testListString == b))
    nullableTCK.methodWithNullableListStringHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListStringHandlerAsyncResultWithHandler(true)(b => {w{assert(testListString.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListStringHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListStringReturn(true)
    nullableTCK.methodWithNullableListStringReturn(false)
  }

  "testNullableListChar" should "work" in {
    import collection.JavaConverters._
    val testListChar = mutable.Buffer('x','y','z')
    nullableTCK.methodWithNullableListCharParam(true, None)
    nullableTCK.methodWithNullableListCharParam(false, Option(testListChar))
    nullableTCK.methodWithNullableListCharHandlerWithHandler(true)(b => assert(testListChar == b))
    nullableTCK.methodWithNullableListCharHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListCharHandlerAsyncResultWithHandler(true)(b => {w{assert(testListChar.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListCharHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListCharReturn(true)
    nullableTCK.methodWithNullableListCharReturn(false)
  }

  "testNullableListJsonObject" should "work" in {
    import collection.JavaConverters._
    val testListJsonObject = mutable.Buffer(Json.obj(("foo","bar")), Json.obj(("juu",3)))
    nullableTCK.methodWithNullableListJsonObjectParam(true, None)
    nullableTCK.methodWithNullableListJsonObjectParam(false, Option(testListJsonObject))
    nullableTCK.methodWithNullableListJsonObjectHandlerWithHandler(true)(b => assert(testListJsonObject == b))
    nullableTCK.methodWithNullableListJsonObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListJsonObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testListJsonObject.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListJsonObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListJsonObjectReturn(true)
    nullableTCK.methodWithNullableListJsonObjectReturn(false)
  }

  "testNullableListJsonArray" should "work" in {
    import collection.JavaConverters._
    val testListJsonArray = mutable.Buffer(Json.arr("foo","bar"), Json.arr("juu"))
    nullableTCK.methodWithNullableListJsonArrayParam(true, None)
    nullableTCK.methodWithNullableListJsonArrayParam(false, Option(testListJsonArray))
    nullableTCK.methodWithNullableListJsonArrayHandlerWithHandler(true)(b => assert(testListJsonArray == b))
    nullableTCK.methodWithNullableListJsonArrayHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListJsonArrayHandlerAsyncResultWithHandler(true)(b => {w{assert(testListJsonArray.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListJsonArrayHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListJsonArrayReturn(true)
    nullableTCK.methodWithNullableListJsonArrayReturn(false)
  }

  "testNullableListApi" should "work" in {
    import collection.JavaConverters._
    val iface = new RefedInterface1Impl().setString("refed_is_here")
    val testListApi = mutable.Buffer(RefedInterface1(iface))
    nullableTCK.methodWithNullableListApiParam(true, None)
    nullableTCK.methodWithNullableListApiParam(false, Option(testListApi))
    nullableTCK.methodWithNullableListApiHandlerWithHandler(true)(b => assert(b.forall(a => a.asJava == iface)))
    nullableTCK.methodWithNullableListApiHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListApiHandlerAsyncResultWithHandler(true)(b => {w{assert(b.result().forall(a => a.asJava == iface))}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListApiHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListApiReturn(true)
    nullableTCK.methodWithNullableListApiReturn(false)
  }

  "testNullableListDataObject" should "work" in {
    import collection.JavaConverters._
    val json = Json.obj(("foo","foo_value"), ("bar",12345), ("wibble",5.6))
    val testListDataObject = mutable.Buffer(TestDataObject.fromJson(json))
    nullableTCK.methodWithNullableListDataObjectParam(true, None)
    nullableTCK.methodWithNullableListDataObjectParam(false, Option(testListDataObject))
    nullableTCK.methodWithNullableListDataObjectHandlerWithHandler(true)(b => assert(b.forall(a => a.asJava.toJson == json)))
    nullableTCK.methodWithNullableListDataObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListDataObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testListDataObject.forall(a => a.asJava.toJson == Json.obj(("foo","foo_value"), ("bar",12345), ("wibble",5.6))))}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListDataObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListDataObjectReturn(true)
    nullableTCK.methodWithNullableListDataObjectReturn(false)
  }

  "testNullableListEnum" should "work" in {
    import collection.JavaConverters._
    val testListEnum = mutable.Buffer(TestEnum.TIM, TestEnum.JULIEN)
    nullableTCK.methodWithNullableListEnumParam(true, None)
    nullableTCK.methodWithNullableListEnumParam(false, Option(testListEnum))
    nullableTCK.methodWithNullableListEnumHandlerWithHandler(true)(b => assert(testListEnum == b))
    nullableTCK.methodWithNullableListEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testListEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListEnumReturn(true)
    nullableTCK.methodWithNullableListEnumReturn(false)
  }

  "testNullableListGenEnum" should "work" in {
    import collection.JavaConverters._
    val testListGenEnum = mutable.Buffer(TestGenEnum.BOB, TestGenEnum.LELAND)
    nullableTCK.methodWithNullableListGenEnumParam(true, None)
    nullableTCK.methodWithNullableListGenEnumParam(false, Option(testListGenEnum))
    nullableTCK.methodWithNullableListGenEnumHandlerWithHandler(true)(b => assert(testListGenEnum == b))
    nullableTCK.methodWithNullableListGenEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableListGenEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testListGenEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableListGenEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableListGenEnumReturn(true)
    nullableTCK.methodWithNullableListGenEnumReturn(false)
  }











  "testNullableSetByte" should "work" in {
    import collection.JavaConverters._
    val testSetByte = Set(12.toByte,24.toByte,(-12).toByte)
    nullableTCK.methodWithNullableSetByteParam(true, None)
    nullableTCK.methodWithNullableSetByteParam(false, Option(testSetByte))
    nullableTCK.methodWithNullableSetByteHandlerWithHandler(true)(b => assert(testSetByte == b))
    nullableTCK.methodWithNullableSetByteHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetByteHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetByte.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetByteHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetByteReturn(true)
    nullableTCK.methodWithNullableSetByteReturn(false)
  }

  "testNullableSetShort" should "work" in {
    import collection.JavaConverters._
    val testSetShort = Set(520.toShort,1040.toShort,(-520).toShort)
    nullableTCK.methodWithNullableSetShortParam(true, None)
    nullableTCK.methodWithNullableSetShortParam(false, Option(testSetShort))
    nullableTCK.methodWithNullableSetShortHandlerWithHandler(true)(b => assert(testSetShort == b))
    nullableTCK.methodWithNullableSetShortHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetShortHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetShort.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetShortHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetShortReturn(true)
    nullableTCK.methodWithNullableSetShortReturn(false)
  }

  "testNullableSetInteger" should "work" in {
    import collection.JavaConverters._
    val testSetInteger = Set(12345,54321,-12345)
    nullableTCK.methodWithNullableSetIntegerParam(true, None)
    nullableTCK.methodWithNullableSetIntegerParam(false, Option(testSetInteger))
    nullableTCK.methodWithNullableSetIntegerHandlerWithHandler(true)(b => assert(testSetInteger == b))
    nullableTCK.methodWithNullableSetIntegerHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetIntegerHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetInteger.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetIntegerHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetIntegerReturn(true)
    nullableTCK.methodWithNullableSetIntegerReturn(false)
  }

  "testNullableSetLong" should "work" in {
    import collection.JavaConverters._
    val testSetLong = Set(123456789l,987654321l,-123456789l)
    nullableTCK.methodWithNullableSetLongParam(true, None)
    nullableTCK.methodWithNullableSetLongParam(false, Option(testSetLong))
    nullableTCK.methodWithNullableSetLongHandlerWithHandler(true)(b => assert(testSetLong == b))
    nullableTCK.methodWithNullableSetLongHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetLongHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetLong.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetLongHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetLongReturn(true)
    nullableTCK.methodWithNullableSetLongReturn(false)
  }

  "testNullableSetFloat" should "work" in {
    import collection.JavaConverters._
    val testSetFloat = Set(1.1f,2.2f,3.3f)
    nullableTCK.methodWithNullableSetFloatParam(true, None)
    nullableTCK.methodWithNullableSetFloatParam(false, Option(testSetFloat))
    nullableTCK.methodWithNullableSetFloatHandlerWithHandler(true)(b => assert(testSetFloat == b))
    nullableTCK.methodWithNullableSetFloatHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetFloatHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetFloat.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetFloatHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetFloatReturn(true)
    nullableTCK.methodWithNullableSetFloatReturn(false)
  }

  "testNullableSetDouble" should "work" in {
    import collection.JavaConverters._
    val testSetDouble = Set(1.11,2.22,3.33)
    nullableTCK.methodWithNullableSetDoubleParam(true, None)
    nullableTCK.methodWithNullableSetDoubleParam(false, Option(testSetDouble))
    nullableTCK.methodWithNullableSetDoubleHandlerWithHandler(true)(b => assert(testSetDouble == b))
    nullableTCK.methodWithNullableSetDoubleHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetDoubleHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetDouble.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetDoubleHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetDoubleReturn(true)
    nullableTCK.methodWithNullableSetDoubleReturn(false)
  }

  "testNullableSetBoolean" should "work" in {
    import collection.JavaConverters._
    val testSetBoolean = Set( true,false,true)
    nullableTCK.methodWithNullableSetBooleanParam(true, None)
    nullableTCK.methodWithNullableSetBooleanParam(false, Option(testSetBoolean))
    nullableTCK.methodWithNullableSetBooleanHandlerWithHandler(true)(b => assert(testSetBoolean == b))
    nullableTCK.methodWithNullableSetBooleanHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetBooleanHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetBoolean.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetBooleanHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetBooleanReturn(true)
    nullableTCK.methodWithNullableSetBooleanReturn(false)
  }

  "testNullableSetString" should "work" in {
    import collection.JavaConverters._
    val testSetString = Set("first","second","third")
    nullableTCK.methodWithNullableSetStringParam(true, None)
    nullableTCK.methodWithNullableSetStringParam(false, Option(testSetString))
    nullableTCK.methodWithNullableSetStringHandlerWithHandler(true)(b => assert(testSetString == b))
    nullableTCK.methodWithNullableSetStringHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetStringHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetString.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetStringHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetStringReturn(true)
    nullableTCK.methodWithNullableSetStringReturn(false)
  }

  "testNullableSetChar" should "work" in {
    import collection.JavaConverters._
    val testSetChar = Set('x','y','z')
    nullableTCK.methodWithNullableSetCharParam(true, None)
    nullableTCK.methodWithNullableSetCharParam(false, Option(testSetChar))
    nullableTCK.methodWithNullableSetCharHandlerWithHandler(true)(b => assert(testSetChar == b))
    nullableTCK.methodWithNullableSetCharHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetCharHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetChar.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetCharHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetCharReturn(true)
    nullableTCK.methodWithNullableSetCharReturn(false)
  }

  "testNullableSetJsonObject" should "work" in {
    import collection.JavaConverters._
    val testSetJsonObject = Set(Json.obj(("foo","bar")), Json.obj(("juu",3)))
    nullableTCK.methodWithNullableSetJsonObjectParam(true, None)
    nullableTCK.methodWithNullableSetJsonObjectParam(false, Option(testSetJsonObject))
    nullableTCK.methodWithNullableSetJsonObjectHandlerWithHandler(true)(b => assert(testSetJsonObject == b))
    nullableTCK.methodWithNullableSetJsonObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetJsonObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetJsonObject.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetJsonObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetJsonObjectReturn(true)
    nullableTCK.methodWithNullableSetJsonObjectReturn(false)
  }

  "testNullableSetJsonArray" should "work" in {
    import collection.JavaConverters._
    val testSetJsonArray = Set(Json.arr("foo","bar"), Json.arr("juu"))
    nullableTCK.methodWithNullableSetJsonArrayParam(true, None)
    nullableTCK.methodWithNullableSetJsonArrayParam(false, Option(testSetJsonArray))
    nullableTCK.methodWithNullableSetJsonArrayHandlerWithHandler(true)(b => assert(testSetJsonArray == b))
    nullableTCK.methodWithNullableSetJsonArrayHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetJsonArrayHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetJsonArray.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetJsonArrayHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetJsonArrayReturn(true)
    nullableTCK.methodWithNullableSetJsonArrayReturn(false)
  }

  "testNullableSetApi" should "work" in {
    import collection.JavaConverters._
    val iface = new RefedInterface1Impl().setString("refed_is_here")
    val testSetApi = Set(RefedInterface1(iface))
    nullableTCK.methodWithNullableSetApiParam(true, None)
    nullableTCK.methodWithNullableSetApiParam(false, Option(testSetApi))
    nullableTCK.methodWithNullableSetApiHandlerWithHandler(true)(b => assert(b.forall(a => a.asJava == iface)))
    nullableTCK.methodWithNullableSetApiHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetApiHandlerAsyncResultWithHandler(true)(b => {w{assert(b.result().forall(a => a.asJava == iface))}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetApiHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetApiReturn(true)
    nullableTCK.methodWithNullableSetApiReturn(false)
  }

  "testNullableSetDataObject" should "work" in {
    import collection.JavaConverters._
    val json = Json.obj(("foo","foo_value"), ("bar",12345), ("wibble",5.6))
    val testSetDataObject = Set(TestDataObject.fromJson(json))
    nullableTCK.methodWithNullableSetDataObjectParam(true, None)
    nullableTCK.methodWithNullableSetDataObjectParam(false, Option(testSetDataObject))
    nullableTCK.methodWithNullableSetDataObjectHandlerWithHandler(true)(b => assert(b.forall(a => a.asJava.toJson == json)))
    nullableTCK.methodWithNullableSetDataObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetDataObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetDataObject.forall(a => a.asJava.toJson == Json.obj(("foo","foo_value"), ("bar",12345), ("wibble",5.6))))}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetDataObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetDataObjectReturn(true)
    nullableTCK.methodWithNullableSetDataObjectReturn(false)
  }

  "testNullableSetEnum" should "work" in {
    import collection.JavaConverters._
    val testSetEnum = Set(TestEnum.TIM, TestEnum.JULIEN)
    nullableTCK.methodWithNullableSetEnumParam(true, None)
    nullableTCK.methodWithNullableSetEnumParam(false, Option(testSetEnum))
    nullableTCK.methodWithNullableSetEnumHandlerWithHandler(true)(b => assert(testSetEnum == b))
    nullableTCK.methodWithNullableSetEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetEnumReturn(true)
    nullableTCK.methodWithNullableSetEnumReturn(false)
  }

  "testNullableSetGenEnum" should "work" in {
    import collection.JavaConverters._
    val testSetGenEnum = Set(TestGenEnum.BOB, TestGenEnum.LELAND)
    nullableTCK.methodWithNullableSetGenEnumParam(true, None)
    nullableTCK.methodWithNullableSetGenEnumParam(false, Option(testSetGenEnum))
    nullableTCK.methodWithNullableSetGenEnumHandlerWithHandler(true)(b => assert(testSetGenEnum == b))
    nullableTCK.methodWithNullableSetGenEnumHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableSetGenEnumHandlerAsyncResultWithHandler(true)(b => {w{assert(testSetGenEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableSetGenEnumHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableSetGenEnumReturn(true)
    nullableTCK.methodWithNullableSetGenEnumReturn(false)
  }














  "testNullableMapByte" should "work" in {
    import collection.JavaConverters._
    val testMapByte = Map("1" -> 1.toByte, "2" -> 2.toByte, "3" -> 3.toByte)
    nullableTCK.methodWithNullableMapByteParam(true, None)
    nullableTCK.methodWithNullableMapByteParam(false, Option(testMapByte))
    nullableTCK.methodWithNullableMapByteHandlerWithHandler(true)(b => assert(testMapByte == b))
    nullableTCK.methodWithNullableMapByteHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapByteHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapByte.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapByteHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapByteReturn(true)
    nullableTCK.methodWithNullableMapByteReturn(false)
  }

  "testNullableMapShort" should "work" in {
    import collection.JavaConverters._
    val testMapShort = Map("1" -> 1.toShort, "2" -> 2.toShort, "3" -> 3.toShort)
    nullableTCK.methodWithNullableMapShortParam(true, None)
    nullableTCK.methodWithNullableMapShortParam(false, Option(testMapShort))
    nullableTCK.methodWithNullableMapShortHandlerWithHandler(true)(b => assert(testMapShort == b))
    nullableTCK.methodWithNullableMapShortHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapShortHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapShort.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapShortHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapShortReturn(true)
    nullableTCK.methodWithNullableMapShortReturn(false)
  }

  "testNullableMapInteger" should "work" in {
    import collection.JavaConverters._
    val testMapInteger = Map("1" -> 1,"2" -> 2, "3" -> 3)
    nullableTCK.methodWithNullableMapIntegerParam(true, None)
    nullableTCK.methodWithNullableMapIntegerParam(false, Option(testMapInteger))
    nullableTCK.methodWithNullableMapIntegerHandlerWithHandler(true)(b => assert(testMapInteger == b))
    nullableTCK.methodWithNullableMapIntegerHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapIntegerHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapInteger.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapIntegerHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapIntegerReturn(true)
    nullableTCK.methodWithNullableMapIntegerReturn(false)
  }

  "testNullableMapLong" should "work" in {
    import collection.JavaConverters._
    val testMapLong = Map("1" -> 1l, "2" -> 2l, "3" -> 3l)
    nullableTCK.methodWithNullableMapLongParam(true, None)
    nullableTCK.methodWithNullableMapLongParam(false, Option(testMapLong))
    nullableTCK.methodWithNullableMapLongHandlerWithHandler(true)(b => assert(testMapLong == b))
    nullableTCK.methodWithNullableMapLongHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapLongHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapLong.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapLongHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapLongReturn(true)
    nullableTCK.methodWithNullableMapLongReturn(false)
  }

  "testNullableMapFloat" should "work" in {
    import collection.JavaConverters._
    val testMapFloat = Map("1" -> 1.1f, "2" -> 2.2f, "3" -> 3.3f)
    nullableTCK.methodWithNullableMapFloatParam(true, None)
    nullableTCK.methodWithNullableMapFloatParam(false, Option(testMapFloat))
    nullableTCK.methodWithNullableMapFloatHandlerWithHandler(true)(b => assert(testMapFloat == b))
    nullableTCK.methodWithNullableMapFloatHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapFloatHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapFloat.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapFloatHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapFloatReturn(true)
    nullableTCK.methodWithNullableMapFloatReturn(false)
  }

  "testNullableMapDouble" should "work" in {
    import collection.JavaConverters._
    val testMapDouble = Map("1" -> 1.11, "2" -> 2.22, "3" ->3.33)
    nullableTCK.methodWithNullableMapDoubleParam(true, None)
    nullableTCK.methodWithNullableMapDoubleParam(false, Option(testMapDouble))
    nullableTCK.methodWithNullableMapDoubleHandlerWithHandler(true)(b => assert(testMapDouble == b))
    nullableTCK.methodWithNullableMapDoubleHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapDoubleHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapDouble.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapDoubleHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapDoubleReturn(true)
    nullableTCK.methodWithNullableMapDoubleReturn(false)
  }

  "testNullableMapBoolean" should "work" in {
    import collection.JavaConverters._
    val testMapBoolean = Map( "1" -> true, "2" -> false, "3" -> true)
    nullableTCK.methodWithNullableMapBooleanParam(true, None)
    nullableTCK.methodWithNullableMapBooleanParam(false, Option(testMapBoolean))
    nullableTCK.methodWithNullableMapBooleanHandlerWithHandler(true)(b => assert(testMapBoolean == b))
    nullableTCK.methodWithNullableMapBooleanHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapBooleanHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapBoolean.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapBooleanHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapBooleanReturn(true)
    nullableTCK.methodWithNullableMapBooleanReturn(false)
  }

  "testNullableMapString" should "work" in {
    import collection.JavaConverters._
    val testMapString = Map("1" -> "first", "2" -> "second", "3" -> "third")
    nullableTCK.methodWithNullableMapStringParam(true, None)
    nullableTCK.methodWithNullableMapStringParam(false, Option(testMapString))
    nullableTCK.methodWithNullableMapStringHandlerWithHandler(true)(b => assert(testMapString == b))
    nullableTCK.methodWithNullableMapStringHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapStringHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapString.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapStringHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapStringReturn(true)
    nullableTCK.methodWithNullableMapStringReturn(false)
  }

  "testNullableMapChar" should "work" in {
    import collection.JavaConverters._
    val testMapChar = Map("1" -> 'x', "2" -> 'y', "3" -> 'z')
    nullableTCK.methodWithNullableMapCharParam(true, None)
    nullableTCK.methodWithNullableMapCharParam(false, Option(testMapChar))
    nullableTCK.methodWithNullableMapCharHandlerWithHandler(true)(b => assert(testMapChar == b))
    nullableTCK.methodWithNullableMapCharHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapCharHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapChar.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapCharHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapCharReturn(true)
    nullableTCK.methodWithNullableMapCharReturn(false)
  }

  "testNullableMapJsonObject" should "work" in {
    import collection.JavaConverters._
    val testMapJsonObject = Map("1" -> Json.obj(("foo","bar")), "2" -> Json.obj(("juu",3)))
    nullableTCK.methodWithNullableMapJsonObjectParam(true, None)
    nullableTCK.methodWithNullableMapJsonObjectParam(false, Option(testMapJsonObject))
    nullableTCK.methodWithNullableMapJsonObjectHandlerWithHandler(true)(b => assert(testMapJsonObject == b))
    nullableTCK.methodWithNullableMapJsonObjectHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapJsonObjectHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapJsonObject.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapJsonObjectHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapJsonObjectReturn(true)
    nullableTCK.methodWithNullableMapJsonObjectReturn(false)
  }

  "testNullableMapJsonArray" should "work" in {
    import collection.JavaConverters._
    val testMapJsonArray:Map[String, JsonArray] = Map("2" -> Json.arr("juu"), "1" -> Json.arr("foo","bar"))
    nullableTCK.methodWithNullableMapJsonArrayParam(true, None)
    nullableTCK.methodWithNullableMapJsonArrayParam(false, Option(testMapJsonArray))
    nullableTCK.methodWithNullableMapJsonArrayHandlerWithHandler(true)(b => assert(testMapJsonArray.toSet.diff(b.toSet).isEmpty))
    nullableTCK.methodWithNullableMapJsonArrayHandlerWithHandler(false)(b => assert(null == b))
    val w = new Waiter()
    nullableTCK.methodWithNullableMapJsonArrayHandlerAsyncResultWithHandler(true)(b => {w{assert(testMapJsonArray.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    val w2= new Waiter()
    nullableTCK.methodWithNullableMapJsonArrayHandlerAsyncResultWithHandler(false)(b => {w2{assert(null == b.result())}; w2.dismiss()})
    w2.await()
    nullableTCK.methodWithNullableMapJsonArrayReturn(true)
    nullableTCK.methodWithNullableMapJsonArrayReturn(false)
  }

  "testNullableMapApi" should "work" in {
    import collection.JavaConverters._
    val iface = new RefedInterface1Impl().setString("refed_is_here")
    val testMapApi = Map("1" -> RefedInterface1(iface))
    nullableTCK.methodWithNullableMapApiParam(true, None)
    nullableTCK.methodWithNullableMapApiParam(false, Option(testMapApi))
  }







//TODO there is no meaningful way to implement these in Scala as primitve nulls aren't possible
//  shared test void testListNullableByte() => testListNullable(ArrayList { 12.byte,null,24.byte }, nullableTCK.methodWithListNullableByteParam, nullableTCK.methodWithListNullableByteHandler, nullableTCK.methodWithListNullableByteHandlerAsyncResult, nullableTCK.methodWithListNullableByteReturn);
//  shared test void testListNullableShort() => testListNullable(ArrayList { 520,null,1040 }, nullableTCK.methodWithListNullableShortParam, nullableTCK.methodWithListNullableShortHandler, nullableTCK.methodWithListNullableShortHandlerAsyncResult, nullableTCK.methodWithListNullableShortReturn);
//  shared test void testListNullableInteger() => testListNullable(ArrayList { 12345,null,54321 }, nullableTCK.methodWithListNullableIntegerParam, nullableTCK.methodWithListNullableIntegerHandler, nullableTCK.methodWithListNullableIntegerHandlerAsyncResult, nullableTCK.methodWithListNullableIntegerReturn);
//  shared test void testListNullableLong() => testListNullable(ArrayList { 123456789,null,987654321 }, nullableTCK.methodWithListNullableLongParam, nullableTCK.methodWithListNullableLongHandler, nullableTCK.methodWithListNullableLongHandlerAsyncResult, nullableTCK.methodWithListNullableLongReturn);
//  shared test void testListNullableFloat() => testListNullable(ArrayList { 1.1,null,3.3 }, nullableTCK.methodWithListNullableFloatParam, nullableTCK.methodWithListNullableFloatHandler, nullableTCK.methodWithListNullableFloatHandlerAsyncResult, nullableTCK.methodWithListNullableFloatReturn, assertFloatEquals);
//  shared test void testListNullableDouble() => testListNullable(ArrayList { 1.11,null,3.33 }, nullableTCK.methodWithListNullableDoubleParam, nullableTCK.methodWithListNullableDoubleHandler, nullableTCK.methodWithListNullableDoubleHandlerAsyncResult, nullableTCK.methodWithListNullableDoubleReturn, assertFloatEquals);
//  shared test void testListNullableBoolean() => testListNullable(ArrayList { true,null,false }, nullableTCK.methodWithListNullableBooleanParam, nullableTCK.methodWithListNullableBooleanHandler, nullableTCK.methodWithListNullableBooleanHandlerAsyncResult, nullableTCK.methodWithListNullableBooleanReturn);
//  shared test void testListNullableChar() => testListNullable(ArrayList { 'F',null,'R' }, nullableTCK.methodWithListNullableCharParam, nullableTCK.methodWithListNullableCharHandler, nullableTCK.methodWithListNullableCharHandlerAsyncResult, nullableTCK.methodWithListNullableCharReturn);


  "testListNullableString" should "work" in {
    import collection.JavaConverters._
    val testListString:mutable.Buffer[String] = mutable.Buffer("first",null,"third")
    nullableTCK.methodWithListNullableStringParam(testListString)
    nullableTCK.methodWithListNullableStringHandler(b => assert(testListString.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithListNullableStringHandlerAsyncResult(b => {w{assert(testListString.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testListString == nullableTCK.methodWithListNullableStringReturn())
  }

  "testListNullableJsonObject" should "work" in {
    import collection.JavaConverters._
    val testListJsonObject:mutable.Buffer[JsonObject] = mutable.Buffer(Json.obj(("foo","bar")), null, Json.obj(("juu",3)))
    nullableTCK.methodWithListNullableJsonObjectParam(testListJsonObject)
    nullableTCK.methodWithListNullableJsonObjectHandler(b => assert(testListJsonObject.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithListNullableJsonObjectHandlerAsyncResult(b => {w{assert(testListJsonObject.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testListJsonObject == nullableTCK.methodWithListNullableJsonObjectReturn())
  }

  "testListNullableJsonArray" should "work" in {
    import collection.JavaConverters._
    val testListJsonArray:mutable.Buffer[JsonArray] = mutable.Buffer(Json.arr("foo","bar"), null, Json.arr("juu"))
    nullableTCK.methodWithListNullableJsonArrayParam(testListJsonArray)
    nullableTCK.methodWithListNullableJsonArrayHandler(b => assert(testListJsonArray.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithListNullableJsonArrayHandlerAsyncResult(b => {w{assert(testListJsonArray.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testListJsonArray == nullableTCK.methodWithListNullableJsonArrayReturn())
  }

  //TODO requires nullsafe equals in RefedInterface1Impl
  "testListNullableApi" should "work" ignore {
    import collection.JavaConverters._
    val iface1 = new RefedInterface1Impl().setString("first")
    val iface2 = new RefedInterface1Impl().setString("third")
    val testListApi:mutable.Buffer[RefedInterface1] = mutable.Buffer(RefedInterface1(iface1), null, RefedInterface1(iface2))
    nullableTCK.methodWithListNullableApiParam(testListApi)
    nullableTCK.methodWithListNullableApiHandler(b => assert(testListApi.map(x => refedIfaceToJavaOrNull(x)) == b.map(x => refedIfaceToJavaOrNull(x))))
    val w = new Waiter()
    nullableTCK.methodWithListNullableApiHandlerAsyncResult(b => {w{assert(testListApi.map(x => refedIfaceToJavaOrNull(x)).diff(b.result().map(a => refedIfaceToJavaOrNull(a))).isEmpty)}; w.dismiss()})
    w.await()
    assert(testListApi.map(x => refedIfaceToJavaOrNull(x)) == nullableTCK.methodWithListNullableApiReturn().map(x => refedIfaceToJavaOrNull(x)))
  }

  "testListNullableDataObject" should "work" in {
    import collection.JavaConverters._
    val json1 = Json.obj(("foo","first"), ("bar",1), ("wibble",1.1))
    val json2 = Json.obj(("foo","third"), ("bar",3), ("wibble",3.3))
    val testListDataObject:mutable.Buffer[TestDataObject] = mutable.Buffer(TestDataObject.fromJson(json1), null, TestDataObject.fromJson(json2))
    nullableTCK.methodWithListNullableDataObjectParam(testListDataObject)
    nullableTCK.methodWithListNullableDataObjectHandler(b => assert(testListDataObject.map(x => dataObjectToJsonOrNull(x)) == b.map(x => dataObjectToJsonOrNull(x))))
    val w = new Waiter()
    nullableTCK.methodWithListNullableDataObjectHandlerAsyncResult(b => {w{assert(testListDataObject.map(x => dataObjectToJsonOrNull(x)) == b.result().map(x => dataObjectToJsonOrNull(x)))}; w.dismiss()})
    w.await()
    assert(testListDataObject.map(x => dataObjectToJsonOrNull(x)) == nullableTCK.methodWithListNullableDataObjectReturn().map(x => dataObjectToJsonOrNull(x)))
  }

  "testListNullableGenEnum" should "work" in {
    import collection.JavaConverters._
    val testListGenEnum:mutable.Buffer[TestGenEnum] = mutable.Buffer(TestGenEnum.BOB,null,TestGenEnum.LELAND)
    nullableTCK.methodWithListNullableGenEnumParam(testListGenEnum)
    nullableTCK.methodWithListNullableGenEnumHandler(b => assert(testListGenEnum.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithListNullableGenEnumHandlerAsyncResult(b => {w{assert(testListGenEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testListGenEnum == nullableTCK.methodWithListNullableGenEnumReturn())
  }

  //TODO there is no meaningful way to implement these in Scala as primitve nulls aren't possible
  //  shared test void testSetNullableByte() => testSetNullable(ArrayList { 12.byte,null,24.byte }, nullableTCK.methodWithSetNullableByteParam, nullableTCK.methodWithSetNullableByteHandler, nullableTCK.methodWithSetNullableByteHandlerAsyncResult, nullableTCK.methodWithSetNullableByteReturn);
//  shared test void testSetNullableShort() => testSetNullable(ArrayList { 520,null,1040 }, nullableTCK.methodWithSetNullableShortParam, nullableTCK.methodWithSetNullableShortHandler, nullableTCK.methodWithSetNullableShortHandlerAsyncResult, nullableTCK.methodWithSetNullableShortReturn);
//  shared test void testSetNullableInteger() => testSetNullable(ArrayList { 12345,null,54321 }, nullableTCK.methodWithSetNullableIntegerParam, nullableTCK.methodWithSetNullableIntegerHandler, nullableTCK.methodWithSetNullableIntegerHandlerAsyncResult, nullableTCK.methodWithSetNullableIntegerReturn);
//  shared test void testSetNullableLong() => testSetNullable(ArrayList { 123456789,null,987654321 }, nullableTCK.methodWithSetNullableLongParam, nullableTCK.methodWithSetNullableLongHandler, nullableTCK.methodWithSetNullableLongHandlerAsyncResult, nullableTCK.methodWithSetNullableLongReturn);
//  shared test void testSetNullableFloat() => testSetNullable(ArrayList { 1.1,null,3.3 }, nullableTCK.methodWithSetNullableFloatParam, nullableTCK.methodWithSetNullableFloatHandler, nullableTCK.methodWithSetNullableFloatHandlerAsyncResult, nullableTCK.methodWithSetNullableFloatReturn, assertFloatEquals);
//  shared test void testSetNullableDouble() => testSetNullable(ArrayList { 1.11,null,3.33 }, nullableTCK.methodWithSetNullableDoubleParam, nullableTCK.methodWithSetNullableDoubleHandler, nullableTCK.methodWithSetNullableDoubleHandlerAsyncResult, nullableTCK.methodWithSetNullableDoubleReturn, assertFloatEquals);
//  shared test void testSetNullableBoolean() => testSetNullable(ArrayList { true,null,false }, nullableTCK.methodWithSetNullableBooleanParam, nullableTCK.methodWithSetNullableBooleanHandler, nullableTCK.methodWithSetNullableBooleanHandlerAsyncResult, nullableTCK.methodWithSetNullableBooleanReturn);
//  shared test void testSetNullableChar() => testSetNullable(ArrayList { 'F',null,'R' }, nullableTCK.methodWithSetNullableCharParam, nullableTCK.methodWithSetNullableCharHandler, nullableTCK.methodWithSetNullableCharHandlerAsyncResult, nullableTCK.methodWithSetNullableCharReturn);

  "testSetNullableString" should "work" in {
    import collection.JavaConverters._
    val testSetString:Set[String] = Set("first",null,"third")
    nullableTCK.methodWithSetNullableStringParam(testSetString)
    nullableTCK.methodWithSetNullableStringHandler(b => assert(testSetString.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithSetNullableStringHandlerAsyncResult(b => {w{assert(testSetString.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testSetString == nullableTCK.methodWithSetNullableStringReturn())
  }

  "testSetNullableJsonObject" should "work" in {
    import collection.JavaConverters._
    val testSetJsonObject:Set[JsonObject] = Set(Json.obj(("foo","bar")), null, Json.obj(("juu",3)))
    nullableTCK.methodWithSetNullableJsonObjectParam(testSetJsonObject)
    nullableTCK.methodWithSetNullableJsonObjectHandler(b => assert(testSetJsonObject.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithSetNullableJsonObjectHandlerAsyncResult(b => {w{assert(testSetJsonObject.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testSetJsonObject == nullableTCK.methodWithSetNullableJsonObjectReturn())
  }

  "testSetNullableJsonArray" should "work" in {
    import collection.JavaConverters._
    val testSetJsonArray:Set[JsonArray] = Set(Json.arr("foo","bar"), null, Json.arr("juu"))
    nullableTCK.methodWithSetNullableJsonArrayParam(testSetJsonArray)
    nullableTCK.methodWithSetNullableJsonArrayHandler(b => assert(testSetJsonArray.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithSetNullableJsonArrayHandlerAsyncResult(b => {w{assert(testSetJsonArray.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testSetJsonArray == nullableTCK.methodWithSetNullableJsonArrayReturn())
  }

  "testSetNullableApi" should "work" in {
    import collection.JavaConverters._
    val iface1 = new RefedInterface1Impl().setString("first")
    val iface2 = new RefedInterface1Impl().setString("third")
    val testSetApi:Set[RefedInterface1] = Set(RefedInterface1(iface1), null, RefedInterface1(iface2))
    //TODO: RefedInterface1Impl needs a nullsafe equals method!
//    nullableTCK.methodWithSetNullableApiParam(testSetApi)
//    nullableTCK.methodWithSetNullableApiHandler(b => assert(testSetApi.map(x => refedIfaceToJavaOrNull(x)) == b.map(x => refedIfaceToJavaOrNull(x))))
//    val w = new Waiter()
//    nullableTCK.methodWithSetNullableApiHandlerAsyncResult(b => {w{assert(testSetApi.map(x => refedIfaceToJavaOrNull(x)).asJava == b.result())}; w.dismiss()})
//    w.await()
//    assert(testSetApi.map(x => refedIfaceToJavaOrNull(x)) == nullableTCK.methodWithSetNullableApiReturn().map(x => refedIfaceToJavaOrNull(x)))
  }

  "testSetNullableDataObject" should "work" in {
    import collection.JavaConverters._
    val json1 = Json.obj(("foo","first"), ("bar",1), ("wibble",1.1))
    val json2 = Json.obj(("foo","third"), ("bar",3), ("wibble",3.3))
    val testSetDataObject:Set[TestDataObject] = Set(TestDataObject.fromJson(json1), null, TestDataObject.fromJson(json2))
    nullableTCK.methodWithSetNullableDataObjectParam(testSetDataObject)
    nullableTCK.methodWithSetNullableDataObjectHandler(b => assert(testSetDataObject.map(x => dataObjectToJsonOrNull(x)) == b.map(x => dataObjectToJsonOrNull(x))))
    val w = new Waiter()
    nullableTCK.methodWithSetNullableDataObjectHandlerAsyncResult(b => {w{assert(testSetDataObject.map(x => dataObjectToJsonOrNull(x)) == b.result().map(x => dataObjectToJsonOrNull(x)))}; w.dismiss()})
    w.await()
    assert(testSetDataObject.map(x => dataObjectToJsonOrNull(x)) == nullableTCK.methodWithSetNullableDataObjectReturn().map(x => dataObjectToJsonOrNull(x)))
  }



  "testSetNullableGenEnum" should "work" in {
    import collection.JavaConverters._
    val testSetGenEnum:Set[TestGenEnum] = Set(TestGenEnum.BOB,null,TestGenEnum.LELAND)
    nullableTCK.methodWithSetNullableGenEnumParam(testSetGenEnum)
    nullableTCK.methodWithSetNullableGenEnumHandler(b => assert(testSetGenEnum.diff(b).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithSetNullableGenEnumHandlerAsyncResult(b => {w{assert(testSetGenEnum.diff(b.result()).isEmpty)}; w.dismiss()})
    w.await()
    assert(testSetGenEnum == nullableTCK.methodWithSetNullableGenEnumReturn())
  }

  //TODO there is no meaningful way to implement these in Scala as primitve nulls aren't possible
//  shared test void testMapNullableByte() => testMapNullable(ArrayList { 12.byte,null,24.byte }, nullableTCK.methodWithMapNullableByteParam, nullableTCK.methodWithMapNullableByteHandler, nullableTCK.methodWithMapNullableByteHandlerAsyncResult, nullableTCK.methodWithMapNullableByteReturn);
//  shared test void testMapNullableShort() => testMapNullable(ArrayList { 520,null,1040 }, nullableTCK.methodWithMapNullableShortParam, nullableTCK.methodWithMapNullableShortHandler, nullableTCK.methodWithMapNullableShortHandlerAsyncResult, nullableTCK.methodWithMapNullableShortReturn);
//  shared test void testMapNullableInteger() => testMapNullable(ArrayList { 12345,null,54321 }, nullableTCK.methodWithMapNullableIntegerParam, nullableTCK.methodWithMapNullableIntegerHandler, nullableTCK.methodWithMapNullableIntegerHandlerAsyncResult, nullableTCK.methodWithMapNullableIntegerReturn);
//  shared test void testMapNullableLong() => testMapNullable(ArrayList { 123456789,null,987654321 }, nullableTCK.methodWithMapNullableLongParam, nullableTCK.methodWithMapNullableLongHandler, nullableTCK.methodWithMapNullableLongHandlerAsyncResult, nullableTCK.methodWithMapNullableLongReturn);
//  shared test void testMapNullableFloat() => testMapNullable(ArrayList { 1.1,null,3.3 }, nullableTCK.methodWithMapNullableFloatParam, nullableTCK.methodWithMapNullableFloatHandler, nullableTCK.methodWithMapNullableFloatHandlerAsyncResult, nullableTCK.methodWithMapNullableFloatReturn, assertFloatEquals);
//  shared test void testMapNullableDouble() => testMapNullable(ArrayList { 1.11,null,3.33 }, nullableTCK.methodWithMapNullableDoubleParam, nullableTCK.methodWithMapNullableDoubleHandler, nullableTCK.methodWithMapNullableDoubleHandlerAsyncResult, nullableTCK.methodWithMapNullableDoubleReturn, assertFloatEquals);
//  shared test void testMapNullableBoolean() => testMapNullable(ArrayList { true, null, false }, nullableTCK.methodWithMapNullableBooleanParam, nullableTCK.methodWithMapNullableBooleanHandler, nullableTCK.methodWithMapNullableBooleanHandlerAsyncResult, nullableTCK.methodWithMapNullableBooleanReturn);
//  shared test void testMapNullableChar() => testMapNullable(ArrayList { 'F',null,'R' }, nullableTCK.methodWithMapNullableCharParam, nullableTCK.methodWithMapNullableCharHandler, nullableTCK.methodWithMapNullableCharHandlerAsyncResult, nullableTCK.methodWithMapNullableCharReturn);

  "testMapNullableString" should "work" in {
    import collection.JavaConverters._
    val testMapString:Map[String, String] = Map("1" -> "first","2" -> null, "3" -> "third")
    nullableTCK.methodWithMapNullableStringParam(testMapString)
    nullableTCK.methodWithMapNullableStringHandler(b => assert(testMapString.toSet.diff(b.toSet).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithMapNullableStringHandlerAsyncResult(b => {w{assert(testMapString.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    assert(testMapString == nullableTCK.methodWithMapNullableStringReturn())
  }

  "testMapNullableJsonObject" should "work" in {
    import collection.JavaConverters._
    val testMapJsonObject:Map[String, JsonObject] = Map("1" -> Json.obj(("foo","bar")), "2" -> null, "3" -> Json.obj(("juu",3)))
    nullableTCK.methodWithMapNullableJsonObjectParam(testMapJsonObject)
    nullableTCK.methodWithMapNullableJsonObjectHandler(b => assert(testMapJsonObject.toSet.diff(b.toSet).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithMapNullableJsonObjectHandlerAsyncResult(b => {w{assert(testMapJsonObject.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    assert(testMapJsonObject == nullableTCK.methodWithMapNullableJsonObjectReturn())
  }

  "testMapNullableJsonArray" should "work" in {
    import collection.JavaConverters._
    val testMapJsonArray:Map[String, JsonArray] = Map("1" -> Json.arr("foo","bar"), "2" -> null, "3" -> Json.arr("juu"))
    nullableTCK.methodWithMapNullableJsonArrayParam(testMapJsonArray)
    nullableTCK.methodWithMapNullableJsonArrayHandler(b => assert(testMapJsonArray.toSet.diff(b.toSet).isEmpty))
    val w = new Waiter()
    nullableTCK.methodWithMapNullableJsonArrayHandlerAsyncResult(b => {w{assert(testMapJsonArray.toSet.diff(b.result().toSet).isEmpty)}; w.dismiss()})
    w.await()
    assert(testMapJsonArray == nullableTCK.methodWithMapNullableJsonArrayReturn())
  }

  "testMapNullableApi" should "work" in {
    import collection.JavaConverters._
    val iface1 = new RefedInterface1Impl().setString("first")
    val iface2 = new RefedInterface1Impl().setString("third")
    val testMapApi:Map[String, RefedInterface1] = Map("1" -> RefedInterface1(iface1), "2" -> null, "3" -> RefedInterface1(iface2))
    nullableTCK.methodWithMapNullableApiParam(testMapApi)
  }

  "testNullableHandler" should "work" in {
    nullableTCK.methodWithNullableHandlerWithHandler(true)(null)
    val w1 = new Waiter()
    nullableTCK.methodWithNullableHandlerWithHandler(false)(a => {w1{assert("the_string_value" == a)};w1.dismiss()})
    w1.await()
    nullableTCK.methodWithNullableHandlerAsyncResultWithHandler(true)(null)
    val w2 = new Waiter()
    nullableTCK.methodWithNullableHandlerAsyncResultWithHandler(false)(a => {w2{assert("the_string_value" == a.result())};w2.dismiss()})
    w2.await()
  }

  "testHttpServerOptionsJson" should "work" in {
    val json = Json.obj(("compressionSupported", true), ("ssl",true), ("sendBufferSize", 65000))
    val options = new HttpServerOptions().setCompressionSupported(true).setSsl(true).setSendBufferSize(65000)
    val actualOptions =  new HttpServerOptions(json)
    assert(actualOptions.isCompressionSupported == options.isCompressionSupported)
    assert(actualOptions.isSsl == options.isSsl)
    assert(actualOptions.getSendBufferSize == options.getSendBufferSize)
  }

  def dataObjectToJsonOrNull(x: TestDataObject): JsonObject = {
    if(x!=null) x.asJava.toJson else null
  }

  def refedIfaceToJavaOrNull(x: RefedInterface1):io.vertx.codegen.testmodel.RefedInterface1 = {
    if( x!= null) x.asJava else null
  }
}
