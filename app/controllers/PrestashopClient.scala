package controllers

import play.api.mvc.{Action, Controller}
import jp.t2v.lab.play20.auth.{Auth, LoginLogout}
import play.api.libs.ws.{Response, WS}
import play.api.libs.concurrent.Promise
import scala.xml.{Elem, NodeSeq}

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 19/05/13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
object PrestashopClient extends Controller with LoginLogout with AuthConf with Auth{



  def productList = Action {

    val products : Promise[Response] = WS.url("http://localhost:8080/prestashop_1.5.4.1/prestashop/api/products/")
      .withAuth("LXQBK5G67CMX6H3SXZCD24AJ9I9VBAAD", "", com.ning.http.client.Realm.AuthScheme.BASIC)
      .get
   val result : Promise[List[NodeSeq]] = products flatMap {resA =>
    val lst : List[scala.xml.Node] = resA.xml \\ "@id" toList
     val result = lst map {id =>
     val repB = WS.url("http://localhost:8080/prestashop_1.5.4.1/prestashop/api/products/"+id)
       .withAuth("LXQBK5G67CMX6H3SXZCD24AJ9I9VBAAD", "", com.ning.http.client.Realm.AuthScheme.BASIC).get
       val products = repB map { prod => prod.xml}
       products
     }
     Promise.sequence(result)
   }
     Async{
       result map {   f =>
         Ok("Result: "+ f )
       }
     }
  }

}
