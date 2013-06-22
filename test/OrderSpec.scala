import controllers.{Orders, routes}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import models._
import play.api.http.HeaderNames
//import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeHeaders, FakeRequest, FakeApplication}
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scala_tools.time.Imports._
import java.sql.Timestamp
import com.codahale.jerkson.Json._
import play.test.Helpers
import play.api.libs.json._



/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 09/01/13
 * Time: 20:33
 * To change this template use File | Settings | File Templates.
 */


class OrderSpec extends FlatSpec with ShouldMatchers{


  "A OrderLine" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
                                 1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
       val line = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      line should not equal(0)

    }
  }

  "A Order" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val order = Order.getById(id)
      order.id should not equal(0)
        order.ref should equal("CO0"+ DateTime.now.monthOfYear().get + DateTime.now.year().get() + "-" + order.id)

    }
  }

  "A User" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase()))
    {
     val user_id = Users.createUser("test@test.com", "12345",1)
      val user = Users.authenticate("test@test.com", "12345")
        user_id should not equal(0)
        user.isDefined
      }
    }
  "addLine method call" should "update order totals... properties" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val order_id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))

     // order.total_ht.head should equal(0.0)
     // order.total_ttc.head should equal(0.0)
      val line_id = OrderLine(order_id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      val order = Order.getById(order_id)
      val line = OrderLine.getLineById(line_id)
      line.qty should equal(2.0)
      line.prix_ht should equal(1.0)
      line.prix_ttc should equal(2.0)
      order.total_ht.head should equal(2.0)
      order.total_ttc.head should equal(4.0)


    }
  }
  "deleteLine method call" should "update order totals... properties" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val order_id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val order = Order.getById(order_id)
      order.total_ht.head should equal(0.0)
      order.total_ttc.head should equal(0.0)
      val line_id = OrderLine(order_id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      val order_after_insert = Order.getById(order_id)
      order_after_insert.total_ht.head should equal(2.0)
      order_after_insert.total_ttc.head should equal(4.0)
      OrderLine.deleteLine(line_id)
      val order_after_delete = Order.getById(order_id)
      order_after_delete.total_ht.head should equal(0.0)
      order_after_delete.total_ttc.head should equal(0.0)


    }
  }
  "updateLine method call" should "update order totals... properties" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val order_id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val order = Order.getById(order_id)
      order.total_ht.head should equal(0.0)
      order.total_ttc.head should equal(0.0)
      val line_id = OrderLine(order.id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      val order_after_insert = Order.getById(order_id)
      order_after_insert.total_ht.head should equal(2.0)
      order_after_insert.total_ttc.head should equal(4.0)
      OrderLine.updateLine(line_id,4.0)
      val order_after_update = Order.getById(order_id)
      order_after_update.total_ht.head should equal(4.0)
      order_after_update.total_ttc.head should equal(8.0)


    }
  }
