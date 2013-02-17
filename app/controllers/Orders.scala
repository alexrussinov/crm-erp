package controllers


import play.api.mvc._
import play.api.data.{Form}
import play.api.data.Forms._
import models._
import java.sql.Timestamp
import org.scala_tools.time.Imports._
import jp.t2v.lab.play20.auth.{Auth, LoginLogout}
import play.api.Play._
import play.api.mvc.Results._
import scala.Some
import com.codahale.jerkson.Json
import play.api.Routes
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import scala.Some


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
  def listOrders = authorizedAction(NormalUser) { user => implicit request =>
                val json = Order.getAllJson
    Ok(json).as(JSON)
  }

  def getCustomersInJson =  authorizedAction(NormalUser) { user => implicit request =>
    val json = Customer.getAllJson
    Ok(json).as(JSON)
   }

  // Form to add line in to the invoice
  val addlineform = Form(
    tuple(
      "product_id" -> number,
      "product_qty" -> number
    )
  )


  def orderFiche(id : Int) = authorizedAction(NormalUser) { user => implicit request =>
    val order = Order.getById(id)
    Ok(views.html.orderfiche(order, user, addlineform))
  }

  def showListOrders = authorizedAction(NormalUser) { user => implicit request =>
  Ok(views.html.showorders(user))
  }

  def addLine = TODO

  def searchProducts(value : String) = Action {
    val json = transaction(DollConn.doll_session(current)){
      val products = from(Product.productTable) ( s => where ( (s.ref like value + "%") or
        (s.label like "%" + value + "%") ) select(s))
      Json.generate(products)
    }

    Ok(json).as(JSON)
  }

}

trait Ord {
  //Where redirect user after successful order creation
  def OrderCreationSucceeded(request: RequestHeader): Result = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
    Redirect(uri).withSession(request.session - "access_uri")
  }
}