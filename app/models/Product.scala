package models
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/05/13
 * Time: 11:33
 * Class represent Product entity
 */
case class Product (
   id : Option[Int] = None,
   reference : String,
   label : String,
   description : String,
   image_url : String,
   unity : String,
   category_id : Option[Int],
   supplier_id : Int,
   manufacture : Option[String],
   reference_supplier : Option[String],
   multi_price : Boolean,
   tva_rate : Double,
   price_supplier : Double,
   base_price : Double

)

object Product {
  //reads for json generation
  implicit val productFormat : Writes[Product]= (
    (__ \ 'id).write[Option[Int]]and
      (__ \ 'reference).write[String]and
      (__ \ 'label).write[String]and
      (__ \ 'description).write[String]and
      (__ \ 'image_url).write[String]and
      (__ \ 'unity).write[String]and
      (__ \ 'category_id).write[Option[Int]]and
      (__ \ 'supplier_id).write[Int]and
      (__ \ 'manufacture).write[Option[String]]and
      (__ \ 'reference_supplier).write[Option[String]]and
      (__ \ 'multi_price).write[Boolean]and
      (__ \ 'tva_rate).write[Double]and
      (__ \ 'price_supplier).write[Double]and
      (__ \ 'base_price).write[Double]
    )(unlift(Product.unapply))
}

object ProductTable extends Table[Product]("t_products"){


  def id = column[Option[Int]]("id",O.PrimaryKey, O.AutoInc)
  def reference = column[String]("reference")
  def label = column[String]("label")
  def description = column[String]("description")
  def image_url = column[String]("image_url")
  def unity =column[String]("unity")
  def category_id = column[Option[Int]]("category_id")
  def supplier_id = column[Int]("supplier_id")
  def manufacture =  column[Option[String]]("manufacture")
  def reference_supplier = column[Option[String]]("reference_supplier")
  def multi_price = column[Boolean]("multi_price")
  def tva_rate = column[Double]("tva_rate")
  def price_supplier = column[Double]("price_supplier")
  def base_price = column[Double]("base_price")

  def * = id ~ reference ~ label ~ description ~ image_url ~ unity ~ category_id ~ supplier_id ~
    manufacture ~ reference_supplier ~ multi_price ~ tva_rate ~ price_supplier ~ base_price  <> (Product.apply _ , Product.unapply _)

  def autoInc = * returning id

  /**
   * Count all computers
   */
  def count(implicit s:Session): Int =
    Query(ProductTable.length).first

}