/*  "A request to the addLine action" should "respond" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val result = controllers.Orders.addLine(FakeRequest().withFormUrlEncodedBody("product_id" -> "1",
      "product_qty" -> "1", "order_id"-> id.toString))
      status(result) should equal (SEE_OTHER)
      redirectLocation(result) should equal (Some(routes.Orders.orderFiche(1).url))
    }
  }*/

  /*"A request to the updateLine action" should "respond with new data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line_id = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      val result = Orders.updateLine(FakeRequest().withFormUrlEncodedBody("id"->line_id.toString,"tva"->"7.7","unite"->"piece",
      "prix_ht"->"9.99", "qty"->"99","order_id"->id.toString))

      val line = OrderLine.getLineById(line_id)

     line.tva should equal (7.7)
     line.unity should equal ("piece")

      //status(result) should equal (SEE_OTHER)
      //contentAsString(result) should include (line.id.toString)
    }
  }*/

  "A request to the listOrders Action" should "respond with data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))

      val result = controllers.Orders.listOrders(FakeRequest())
      status(result) should equal (OK)
      contentAsString(result) should include ("xxx")
    }
  }

  "A request to the getOrderLinesInJson Action" should "respond with data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine

      val result = controllers.Orders.getOrderLinesInJson(id)(FakeRequest())
      status(result) should equal (OK)
      contentAsString(result) should include (id.toString)
    }
  }

  "A OrderLine" should "be updatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,
        1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line_id = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine
      line_id should not equal(0)
      val line2_id = OrderLine(id,2,"xxx","xxx",5.5,2.0,"kg", 3.0, 2.0).insertLine
      line2_id should not equal(0)

      OrderLine.updateLine(line_id, 10.5)

      val line = OrderLine.getLineById(line_id)
      line.id should equal (1)
      line.unity should equal ("kg")
      line.qty should equal (10.5)
      line.tva should equal (5.5)
    }
  }

  "A request to the deleteLine Action" should "respond with action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine

      val result = controllers.Orders.deleteLine(1,id)(FakeRequest())
      status(result) should equal (OK)
    }
  }

  "POST addLine with JSON" should "respond with action and return a message" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))

      val map  = generate(Map("user_id"->1,"order_id"->1,"product_id"->1, "qty"->"5.0") )
      val jsnstr : JsValue  = Json.parse("""{"user_id":"1", "order_id":"1", "product_id": "5", "qty": "2.0"}""")
      val jsn = Json.parse(map)
      val request = FakeRequest().copy(body = jsnstr).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json");
      val fakeRequest = FakeRequest(Helpers.POST, routes.Orders.addLineInJson().url, FakeHeaders(), jsnstr)



      val req = FakeRequest(
        method = "POST",
        uri = routes.Orders.addLineInJson.url,
        headers = FakeHeaders(
          Map("Content-type"->Seq("application/json"))
        ),
        body =  jsn
      )
      val r = FakeRequest().withJsonBody(jsn)

      val result = controllers.Orders.addLineInJson()(r)
      status(result) should equal (OK)
      contentAsString(result) should include ("1")
    }
  }

  "POST updateLine with JSON" should "respond with action and return a message" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line_id = OrderLine(id,1,"xxx","xxx",5.5,2.0,"kg", 1.0, 2.0).insertLine

      val map  = generate(Map("order_id"->1,"line_id"->line_id, "qty"->"5.0") )
      val jsn = Json.parse(map)
      val r = FakeRequest().withJsonBody(jsn)

      val result = controllers.Orders.updateLineInJson()(r)
      status(result) should equal (OK)
      contentAsString(result) should include ("5.0")
    }
  }

  "A request to the getOrderInJson" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,1,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val line_id = OrderLine(id,1,"ref1","some label",5.5,2.5,"kg",9.99,12.1).insertLine
      val line = OrderLine.getLineById(line_id)
      val customer = Customer.getById(1)
      val result = controllers.Orders.getOrderInJson(id)(FakeRequest())
      status(result) should equal(OK)
      contentAsString(result) should include (customer.nom.head)
      contentAsString(result) should include ("xxx")
      contentAsString(result) should include (line.product_ref)
    }
  }

  "A request to validateOrder" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,0,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val result = controllers.Orders.validateOrder(id)(FakeRequest())
      status(result) should equal(OK)
      val order = Order.getById(id)
      order.fk_statut should equal(1)
    }
  }

  "A request to deleteOrder" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val t =  new Timestamp(DateTime.now.getMillis)
      val id = Order.createOrder(1,"2002-01-22", t,1,0,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val result = controllers.Orders.deleteOrder(id)(FakeRequest())
      status(result) should equal(SEE_OTHER)
      val orders = Order.getAllJson
      orders.length should equal(2)
    }
  }
  "A request to sendOrder" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      val result = controllers.Orders.sendOrder(1)(FakeRequest())
      status(result) should equal(OK)
    }
  }

}

