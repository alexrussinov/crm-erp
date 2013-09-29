package controllers

import play.api.mvc._
import jp.t2v.lab.play2.auth.{Auth, LoginLogout}
import models._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import org.scala_tools.time.Imports._
import lib.CustomIO
import play.api.Play
import scala.util.{Failure, Success}
import play.api.libs.concurrent.Execution.Implicits._
import fly.play.s3.{BucketFile, BucketItem, S3}
import scala.concurrent.Future
import play.api.libs.iteratee.Iteratee
import java.io.ByteArrayOutputStream
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.BodyParsers.parse.Multipart._
import play.api.mvc.BodyParsers.parse.multipartFormData


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 18/07/13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
object Products extends  Controller with LoginLogout with AuthConf with Auth {

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

  def getProductsInJson = authorizedAction(NormalUser){ user => implicit request =>
    val json = DB.withSession { implicit session =>

      val result = ProductTable.getAllProductsWithCustomerPrices(user.customer_id.getOrElse(0))
      Json.toJson(result)
    }
    Ok(json)

  }

  // Action that provide a view with a form for importing products from CSV
  def uploadProductsCsvForm = authorizedAction(Administrator){ user => implicit request =>
    Ok(views.html.importproducts(user))
  }

  // Action that performs upload and import

  def uploadFile = Action(parse.multipartFormData) { request =>
    val result   = request.body.file("products") map {tmp =>
    val dt  = new DateTime
    val filename = tmp.filename
      import java.io.File
      tmp.ref.moveTo(new File("imports/"+filename.split("\\.")(0)+dt.toString("YYMMDDHMS")+".csv"))
      CustomIO.getFileTree(new File("imports/")) filter (_.isFile) map (f=>Map(f.getName->("imports/"+f.getName)))
    }
    //Ok(Json.toJson(result))
    Redirect(routes.Products.uploadProductsCsvForm)
    }

   def getFilesToImportJson = Action {request =>
     import java.io.File
       Ok(Json.toJson(CustomIO.getFileTree(new File("imports/")) filter (_.isFile) map (f=>FileImp(f.getName,("imports/"+f.getName)))))
   }

  def importProductsFromCsv(path : String) = Action {request =>
    val res = Imports.importFromCsvWithHeaders(path,";")
    val products : Seq[Product] = Product.productsFromSource(res)
//    val productsWithFoto = products.map(f=>{
//     val images = Product.searchForImage(f,"images/products/szubryt")
//      if(!images.isEmpty) Product(f.id,f.reference,f.label,f.description,Some(images.head.path),f.unity,
//        f.category_id,f.supplier_id,f.manufacture,f.reference_supplier,f.multi_price,f.tva_rate,f.price_supplier,f.base_price)
//      else f
//    }
//    )
//    try {  DB.withSession { implicit session =>
//           productsWithFoto.foreach(ProductTable.insert)
//      }
//     // Ok(""+products.length)
//      Redirect(routes.Catalogue.listProducts)
//    }
//    catch{
//      case e: Exception => BadRequest(e.toString)
//    }

    /*version with future*/

    val bucket = S3("itcpluswebproducts")
    val items : Future[Iterable[BucketItem]]  =  bucket.list("szubryt/")

    Async{
      items.map{o=>
        val productsWithPhoto : Seq[Product] = products.map{f=>
          val images = Product.searchForImage2(f,o)
          if(!images.isEmpty) Product(f.id,f.reference,f.label,f.description,Some(images.head.path),f.unity,
            f.category_id,f.supplier_id,f.manufacture,f.reference_supplier,f.multi_price,f.tva_rate,f.price_supplier,f.base_price)
          else f
        }
        try{  DB.withSession { implicit session =>
          productsWithPhoto.foreach(ProductTable.insert) }

        Redirect(routes.Catalogue.listProducts)
        }
        catch{
          case e: Exception => BadRequest(e.toString)
        }
      }
    }
  }

  def uploadProductsImages = authorizedAction(Administrator){ user => implicit request =>
  Ok(views.html.products_images_upload(user))
  }

