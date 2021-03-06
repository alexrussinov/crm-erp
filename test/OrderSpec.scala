import controllers._
import models.CategoryT
import models.Company
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import models._
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.test.FakeApplication
import play.api.test.FakeHeaders
import play.api.test.{FakeHeaders, FakeRequest, FakeApplication}
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scala_tools.time.Imports._
import java.sql.Timestamp
import play.test.Helpers
import scala.Some
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current
import jp.t2v.lab.play2.auth.test.Helpers._
import scala.Some


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 09/01/13
 * Time: 20:33
 * To change this template use File | Settings | File Templates.
 */


class OrderSpec extends FlatSpec with ShouldMatchers{

  object config extends AuthConf


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
        order.ref should equal("CO"+ DateTime.now.monthOfYear().get + DateTime.now.year().get() + "-" + order.id)

    }
  }

  "A User" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase()))
    {
     val user_id = Users.createUser("test@test.com", "12345",1,Some(1))
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

      val map  = Json.toJson(Map("user_id"->Json.toJson(1),"order_id"->Json.toJson(1),"product_id"->Json.toJson(1), "qty"->Json.toJson("5.0"),"unite"->Json.toJson("piece")) )
      val jsnstr : JsValue  = Json.parse("""{"user_id":"1", "order_id":"1", "product_id": "5", "qty": "2.0"}""")
//      val jsn = Json.parse(map)
//      val request = FakeRequest().copy(body = jsnstr).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json");
      val fakeRequest = FakeRequest(Helpers.POST, routes.Orders.addLineInJson().url, FakeHeaders(), jsnstr)



//      val req = FakeRequest(
//        method = "POST",
//        uri = routes.Orders.addLineInJson.url,
//        headers = FakeHeaders(
//          Map("Content-type"->Seq("application/json"))
//        ),
//        body =  jsn
//      )
      val r = FakeRequest().withJsonBody(map)

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

      val map  = Json.toJson(Map("order_id"->Json.toJson(1),"line_id"->Json.toJson(line_id), "qty"->Json.toJson("5.0")) )
//      val jsn = Json.parse(map)
      val r = FakeRequest().withJsonBody(map)

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
      val customer = DB.withSession { implicit session => CompanyTable.getById(1)}
      val result = controllers.Orders.getOrderInJson(id)(FakeRequest())
      status(result) should equal(OK)
      contentAsString(result) should include (customer.name.head)
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
      //in first part we test deleeting order with lines
      val id : Int = Order.createOrder(1,"2002-01-22", t,1,0,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      // need to create lines for an order
      for(i <- 0 until 10){
      val map  = Json.toJson(Map("user_id"->Json.toJson(1),"order_id"->Json.toJson(id),"product_id"->Json.toJson(1), "qty"->Json.toJson("5.0"),"unite"->Json.toJson("piece")) )
      val r = FakeRequest().withJsonBody(map)
      val result1 = controllers.Orders.addLineInJson()(r)
      }
      val lines = OrderLine.getLines(id)
      lines.length should equal(10)

      val result = controllers.Orders.deleteOrder(id)(FakeRequest())
      status(result) should equal(SEE_OTHER)
      val orders = Order.getAllJson
      Json.stringify(orders).length should equal(2)

      // second part deleting an empty order
      val id2 : Int = Order.createOrder(1,"2002-01-22", t,1,0,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
      val result2 = controllers.Orders.deleteOrder(id2)(FakeRequest())
      status(result2) should equal(SEE_OTHER)
      val orders2 = Order.getAllJson
      Json.stringify(orders2).length should equal(2)

    }
  }
//     "A request to sendOrder" should "respond with Action" in  {
//     running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
//     val t =  new Timestamp(DateTime.now.getMillis)
//     val id : Int = Order.createOrder(1,"2002-01-22", t,1,0,Some(0.0),Some(0.0),Some(0.0),Some("xxx"))
//     val result = controllers.Orders.sendOrder(id)(FakeRequest().withLoggedIn(config)(1))
//     status(result) should equal(OK)
//     Order.getById(id).sent should equal (true)
//   }
//  }

  "A Supplier" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

     val id : Int = Supplier("Bac-Pol", "some address", "some tel", "some@email.com").create_supplier
     val supplier = Supplier.getById(id)
     val suppliers = Supplier.getAll
      supplier.id should not equal(0)
     // suppliers.size should equal (1)
    }
  }

  "A Product" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
     DB.withSession { implicit session =>
       ProductTable.insert(
         Product(None, "some ref", "some label",Some("some desc."),Some("image url"),"kg",
           None,1,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 14.99))
       val prod  = for (p <- ProductTable) yield p
      prod.first.id.get should equal(1)
    }
   }
  }


  "A Discount" should "be applicable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      CustomerDiscount(1,1,25.00).create_discount
      CustomerDiscount(1,2,30.00).create_discount
      DB.withSession { implicit session =>
        ProductTable.insertAll(
          Product(None, "some ref2", "some label",Some("some desc."),Some("image url"),"kg",
            None,2,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 10.00),
          Product(None, "some ref3", "some label",Some("some desc."),Some("image url"),"kg",
            None,3,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 9.99, 15.00)
        )

        val result = ProductTable.getAllProductsWithCustomerPrices(1)
        result.head._11 should equal(11.24)
        result(1)._11 should equal(7)
        result(2)._11 should equal(15)
      }
    }
  }


  "A CustomerDiscount" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
     val id : Int = CustomerDiscount.create(CustomerDiscount(1,1,25.00))
      val discount = CustomerDiscount.getCustomerDiscountsById(id)
      discount.id should not equal(0)
      }
  }

