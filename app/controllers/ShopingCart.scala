package controllers

import play.api.mvc.Controller
import jp.t2v.lab.play20.auth.{Auth, LoginLogout}
import models.{Product, NormalUser}


/**
 * Project: ${PROJECT_NAME}
 * User: alex
 * Date: 20/01/13
 * Time: 16:51
 * 
 */
object ShopingCart extends Controller with LoginLogout with AuthConf with Auth {

    def addItem(item_id : Int, qty : Double) = authorizedAction(NormalUser){ user => implicit request =>
      val product = Product.getById(item_id)
      Ok("").withSession(session + (product.id.toString -> qty.toString))

    }
}