  def productFiche(id : Int) = authorizedAction(Administrator){ user => implicit request =>
  Ok(views.html.product_fiche(user,id))
  }

  def getProductByIdInJson(id : Int) = authorizedAction(Administrator){ user => implicit request =>
    val product =  DB.withSession { implicit session => ProductTable.getById(id)}
    Ok(Json.toJson(product))
  }
  /* handle image file upload and product update with appropriate image url */
//  val imageUploadForm = Form(
//  tuple(
//  "product_id" -> number,
//  "image" ->file ignored
//  )
//  )

  // hadle file part as Array[Byte]
  def handleFilePartAsByteArray: PartHandler[FilePart[Array[Byte]]] =
    handleFilePart {
      case FileInfo(partName, filename, contentType) =>
        // simply write the data to the a ByteArrayOutputStream
        Iteratee.fold[Array[Byte], ByteArrayOutputStream](
          new ByteArrayOutputStream()) { (os, data) =>
          os.write(data)
          os
        }.mapDone { os =>
          os.close()
          os.toByteArray
        }
    }
   // custom body parser to handle file part as Array[Byte]
  def multipartFormDataAsBytes:BodyParser[MultipartFormData[Array[Byte]]] =
    multipartFormData(handleFilePartAsByteArray)


   /* Upload image to the server and update product url to the image */
  def updateProductImage2() = authorizedAction(Administrator){ user => request =>

   val product_id : Int = (request.body.asMultipartFormData map {f=>f.asFormUrlEncoded.get("product_id")}).get.get.head.toInt
   val product = DB.withSession { implicit session => ProductTable.getById(product_id)}
   val file  = request.body.asMultipartFormData map {f => f.file("image").map {tmp =>
      val filename = tmp.filename
      import java.io.File
      val file = new File("public/images/products/"+" ".r.replaceAllIn(product.manufacture.getOrElse("default").toLowerCase,"_")+"/"+filename)
      tmp.ref.moveTo(file,true)
      file
    }}
    DB.withSession { implicit session => ProductTable.filter(_.id === product_id).map(_.image_url).update(file.map(f=>"public".r.replaceAllIn(f.get.getPath,"assets")))  }
    //Ok("")
    Redirect(routes.Products.productFiche(product_id))
  }
  /*Upload image to S3 and update product url to the image */
  def updateProductImage = Action(multipartFormDataAsBytes){request =>
    val bucket = S3("itcpluswebproducts")
    val product_id : Int = (request.body.asFormUrlEncoded.get("product_id")).get.head.toInt
    val bucket_prefix = (request.body.asFormUrlEncoded.get("bucket_prefix")).get.headOption
    val product = DB.withSession { implicit session => ProductTable.getById(product_id)}
    request.body.files foreach{
      case FilePart(key,filename,content,bytes)=>bucket + BucketFile(bucket_prefix.getOrElse("other")+"/"+filename,content.getOrElse("application/octet-stream"),bytes)
        DB.withSession { implicit session => ProductTable.filter(_.id === product_id).map(_.image_url).update(Some("https://s3.amazonaws.com/itcpluswebproducts/"+bucket_prefix.getOrElse("other")+"/"+filename))  }
    }
    Redirect(routes.Products.productFiche(product_id))
  }

  def updateProduct = authorizedAction(Administrator){ user => request =>
    request.body.asJson.map{json =>
      json.validate[Product].map{
        case product => {
          play.api.db.slick.DB.withSession{implicit session =>
          ProductTable.updateProduct(product)
          }
          val updatedProduct = play.api.db.slick.DB.withSession{implicit session => ProductTable.getById(product.id.get)}
          Ok(Json.toJson(updatedProduct))
        }
      }.recoverTotal(e=>BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }.getOrElse(BadRequest("Expecting Json Data"))
  }

}

object Categories extends  Controller with LoginLogout with AuthConf with Auth {

  def manageCategories = authorizedAction(NormalUser){ user => implicit request =>
  Ok(views.html.categories(user))
  }

}
// TODO Evolution for slick doesn't work
//TODO We need controller to import products from csv
//TODO controller to update products from csv
//TODO controllers to manage categories