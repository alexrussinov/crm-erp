package models

import org.squeryl.{Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import play.api.Play._
import org.jsoup.Jsoup
import play.api.libs.ws.{Response, WS}
import scala.xml.NodeSeq
import play.api.libs.concurrent.Promise


/**
 * Class for representing Extern product entity
 * */
case class ProductDoll(@Column("rowid") id : Int, ref : Option[String], label : Option[String],
                   price : Option[Double], tva_tx: Option[Double], unite : String)  {


  // TODO: Add fields for product entity
}

case class ProductPrice(@Column("rowid") id : Int, fk_product : Int, price_level : Int, price: Double, price_ttc:Double,
                        tva_tx: Double)

object ProductDoll extends Schema {
  val productTable: Table[ProductDoll] = table[ProductDoll]("llx_product")
  val productpriceTable : Table[ProductPrice] = table[ProductPrice]("llx_product_price")

  def getById(id : Int) : ProductDoll = {
    transaction(DollConn.doll_session(current)){
      val product = from(productTable)(s => where (s.id === id) select(s))
      product.head
    }
  }

  def getProductPrice(product_id : Int, price_level : Int): ProductPrice = {
    if(price_level != 0)
    transaction(DollConn.doll_session(current)){
      val productPrice = from(productpriceTable)(s => where(s.fk_product === product_id and s.price_level === price_level)
        select(s))
      productPrice.head
    }
    else ProductPrice(0,0,0,0.00,0.00,0.00)



  }



}

case class ProductPresta(ref_fournisseur: String, id_fournisseur : Int, categorie_fournisseur : Int, price_fournisseur : Double,
                          description : String, image : String) {

}

object ProductPresta {
  def fromXml(node : scala.xml.NodeSeq): ProductPresta = {
    ProductPresta(
      (node \ "reference").text.toString,
      (node \ "id" ).text.toInt,
      (node \ "id_category_default").text.toInt,
      (node \ "price").text.toDouble,
      Jsoup.parse((node \ "description_short" \ "language").text.toString).body().text(),
      (node \ "id_default_image").text.toString
    )
  }

//  def listProducts(resp : scala.xml.NodeSeq) = {
//    val products:Promise[Response] = WS.url("http://localtest.my/prestashop/api/"+obj+"/")
//      .withAuth("GIS6VC2DAXZWZN8XQEET1SSJ6L0CCTPG", "", com.ning.http.client.Realm.AuthScheme.BASIC)
//      .get
//
//  }
//
}