//  "Import method" should "return the result" in {
//    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
//      val res = Imports.importFromCsvWithHeaders("imports/test4.csv",";")
//      val products = Product.productsFromSource(res)
//      res.head("base_price").toDouble should equal(0.73)
//      res.head("reference") should include  ("BP_002106")
//      products.head.reference should equal("BP_002106")
//      products.head.supplier_id should equal(1)
//      products.head.base_price should equal(0.73)
//    }
//  }

  "A Category" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      DB.withSession { implicit session =>
        CategoryTable.insert(
        CategoryT(None,"Charcuterie"))
        val cat  = for (c <- CategoryTable) yield c
        cat.first.id.get should equal(1)
      }
    }
  }

  "A request to getCategories" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

      val result = controllers.Application.getCategoriesInJson(FakeRequest())
      status(result) should equal(OK)
      contentAsString(result) should include ("Produits du Vieux Village")
    }
  }

  "A request to getNumberOfProductsByCategory" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      DB.withSession { implicit session =>
        ProductTable.insert(
          Product(None, "some ref", "some label",Some("some desc."),Some("image url"),"kg",
            Some(31),2,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 10.00))
      }
      val result = controllers.Catalogue.getNumberOfProductsByCategory(FakeRequest())
      contentAsString(result) should include ("31")
    }
  }


  "A Company" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      DB.withSession { implicit session =>
        Seq(
            Company(None,Some("Bac-Pol"),None,Some("+33688818161"),None,false,false),
            Company(None,Some("Bac-Pol"),None,Some("+33688818161"),None,false,false)
        )foreach (CompanyTable.add)

        val id = CompanyTable.add(Company(None,Some("Bac-Pol"),None,Some("+33688818161"),None,false,false))

        AddressTable.insert(
        Address(None,None,Some("City"),Some("02100"),None,id)
        )

        ContactTable.insert(
        Contact(None,Some("Ivanov"),Some("John"),Some("developper"),None,None,None,id)
        )

        val cust : List[Company]  = CompanyTable.getAll
        val comp = cust.map(c=>CompanyJson(c.id,c.name,c.price_level,c.tel,c.email,c.supplier,c.prospect,c.address,c.contacts))
        cust.length should equal (23)
        cust.head.id.get should equal(1)
        //cust.head.address.get.id.get should equal(1)
        //cust.head.contacts.head.id.get should equal(1)
      }
    }
  }

  "A request to getCustomersJson" should "respond with Action" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){

      DB.withSession { implicit session =>
        Seq(
          Company(None,Some("Bac-Pol"),None,Some("+33688818161"),None,false,false),
          Company(None,Some("Kora"),None,Some("+33688818161"),None,false,false),
          Company(None,Some("Virtu"),None,Some("+33688818161"),None,false,false)
        )foreach(CompanyTable.insert)
        AddressTable.add(
          Address(None,None,Some("City"),Some("02100"),None,Some(21))
        )
      }

      val result = controllers.Companies.getCustomersInJson(FakeRequest())
      status(result) should equal(OK)
      contentAsString(result) should include ("Kora")
      contentAsString(result) should include ("City")
    }
  }

  "GetProductByIdForCustomer method" should "give correct value" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      CustomerDiscount(1,1,25.00).create_discount
      CustomerDiscount(1,2,30.00).create_discount
      DB.withSession { implicit session =>
        ProductTable.insertAll(
          Product(None, "some ref2", "some label",Some("some desc."),Some("image url"),"kg",
            None,2,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 10.00),
          Product(None, "some ref3", "some label",Some("some desc."),Some("image url"),"kg",
            None,3,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 9.99, 15.00)
        )

        val result = ProductTable.getProductByIdForCustomer(2,1)
        result.price should equal(7.00)
      }
    }
  }

}

