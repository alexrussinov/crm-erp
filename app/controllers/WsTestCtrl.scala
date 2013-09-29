package controllers

import play.api.mvc._
import fly.play.s3.{BucketItem, S3}
import scala.concurrent._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 22/09/13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
object WsTestCtrl extends Controller {


  def getListOjectsFromBucket = Action {
    Async {
      val bucket = S3("itcpluswebproducts")
      val res : Future[Iterable[BucketItem]]  =  bucket.list("szubryt/")
      res map {  f=>
          val r : Iterable[String] = for {
            item <- f
          }yield item.name
         Ok(Json.toJson(r)).as(JSON)
      }
    }
   }
}
