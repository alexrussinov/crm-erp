package controllers

import play.api.mvc._
import jp.t2v.lab.play2.auth.{Auth, LoginLogout}
import models.{Product, ProductTable}
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 18/07/13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
object Products extends  Controller with LoginLogout with AuthConf with Auth {

  def getProductsInJson = Action {
    val json = DB.withSession { implicit session =>
     val products : List[Product] = (for (p <- ProductTable) yield (p)).list
      Json.toJson(products)
    }
    Ok(json)
  }


}
// TODO Evolution for slick doesn't work
//TODO We need controller to import products from csv
//TODO controller to update products from csv
//TODO controllers to manage categories