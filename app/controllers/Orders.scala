package controllers


import _root_.util.pdf.PDF
import play.api.data.format.Formats._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import java.sql.Timestamp
import org.scala_tools.time.Imports._
import jp.t2v.lab.play2.auth._
import play.api.Play._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.functional.syntax._
//import com.codahale.jerkson.Json
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import play.api.Play.current
import lib._
import com.typesafe.plugin._
import org.apache.commons.mail._




object Orders extends  Controller with LoginLogout with AuthConf with Auth with Ord {

  val newOrderForm = Form (
     tuple(
     "societe" -> number,
     "order_date" -> text,
     "note" -> text
     )
 )

  def showCreateOrderForm = authorizedAction(NormalUser) {  user =>  implicit request =>
    Ok(views.html.neworderform(newOrderForm, user))

  }

  def createOrder = authorizedAction(NormalUser) { user => implicit request =>
  newOrderForm.bindFromRequest.fold (
  formWithErrors => BadRequest,
  data =>{
     val id = Order.createOrder(data._1, data._2, new Timestamp(DateTime.now.getMillis),
              user.id, 0, Some(0.toFloat),
              Some(0.toFloat), Some(0.toFloat), Some(data._3))

    //OrderCreationSucceeded(request)
    Redirect(routes.Orders.orderFiche(id))
  }
  )
  }

  // generate list of oders as JSON
  def listOrders = /*authorizedAction(NormalUser)*/ Action { /*user =>*/ implicit request =>
                val json = Order.getAllJson
    Ok(json).as(JSON)
  }

  // return orders for a specic customer
  def listCustomerOrdersJson(id : Int) = Action { implicit request =>
    val json = Order.getAllCustomerOrdersJson(id)
    Ok(json).as(JSON)
  }
  // Retrieve list of avaiable customers
  def getCustomersInJson =  authorizedAction(NormalUser) { user => implicit request =>
    val json = Customer.getAllJson
    Ok(json).as(JSON)
   }

  // Fetch customer in json by id

  def getCustomerInJson(id : Int)= authorizedAction(NormalUser) { user => implicit request =>
    val json = Customer.getByIdInJson(id)
    Ok(json).as(JSON)
  }

 // Fetch order lines in Json by order id
  def getOrderLinesInJson(order_id : Int) = /*authorizedAction(NormalUser) */Action {/* user => */ implicit request =>
    val json = OrderLine.getLinesJson(order_id)
    Ok(json).as(JSON)
  }

  //retrieve an order for the given id as json object
  def getOrderInJson(order_id: Int)= Action {
    val json = Order.getByIdInJson(order_id)
    Ok(json).as(JSON)
  }

  // Form to add line in to the invoice
  val addlineform = Form(
    tuple(
      "product"  -> text,
      "product_id" -> of[Int],
      "product_qty" -> text,
      "order_id" -> number

    )
  )

  //Form to update an order line
  val update_line_form = Form(
  tuple(
  "id" -> of[Int],
  "qty" -> text,
  "order_id" -> number
  )
  )

  def orderFiche(id : Int) = authorizedAction(NormalUser) { user => implicit request =>
    val order = Order.getById(id)
    val customer = Customer.getById(order.fk_soc)
    //Ok(views.html.orderfiche(order, user, addlineform, update_line_form ))
    Ok(views.html.orderfiche3(order, user))
  }

  def showListOrders = authorizedAction(NormalUser) { user => implicit request =>
  Ok(views.html.showorders(user))
  }

