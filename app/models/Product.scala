package models
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import lib.{Convertion, mat}

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/05/13
 * Time: 11:33
 * Class represent Product entity
 */
case class ProductCustomer(
 id : Option[Int] = None,
 reference : String,
 label : String,
 description : Option[String],
 image_url : Option[String],
 unity : String,
 category_id : Option[Int],
 tva_rate : Double,
 price : Double){
 val price_ttc : Double = mat.round(this.price*((100+this.tva_rate)/100))
}
object ProductCustomer

case class Product (
   id : Option[Int] = None,
   reference : String,
   label : String,
   description : Option[String],
   image_url : Option[String],
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
      (__ \ 'description).write[Option[String]]and
      (__ \ 'image_url).write[Option[String]]and
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

    // enable us to write Json of this type
  implicit val productWithCustomerPriceFormat = (
    (__ \ 'id).write[Option[Int]]and
      (__ \ 'reference).write[String]and
      (__ \ 'label).write[String]and
      (__ \ 'description).write[Option[String]]and
      (__ \ 'image_url).write[Option[String]]and
      (__ \ 'unity).write[String]and
      (__ \ 'category_id).write[Option[Int]]and
      (__ \ 'supplier_id).write[Int]and
      (__ \ 'manufacture).write[Option[String]]and
      (__ \ 'tva_rate).write[Double]and
      (__ \ 'price).write[Double]
    ).tupled : Writes[(Option[Int], String, String, Option[String], Option[String], String, Option[Int], Int, Option[String], Double, Double)]

  // generates seq. of products from src, we use this method while importing products from csv
  def productsFromSource(src : Seq[Map[String,String]]) : Seq[Product] = {
        src map (s=>Product(None,s("reference"),s("label"),Some(s("description")),Some(s("image_url")),s("unity"),Convertion.prse[Int](s("category_id")),Convertion.prse[Int](s("supplier_id")).get,
          Some(s("manufacture")),Some(s("reference_supplier")),Convertion.prse[Boolean](s("multi_price")).get,Convertion.prse[Double](s("tva_rate")).get,Convertion.prse[Double](s("price_supplier")).get,Convertion.prse[Double](s("base_price")).get))
  }
}

object ProductTable extends Table[Product]("t_products"){


  def id = column[Option[Int]]("id",O.PrimaryKey, O.AutoInc)
  def reference = column[String]("reference")
  def label = column[String]("label")
  def description = column[Option[String]]("description")
  def image_url = column[Option[String]]("image_url")
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
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(ProductTable.length).first

  def getAll(implicit s : Session) : List[Product] = {
    (for(p <- ProductTable) yield p ).list
  }

  def getAllProductsWithCustomerPrices (customer_id : Int)(implicit s : Session) = {
         val products : List[Product] = (for (p <- ProductTable) yield (p)).list
    if(customer_id > 0){
         val discounts : List[CustomerDiscount] = CustomerDiscount.getDiscountsByCustomerId(customer_id)
         if (!discounts.isEmpty){
    for{ p <- products
         d <- discounts if(p.supplier_id == d.supplier_id)
    } yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
            p.manufacture, p.tva_rate, mat.round(p.base_price*((100-d.discount)/100)) )
          }
      else {
           for (p <- products) yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
           p.manufacture, p.tva_rate, p.base_price )
         }
       }
    else for (p <- products) yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
         p.manufacture, p.tva_rate, p.base_price )
  }
   // Retrieve product with fields set for customer
  def getProductById(product_id : Int, customer_id : Int)(implicit s : Session): ProductCustomer ={
    val discounts = CustomerDiscount.getDiscountsByCustomerId(customer_id)
    val products : List[Product] = (for(p <- ProductTable)yield(p)).list
    if(discounts.isEmpty){
      val pr : List[ProductCustomer] = for {
        p <- products if(p.id.get == product_id)
      }yield ProductCustomer(p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,p.base_price)
      pr.head
    }

   else { val pr : List[ProductCustomer] = for {
      p <- products if(p.id.get == product_id)
      d <- discounts if(p.supplier_id == d.supplier_id)
    }yield ProductCustomer(p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,mat.round(p.base_price*((100-d.discount)/100)))
    pr.head
    }

//    val productCustomer  =  for{
//         p <- ProductTable if(id === product_id)
//         d <- discounts    if(p.supplier_id == d.supplier_id)
//        }yield (p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,mat.round(p.base_price*((100-d.discount)/100))))
//    pr.head
  }

  def getAllManufacturers(implicit session : Session) : List[Option[String]] = {
    val res = (for (product <- ProductTable) yield product.manufacture).list.toSet
    res.toList

  }


}


