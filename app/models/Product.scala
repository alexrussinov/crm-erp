package models
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import lib.{Normalize, CustomIO, Convertion, mat}
import java.io.File
import scala.concurrent.Future
import fly.play.s3.{S3, BucketItem}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._
import scala.util._

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

case class ProductImage(
name : String,
path : String
)

object Product {
  //write for json generation
  implicit val productWrites : Writes[Product]= (
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

  implicit val productReads : Reads[Product] = (
    (__ \ 'id).read[Option[Int]]and
      (__ \ 'reference).read[String]and
      (__ \ 'label).read[String]and
      (__ \ 'description).read[Option[String]]and
      (__ \ 'image_url).read[Option[String]]and
      (__ \ 'unity).read[String]and
      (__ \ 'category_id).read[Option[Int]]and
      (__ \ 'supplier_id).read[Int]and
      (__ \ 'manufacture).read[Option[String]]and
      (__ \ 'reference_supplier).read[Option[String]]and
      (__ \ 'multi_price).read[Boolean]and
      (__ \ 'tva_rate).read[Double]and
      (__ \ 'price_supplier).read[Double]and
      (__ \ 'base_price).read[Double]
    ) (Product.apply _)

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

  //search for available image ... for the product in a local folder
  def searchForImage(product : Product, pathToImageFolder : String) : List[ProductImage] = {
    def nrmlz(str : String): String ={
      "_".r.replaceAllIn(str," ").toLowerCase
    }
      val listOfFiles : Stream[FileImp] = CustomIO.getFileTree(new File("public/"+pathToImageFolder)) filter (_.isFile) map (f=>FileImp(f.getName,("assets/" + pathToImageFolder+ "/" + f.getName)))

     val result = for{entry <- listOfFiles.sortBy(f=>f.name)
                     if(nrmlz(entry.name).contains(Normalize.removeDiacritics(product.label.split("/")(0)).toLowerCase))
    } yield (ProductImage(entry.name,entry.path))
    result.toList
  }

  def searchForImageS3(product : Product) : Future[List[ProductImage]] = {
    def nrmlz(str : String): String ={
      "_".r.replaceAllIn(str," ").toLowerCase
    }
    val bucket = S3("itcpluswebproducts")
    val items : Future[Iterable[BucketItem]]  =  bucket.list("szubryt/")

    val res = items.map{f=>
      //create list of file names
      val listOfNames : Iterable[String] =  for{
        item <- f
      }yield(item.name)
      //create lis of ProductImage
      val result : List[ProductImage] = (for{
                  name <- listOfNames
                  if(nrmlz(name).contains(Normalize.removeDiacritics(product.label.split("/")(0)).toLowerCase))
                }yield (ProductImage(name,"https://s3.amazonaws.com/itcpluswebproducts/"+name))).toList

      result
    }
    res
  }

  def searchForImage2(product : Product, items: Iterable[BucketItem]) : List[ProductImage] = {
    // normalize the string by replacing undescore with espace and convert all characters to lower case
    def nrmlz(str : String): String ={
      "_".r.replaceAllIn(str," ").toLowerCase
    }
    //create list of file names
    val listOfNames : Iterable[String] =  for{
      item <- items
    }yield(item.name)

    (for{
      name <- listOfNames
      if(nrmlz(name).contains(Normalize.removeDiacritics(product.label.split("/")(0)).toLowerCase))
    }yield (ProductImage(name,"https://s3.amazonaws.com/itcpluswebproducts/"+name))).toList

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

  def autoInc = reference ~ label ~ description ~ image_url ~ unity ~ category_id ~ supplier_id ~
    manufacture ~ reference_supplier ~ multi_price ~ tva_rate ~ price_supplier ~ base_price returning id

  /**
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(ProductTable.length).first

  def getAll(implicit s : Session) : List[Product] = {
    (for(p <- ProductTable) yield p ).list
  }
   // we must have customer discount define for all suppliers(supplier_id)  even if 0, for this method to work

  def getAllProductsWithCustomerPrices (customer_id : Int)(implicit s : Session) = {
         // list of all products
         val products : List[Product] = (for (p <- ProductTable) yield (p)).list
    if(customer_id > 0){
          // list of all discounts available for this customer
         val discounts : List[CustomerDiscount] = CustomerDiscount.getDiscountsByCustomerId(customer_id)
      // helper function to retrieve customer discount, if there is no, return 0 discount
      def searchForDiscount(p: Product): List[CustomerDiscount] = {
        val result = for{
          d<-discounts if(d.supplier_id == p.supplier_id)
        }yield d
        if(result!=Nil)
          result
        else
          List(CustomerDiscount(0,0,0.00))
      }
         if (!discounts.isEmpty){
    for{ p <- products
         d <- searchForDiscount(p) //if(p.supplier_id == d.supplier_id) //we have replaced this condition directly to the yield expression to handle the case when customer discounts exist, but not for this supplier
    }  yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
            p.manufacture, p.tva_rate,
      if(d.discount != 0.00)
        mat.round(p.base_price*((100-d.discount)/100))
      else
        p.base_price )

          }
      else {
           for (p <- products) yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
           p.manufacture, p.tva_rate, p.base_price )
         }
       }
    else
      for (p <- products) yield (p.id,p.reference, p.label, p.description, p.image_url, p.unity, p.category_id, p.supplier_id,
         p.manufacture, p.tva_rate, p.base_price )
  }
   // Retrieve product with fields set for customer
  def getProductByIdForCustomer(product_id : Int, customer_id : Int)(implicit s : Session): ProductCustomer ={
    val discounts = CustomerDiscount.getDiscountsByCustomerId(customer_id)
    val products : List[Product] = (for(p <- ProductTable)yield(p)).list
     // helper function
     def searchForDiscount(p: Product): List[CustomerDiscount] = {
       val result = for{
         d<-discounts if(d.supplier_id == p.supplier_id)
       }yield d
       if(result!=Nil)
         result
       else
         List(CustomerDiscount(0,0,0.00))
     }
    if(discounts.isEmpty){
      val pr : List[ProductCustomer] = for {
        p <- products
        d <- searchForDiscount(p)
      }yield ProductCustomer(p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,p.base_price)
      pr.head
    }

   else { val pr : List[ProductCustomer] = for {
      p <- products if(p.id.get == product_id)
      d <- searchForDiscount(p) //if(p.supplier_id == d.supplier_id)  we have replaced this condition directly to the yield expression to handle the case when customer discounts exist, but not for this supplier
    }yield ProductCustomer(p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,
        if(d.discount != 0.00)
          mat.round(p.base_price*((100-d.discount)/100))
        else p.base_price )
    pr.head
    }

//    val productCustomer  =  for{
//         p <- ProductTable if(id === product_id)
//         d <- discounts    if(p.supplier_id == d.supplier_id)
//        }yield (p.id,p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.tva_rate,mat.round(p.base_price*((100-d.discount)/100))))
//    pr.head
  }

  def getById(id : Int)(implicit s : Session) : Product = {
   val result = (for{
      p <- ProductTable
      if(p.id === id)
    }yield p).list

    result.head
  }

  def getAllManufacturers(implicit session : Session) : List[Option[String]] = {
    val res = (for (product <- ProductTable) yield product.manufacture).list.toSet
    res.toList

  }

  def updateProduct(product : Product)(implicit s : Session)={
    ProductTable.filter(_.id === product.id).update(product)
  }

  def add(p : Product)(implicit s : Session)={
    ProductTable.autoInc.insert(p.reference,p.label,p.description,p.image_url,p.unity,p.category_id,p.supplier_id,p.manufacture,
    p.reference_supplier,p.multi_price,p.tva_rate,p.price_supplier,p.base_price)
  }

  def deleteProduct(id : Int)(implicit s: Session) = {
    ProductTable.where(_.id===id).delete
  }


}