  def addLine = authorizedAction(NormalUser) { user => implicit request =>
    addlineform.bindFromRequest.fold(
      formWithErrors => {

        BadRequest(views.html.errors(formWithErrors))
      },
      data =>{
        val order = Order.getById(data._4)
        val customer = Customer.getById(order.fk_soc)
        val product = ProductDoll.getById(data._2)
        val pr_price = ProductDoll.getProductPrice(data._2, customer.price_level.getOrElse(0))
        OrderLine(data._4, product.id, product.ref.getOrElse(""), product.label.getOrElse(""),pr_price.tva_tx, Convertion.prse[Double](data._3).getOrElse(0.00),product.unite,pr_price.price, pr_price.price_ttc).insertLine
        Redirect(routes.Orders.orderFiche(data._4))
      }
    )

  }

//  def addLineInJson = Action(parse.json) { request =>
//
//
//    val client_id : Option[Int] = (request.body \ "user_id").asOpt[Int]
//    val order_id = (request.body \ "order_id").asOpt[Int]
//    val product_id = (request.body \ "product_id").asOpt[Int]
//    val qty = (request.body \ "qty").asOpt[Double]
//
//    val client : Customer = Customer.getById(client_id.getOrElse(1) )
//    val product_price : ProductPrice = ProductDoll.getProductPrice(product_id.head, client.price_level.head)
//    val product : ProductDoll = ProductDoll.getById(client_id.head)
//    val line_id : Int = OrderLine(order_id.head,product.id,product.ref.head,product.label.head,product.tva_tx.head, qty.head, product.unite,product_price.price,product_price.price_ttc).insertLine
//
//    if(line_id > 0)
//      Ok(request.body)
//    else BadRequest
//
//  }

  def addLineInJson = Action { implicit request =>
    request.body.asJson.map{ json =>
        val client_id  = (json \ "user_id").asOpt[Int]
        val order_id = (json \ "order_id").asOpt[Int]
        val product_id = (json \ "product_id").asOpt[Int]
        val qty = (json \ "qty").asOpt[String]

        val client : Customer = Customer.getById(client_id.head )
        val product_price : ProductPrice = ProductDoll.getProductPrice(product_id.head, client.price_level.head)
        val product : ProductDoll = ProductDoll.getById(product_id.head)
        val line_id =OrderLine(order_id.head,product.id,product.ref.head,product.label.head,product.tva_tx.head, qty.head.toDouble, product.unite,product_price.price,product_price.price_ttc).insertLine
        val order = Order.getByIdInJson(order_id.head)
        Ok(order).as(JSON)
    }.getOrElse{BadRequest}

  }



//  def updateLine (id : Int, tva : Double, qty : Double, unite : String, prix_ht : Double, order_id : Int) = Action {
//             OrderLine.updateLine(id, tva, unite, prix_ht, qty)
//              Ok("")
//  }
  def updateLine  = authorizedAction(NormalUser) {user => implicit request =>
  update_line_form.bindFromRequest.fold(

  formWithErrors => {

    BadRequest
  },
  data => {
  OrderLine.updateLine(data._1,Convertion.prse[Double](data._2).getOrElse(0.00))
  Redirect(routes.Orders.orderFiche(data._3))
  }
  )
    //OrderLine.updateLine(data._1, tva, unite, prix_ht, qty)
    //Ok("")
  }

  def updateLineInJson  = Action {  implicit request =>
    request.body.asJson.map{ json =>
      val order_id = (json \ "order_id").asOpt[Int]
      val line_id  = (json \ "line_id").asOpt[Int]
      val qty = (json \ "qty").asOpt[String]

      OrderLine.updateLine(line_id.get,qty.get.toDouble)
      val order = Order.getByIdInJson(order_id.get)
      Ok(order).as(JSON)
    }.getOrElse{BadRequest}

  }

