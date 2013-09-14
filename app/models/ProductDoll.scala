package models

import org.squeryl.{Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import play.api.Play._
import org.jsoup.Jsoup
import play.api.libs.ws.{Response, WS}
import scala.xml.NodeSeq
import play.api.libs.concurrent.Promise
import play.api.libs.json._
import play.api.libs.json.Json
import java.sql.Timestamp
import play.api.libs.functional.syntax._



/**
 * Class for representing Extern product entity
 * */
case class ProductDoll(@Column("rowid") id : Int, ref : Option[String], label : Option[String],
                   price : Option[Double], tva_tx: Option[Double], unite : String)  {
//  implicit val productDollWrites = new Writes[ProductDoll] {
//    def writes(pd: ProductDoll): JsValue = {
//      Json.obj(
//        "id" -> pd.id,
//        "ref" -> pd.ref,
//        "label" -> pd.label,
//        "price" -> pd.price,
//        "tva_tx"-> pd.tva_tx,
//        "unite" -> pd.unite
//      )
//    }
//  }
//  implicit val productDollListWrite = Json.writes[List[ProductDoll]]
//  implicit val productDollReads = Json.reads[ProductDoll]
//  implicit val productDollWrites = Json.writes[ProductDoll]
//  implicit val productDollFormat = Format(productDollReads,productDollWrites)

//
//  implicit val productDollWrites : Writes[ProductDoll]= (
//    (__ \ "fk_societe").write[Int] and
//      (__ \ "order_date").write[String] and
//      (__ \ "date_creation").write[Timestamp] and
//      (__ \ "fk_user_author").write[Int] and
//      (__ \ "fk_statut").write[Int] and
//      (__ \ "tva").write[Double] and
//      (__ \ "total_ht").write[Double] and
//      (__ \ "total_ttc").write[Double] and
//      (__ \ "note").write[String]
//    )(unlift(ProductDoll.unapply))

// implicit val productDollListWrites = Writes.traversableWrites[ProductDoll](productDollWrites)

  // TODO: Add fields for product entity
}

//implicit object productDollFormat extends Format[ProductDoll] {
//
//  def reads(json: JsValue) = ProductDoll(
//    (json \ "id").as[Int],
//    (json \ "ref").as[Option[String]],
//    (json \ "label").as[Option[String]],
//    (json \ "price").as[Option[Double]],
//    (json \ "tva_tx").as[Option[Double]],
//    (json \ "unite").as[String]
//  )
//  def writes(pd: ProductDoll) = JsObject(Seq(
//       "id" -> JsNumber(pd.id),
//       "ref" -> JsString(pd.ref.get),
//       "label" -> JsString(pd.label.get),
//         "price" -> JsNumber(pd.price.get),
//         "tva_tx"-> JsNumber(pd.tva_tx.get),
//         "unite" -> JsString(pd.unite)
//  ))
//}



case class ProductPrice(@Column("rowid") id : Int, fk_product : Int, price_level : Int, price: Double, price_ttc:Double,
                        tva_tx: Double){
  implicit val productPriceWrites = new Writes[ProductPrice] {
    def writes(pp: ProductPrice): JsValue = {
      Json.obj(
        "id" -> pp.id,
        "fk_product" -> pp.fk_product,
        "price_level" -> pp.price_level,
        "price" -> pp.price,
        "price_ttc"-> pp.price_ttc,
        "tva_tx" -> pp.tva_tx
      )
    }
  }
}

object ProductDoll extends Schema {
  val productTable: Table[ProductDoll] = table[ProductDoll]("llx_product")
  val productpriceTable : Table[ProductPrice] = table[ProductPrice]("llx_product_price")
  //implicit val pWrite = Json.format[(Int,String,String,Double,Double)]
  implicit val productDollFormat = Json.format[ProductDoll]
  implicit val productWithPricesTuple = (
    (__ \ '_1).write[Int] and
    (__ \ '_2).write[Option[String]] and
    (__ \ '_3).write[Option[String]] and
    (__ \ '_4).write[Option[Double]] and
    (__ \ '_5).write[Double]
    ).tupled : Writes[(Int,Option[String],Option[String],Option[Double],Double)]


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


  def getProductsWithPrices(customer_id: Int,offset: Int, pageLength: Int): JsValue = {
    val customer: CustomerDoll = CustomerDoll.getById(customer_id)
     val json  = transaction(DollConn.doll_session(current)){
      val products  = from(productTable,productpriceTable)((s,pr)=>
      where(s.id===pr.fk_product and pr.price_level===customer.price_level) select(s.id, s.ref, s.label,s.tva_tx, pr.price)
      ).page(offset,pageLength)
      Json.toJson(products)
    }
    json
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
    {
      val image = (node \ "id_default_image" )(0)
      image.attribute(image.getNamespace("xlink"),"href").getOrElse(None).toString
    }
    )
  }

//  def getProductsWithPricesInJson(resp : scala.xml.NodeSeq) = {
//    val products:Promise[Response] = WS.url("http://localtest.my/prestashop/api/"+obj+"/")
//      .withAuth("GIS6VC2DAXZWZN8XQEET1SSJ6L0CCTPG", "", com.ning.http.client.Realm.AuthScheme.BASIC)
//      .get
//
//  }
//
}