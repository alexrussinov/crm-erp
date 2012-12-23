package controllers

import play.api._
import play.api.mvc._
import org.squeryl.PrimitiveTypeMode._
import models.{AppDB, Product}
import com.codahale.jerkson.Json
object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def getProducts = Action {
    val json = inTransaction {
      val products = from(AppDB.productTable)(productTable =>
        select(productTable)
      )
      Json.generate(products)
    }
    Ok(json).as(JSON)
  }

}