  def searchProducts(value : Option[String], customer_id : Int) = Action {
    implicit val productsFormatTuple5 = (
      (__ \ '_1).write[Int] and
        (__ \ '_2).write[Option[String]] and
          (__ \ '_3).write[Option[String]] and
            (__ \ '_4).write[Double] and
              (__ \ '_5).write[Double]
      ).tupled : Writes[(Int,Option[String],Option[String],Double,Double)]

    val customer = Customer.getById(customer_id)
    val json = transaction(DollConn.doll_session(current)){
      //val products = from(Product.productTable) ( s => where ( (s.ref like value.map(_+ "%").?) or
      //  (s.label like value.map("%"+ _ + "%").?) ) select(s))

      val products  = from(ProductDoll.productTable, ProductDoll.productpriceTable) ((s,p) => where((s.ref like value.map(_+ "%").?) or
        (s.label like value.map("%"+ _ + "%").?) and (p.fk_product === s.id) and (p.price_level === customer.price_level))
       select(s.id, s.ref, s.label, p.price, p.tva_tx))
      //select(s))

      Json.toJson(products)
    }

    Ok(json).as(JSON)
  }

  def deleteLine (id : Int, order_id : Int) = Action {
    val result = OrderLine.deleteLine(id)
    if (result > 0){
      val order_json = Order.getByIdInJson(order_id)
    Ok(order_json).as(JSON)
    }
    else
      BadRequest

  }
  // validate an order and return updated order as json object
  def validateOrder(order_id : Int) = Action {
    Order.validate(order_id)
    val order = Order.getByIdInJson(order_id)

    Ok(order).as(JSON)

  }

  // make order editable
  def modifyOrder(order_id : Int) = Action {
    Order.modify(order_id)
    val order = Order.getByIdInJson(order_id)

    Ok(order).as(JSON)
  }
  // delete an order from the database
  def deleteOrder(order_id: Int) = Action { request =>
    Order.delete(order_id)
    Redirect(routes.Orders.showListOrders())
  }

  def sendOrder(order_id: Int) = authorizedAction(NormalUser) {user => implicit request =>

   val order = Order.getById(order_id)
   val client = Customer.getById(order.fk_soc)
   val lnk = Order.generateOrderInCSV(order_id)
    // Create the attachment
    val attachment = new EmailAttachment()
    attachment.setPath(lnk)
    attachment.setDisposition(EmailAttachment.ATTACHMENT)
    attachment.setDescription("commande")
    attachment.setName("commande "+order.ref)

    // Create the email message
    val email = new MultiPartEmail()
    email.setHostName("smtp.googlemail.com")
    email.setSmtpPort(465)
    email.setAuthenticator(new DefaultAuthenticator("imexbox@gmail.com", "eU0mwqAa"))
    email.setSSL(true)
    email.addTo("imexbox@gmail.com")
    email.setFrom("imexbox@gmail.com","Commandes")
    email.setSubject("Commande de "+client.nom.get)
    email.setMsg("Veuillez trouver ci-joint la commande "+order.ref + " de "+client.nom.get+"Author: "+user.email)

    // add the attachment
    email.attach(attachment)

    // send the email
    email.send()
    Ok("Email sended correctly")
}
 // generates pdf for the order and render it in the browser
  def generatePdf(order_id: Int) = Action {
    val order = Order.getById(order_id)
    val lines = OrderLine.getLines(order_id)
    val total_qty = Order.totalQty(order_id)
    val customer = Customer.getById(order.fk_soc)
    Ok(PDF.toBytes(views.html.tpl_pdf_customer_oder.render(order,lines,customer,total_qty))).as("application/pdf")
  }

  // create this controller for test purposes only

  def testPdf(id : Int) = Action {

    val order = Order.getById(id)
    val lines = OrderLine.getLines(id)
    val total_qty = Order.totalQty(id)
    val customer = Customer.getById(order.fk_soc)
    Ok(views.html.tpl_pdf_customer_oder(order,lines,customer,total_qty))
  }

//  TODO PDF generation doesn't work
//  TODO list of orders must depend on type of the user (admin or  normal user)

}

trait Ord {
  //Where redirect user after successful order creation
  def OrderCreationSucceeded(request: RequestHeader): Result = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
    Redirect(uri).withSession(request.session - "access_uri")
  }
}