package controllers


import play.api.data.format.Formats._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models._
import java.sql.Timestamp
import org.scala_tools.time.Imports._
import jp.t2v.lab.play20.auth.{Auth, LoginLogout}
import play.api.Play._
import play.api.mvc.Results._
import com.codahale.jerkson.Json
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import lib._
import play.api.libs.json._



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

  def getCustomersInJson =  authorizedAction(NormalUser) { user => implicit request =>
    val json = Customer.getAllJson
    Ok(json).as(JSON)
   }

  def getOrderLinesInJson(order_id : Int) = /*authorizedAction(NormalUser) */Action {/* user => */ implicit request =>
    val json = OrderLine.getLinesJson(order_id)
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
    Ok(views.html.orderfiche(order, user, addlineform, update_line_form ))
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

  def addLineInJson = Action(parse.json) { request =>
    val line_id: Int = OrderLine.readJs(request.body).insertLine
   if (line_id > 0)
    Ok("Line id:" + line_id)
    else BadRequest

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

  def searchProducts(value : Option[String], customer_id : Int) = Action {
    val customer = Customer.getById(customer_id)
    val json = transaction(DollConn.doll_session(current)){
      //val products = from(Product.productTable) ( s => where ( (s.ref like value.map(_+ "%").?) or
      //  (s.label like value.map("%"+ _ + "%").?) ) select(s))

      val products = from(ProductDoll.productTable, ProductDoll.productpriceTable) ((s,p) => where((s.ref like value.map(_+ "%").?) or
        (s.label like value.map("%"+ _ + "%").?) and (p.fk_product === s.id) and (p.price_level === customer.price_level))
       select(s.id, s.ref, s.label, p.price, p.tva_tx))
      //select(s))
      Json.generate(products)
    }

    Ok(json).as(JSON)
  }

  def deleteLine (id : Int, order_id : Int) = Action {
    val result = OrderLine.deleteLine(id)
    if (result > 0)
    Ok("")
    else
      BadRequest

  }



//  TODO Add controller for deleting an order
//  TODO Add controller for validating an order
//  TODO Add controller for sending an order
//  TODO Add controller for PDF and CSF generating
//  TODO list of orders must depend on type of the user (admin or  normal user)

}

trait Ord {
  //Where redirect user after successful order creation
  def OrderCreationSucceeded(request: RequestHeader): Result = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
    Redirect(uri).withSession(request.session - "access_uri")
  }
}