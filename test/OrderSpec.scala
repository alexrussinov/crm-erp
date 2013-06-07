import controllers.{Orders, routes}
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import models._
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scala_tools.time.Imports._
import java.sql.Timestamp

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
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
     val user_id = Users.createUser("test@test.com", "12345",1)
      val user = Users.authenticate("test@test.com", "12345")
        user_id should not equal(0)
        user.isDefined
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

      val result = controllers.Orders.deleteLine(2,id)(FakeRequest())
      status(result) should equal (OK)
    }
  }

  "A request to the getProductsWithPricesInJson in Json Action" should "respond with data" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {


     // val result = controllers.Catalogue.getProductsWithPricesInJson(1)(FakeRequest())
      //status(result) should equal (OK)

      val  result : String = ProductDoll.getProductsWithPrices(1)
      result should include ("1")
    }
  }

}

