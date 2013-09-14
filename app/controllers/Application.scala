package controllers

import models._
import play.api._
import data.Form
import play.api.data.Forms._
import data._
import play.api.data.validation.Constraints._
import db.DB
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reflect.classTag
import scala.Some
import lib.mat
import org.apache.commons.mail.{DefaultAuthenticator, MultiPartEmail}


//import com.codahale.jerkson.Json
import org.squeryl.PrimitiveTypeMode._
import play.api.Play.current
import jp.t2v.lab.play2.auth._
import play.api.Play._
import play.api.Routes




object Application extends Controller with LoginLogout with AuthConf with Auth{
  
  def index = authorizedAction(NormalUser){ user => implicit request =>
    Ok(views.html.index(user))
  }

 //get categories for a left sidebar categories tree
  def getCategoriesInJson = Action{
    val result = Json.toJson(play.api.db.slick.DB.withSession { implicit session =>CategoryTable.getAll})
    Ok(result).as(JSON)
  }

  //get list of manufacturers(marques)

  def getManufacturersInJson = Action{
    val result = Json.toJson(play.api.db.slick.DB.withSession{implicit session =>ProductTable.getAllManufacturers})
    Ok(result).as(JSON)
  }

  def getProducts = Action {

    val json = transaction(DollConn.doll_session(current)) {
      val products = from(ProductDoll.productTable)(productTable =>
        select(productTable)
      )
      Json.toJson(products)
    }
    Ok(json).as(JSON)
  }

  def searchProducts(value : Option[String]) = Action {
    val json = transaction(DollConn.doll_session(current)){
      val products = from(ProductDoll.productTable) ( s => where ( (s.ref like value.map(_+ "%").?) or
        (s.label like value.map("%"+ _ + "%").?) ) select(s))
      Json.toJson(products)
    }

    Ok(json).as(JSON)
  }

  def showProducts = authorizedAction(NormalUser){ user => implicit request =>
    Ok(views.html.products(user))
  }

  val contactForm = Form(
    tuple(
      "email"-> email,
      "subject"->text,
      "message"->text,
      "name"->text
    )
  )

  def contactPage = authorizedAction(NormalUser){ user => implicit request =>
  Ok(views.html.contact(user,contactForm))
  }


  def contactFormSendMessage = authorizedAction(NormalUser){ user => implicit request =>
  contactForm.bindFromRequest.fold (
    formWithErrors=>BadRequest(views.html.contact(user,formWithErrors)),
    value =>{
    // Create the email message
    val email = new MultiPartEmail()

    email.setHostName("smtp.googlemail.com")
    email.setSmtpPort(465)
    email.setAuthenticator(new DefaultAuthenticator("imexbox@gmail.com", "eU0mwqAa"))
    email.setSSL(true)
    email.addTo("imexbox@gmail.com")
    email.setFrom(value._1,value._1)
    email.setSubject("Nouveau message de "+value._4 +" depuis formulaire de contact: "+value._2)
    email.setMsg(value._3)
      // send the email
    email.send()
    Redirect(routes.Application.contactPage).flashing("message"->"Votre message envoyé avec success!")
     }
    )
  }

  def dashboard = authorizedAction(NormalUser){ user => implicit request =>
     Ok(views.html.dashboard(user))
  }

  /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(Users.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  /** Alter the login page action to suit your application. */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm, Users("", "", 0,Some(0))))
  }

  /**
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `Result`,
   * If you import `jp.t2v.lab.play20.auth._`, you can add a procedure like the following.
   *
   *   gotoLogoutSucceeded.flashing(
   *     "success" -> "You've been logged out"
   *   )
   */
  def logout = Action { implicit request =>
  // do something...
    gotoLogoutSucceeded.flashing(
    "success" -> "You've been logged out"
    )

  }

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   *
   * Since the `gotoLoginSucceeded` returns `Result`,
   * If you import `jp.t2v.lab.play20.auth._`, you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors, Users("","", 0,Some(0)))),
      user => gotoLoginSucceeded(user.get.id)
    )
  }

  def javascriptRoutes = Action { implicit request =>

    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.Orders.searchProducts, routes.javascript.Application.getProducts,
        routes.javascript.Orders.getOrderLinesInJson
      )
    ).as(JAVASCRIPT)
  }


  // create new supplier


  val createsupplierform : Form[Supplier] = Form(
  mapping(
  "name" -> nonEmptyText,
  "address"-> nonEmptyText,
  "tel" -> nonEmptyText,
  "email"-> email.verifying(nonEmpty)
  )(Supplier.apply)(Supplier.unapply)
  )

  def createSupplierForm = authorizedAction(Administrator){ user => implicit request =>
      Ok(views.html.createsupplier(createsupplierform,user))
  }

  def createSupplier = authorizedAction(Administrator) { user => implicit request =>
  createsupplierform.bindFromRequest.fold(
  formWithErrors => BadRequest(views.html.createsupplier(formWithErrors,user)),
  supplier => {
    val id = Supplier(supplier.name,supplier.address, supplier.tel, supplier.email).create_supplier
    Redirect(routes.Application.listSuppliers)
  }
  )


  }

  def listSuppliers = authorizedAction(Administrator) {user => implicit request =>
             Ok(views.html.listsuppliers(user))
  }

  def getSuppliersInJson = Action {
        //Ok(Supplier.getAllInJson)
    val suppliers = Json.toJson( play.api.db.slick.DB.withSession{implicit session =>
        CompanyTable.getAllSuppliers.map(s=>s.toCompanyJson)
        })
    Ok(suppliers).as(JSON)
  }



}

trait AuthConf extends AuthConfig {
  /**
   * A type that is used to identify a user.
   * `String`, `Int`, `Long` and so on.
   */
  type Id = Int

  /**
   * A type that represents a user in your application.
   * `User`, `Account` and so on.
   */
  type User = Users

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Permission {
   * case object Administrator extends Permission
   * case object NormalUser extends Permission
   */

  type Authority = Permission

  /**
   * A `ClassManifest` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  // val idManifest: ClassManifest[Id] = classManifest[Id]  // This working with play 2.0.4

  val idTag = classTag[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id): Option[User] = Users.findById(id)

  /**
   * Where to redirect the user after a successful login.
   */
  //def loginSucceeded(request: RequestHeader): Result = Redirect(routes.Application.showProducts)
  def loginSucceeded(request: RequestHeader): Result = {
    val uri = request.session.get("access_uri").getOrElse(routes.Application.index.url.toString)
    Redirect(uri).withSession(request.session - "access_uri")
  }

  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader): Result = Redirect(routes.Application.index)

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
   //def authenticationFailed(request: RequestHeader): Result = Redirect(routes.Application.login)

  def authenticationFailed(request: RequestHeader): Result =
    Redirect(routes.Application.login).withSession("access_uri" -> request.uri)

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed(request: RequestHeader): Result = Forbidden("no permission")

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority): Boolean =
    (user.admin, authority) match {
      case (1, _) => true
      case (0, NormalUser) => true
      case _ => false
    }

  /**
   * Whether use the secure option or not use it in the cookie.
   * However default is false, I strongly recommend using true in a production.
   */
 // override lazy val cookieSecureOption: Boolean = play.api.Play.current.configuration.getBoolean("auth.cookie.secure").getOrElse(true)

}

object AccountCreation extends Controller with LoginLogout with AuthConf with Auth {

  val createaccountform = Form (
  mapping(
  "email" -> email.verifying("User with such email existe", e => !Users.findByEmail(e).isDefined),
  "password" -> nonEmptyText(minLength = 6),
  "admin" -> number,
  "customer_id"-> optional(number)
  )(Users.apply)(Users.unapply)

  )

  def createAccountForm = authorizedAction(Administrator){ user => implicit request =>
    Ok(views.html.createaccount(createaccountform, "Create user", user))
  }

  def createAccount = authorizedAction(Administrator){ user => implicit request =>
     createaccountform.bindFromRequest.fold(
       formWithErrors => BadRequest(views.html.createaccount(formWithErrors, "Errors!!!", user)),
       user => {
         val us : Users = Users.createUser(user.email, user.pass, user.admin, user.customer_id)
         // We need to create default 0.00 discounts for this user for all existing suppliers
         val suppliers = Supplier.getAll
         for(supplier <- suppliers)CustomerDiscount(us.customer_id.getOrElse(0),supplier.id,0.00).create_discount

         Redirect(routes.Catalogue.listProducts)
       }
     )
  }

  def listUsers = authorizedAction(Administrator){ user => implicit request =>
      Ok(views.html.listusers(user))
  }

  def userFiche(id : Int) = authorizedAction(NormalUser){ user => implicit request =>
     Ok(views.html.user_fiche(user,id))
  }

  def getUserInJson(id : Int) = Action {
    implicit val userWrites = (
      (__ \'id).write[Int] and
        (__ \ 'email).write[String] and
        (__ \ 'customer_id).write[Option[Int]]
      ).tupled : Writes[(Int,String,Option[Int])]
    val user = Users.findById(id).get
    val json = Json.toJson((user.id,user.email,user.customer_id))
    Ok(json).as(JSON)
  }

  def getUsersInJson = Action {
    implicit val userWrites = (
      (__ \'id).write[Int] and
      (__ \ 'email).write[String] and
      (__ \ 'admin).write[Int] and
      (__ \ 'customer_id).write[Option[Int]]
      ).tupled : Writes[(Int,String,Int,Option[Int])]

    val users : List[(Int,String,Int,Option[Int])] = Users.findAll.map(u=>(u.id,u.email,u.admin,u.customer_id))
    val json = Json.toJson(users)

    Ok(json).as(JSON)
  }
}
    // TODO We need a view that lists all users and a view that represents user(customer) fich, user data modification
object Catalogue extends Controller with LoginLogout with AuthConf with Auth {
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

      /**
       *
        * @param customer_id
       * @return All products with prices as json for specified customer id,
       *         light form (with out some fields that's not necessary for the customer view)
       */

  def getProductsWithPricesInJson(customer_id: Int) = authorizedAction(NormalUser){ user => implicit request =>
//                val result = ProductDoll.getProductsWithPrices(customer_id,0,10000)
                val result = Json.toJson(play.api.db.slick.DB.withSession { implicit session => ProductTable.getAllProductsWithCustomerPrices(customer_id)} )
    Ok(result).as(JSON)

  }

      /**
       *  Method to get all available products, used for manage catalog
        * @return all products as json, with all fields
       */
  def getAllProducts = authorizedAction(Administrator){user => implicit request =>
     val result = Json.toJson( play.api.db.slick.DB.withSession { implicit session => ProductTable.getAll} )
    Ok(result).as(JSON)
  }

  def listProducts = authorizedAction(NormalUser){ user => implicit request =>
    Ok(views.html.catalogue(user))
  }


  // Return number of products in each category as Json result
  def getNumberOfProductsByCategory = Action {

    def totalProductsByCategory (lst : List[Product],res : Map[String,Int]): Map[String,Int]={

        lst match {
        case Nil => res
        case x::xs => if(res contains(x.category_id.getOrElse(0).toString))
                       totalProductsByCategory(xs,res.updated (x.category_id.get.toString, mat.increment(res.get(x.category_id.get.toString).get) ))
                      else if(x.category_id.getOrElse(0)==0)
                        totalProductsByCategory(xs,res)
                       else
                         totalProductsByCategory(xs,res + (x.category_id.get.toString -> 1) )
         }
    }
    // auxilier function that helps to get total number of products in subcategories
    def countProductsInSubcategories(category : Category, data : Map[String, Int]) = {

      var count : Int = 0

      def update() ={
        if(data.contains(category.id.toString))
          data.updated(category.id.toString,count)
        else
          data ++ Map(category.id.toString -> count)
      }

      if(category.subcutegory.isEmpty) update()
      else {
        for(cat <- category.subcutegory) count += data.get(cat.id.toString).getOrElse(0)
         update()
      }



    }

    val products : List[Product] = play.api.db.slick.DB.withSession { implicit session => ProductTable.getAll}
    val categories : List[Category] = play.api.db.slick.DB.withSession { implicit session => CategoryTable.getAll}
    val result : Map[String,Int] = categories.map(f=>countProductsInSubcategories(f,totalProductsByCategory(products,Map()))).reduce(_ ++ _)


    Ok(Json.toJson(result)).as(JSON)

  }
  // serve the view to manage catalog
  def manageCatalog = authorizedAction(Administrator){ user => implicit request =>
  Ok(views.html.manage_catalog(user))
  }

  def getNumberOfProductsByManufacture = Action {
        //get list of all products
        val products : List[Product] = play.api.db.slick.DB.withSession { implicit session => ProductTable.getAll}
        //count occurrences of each manufacture and return a Map(manufacture->occurrences)
        val result = products.foldLeft(Map[String,Int] () withDefaultValue 0){
          (m,x)=>m+(x.manufacture.getOrElse("")->(1+m(x.manufacture.getOrElse(""))))
        }
        Ok(Json.toJson(result)).as(JSON)

  }

}

object Companies extends Controller with LoginLogout with AuthConf with Auth {

  def getCustomersInJson = Action{ implicit request =>
    val customers : List[CompanyJson] = play.api.db.slick.DB.withSession { implicit session =>
    CompanyTable.getAllCustomers.map(c=>CompanyJson(c.id,c.name,c.price_level,c.tel,c.email,c.supplier,c.prospect,c.address,c.contacts))
    }
  val json = Json.toJson(customers)
  Ok(json).as(JSON)
  }

  def getSuppliersInJson = Action{implicit request =>
    val suppliers : List[CompanyJson] = play.api.db.slick.DB.withSession { implicit session =>
       CompanyTable.getAllSuppliers.map(c=>c.toCompanyJson)
    }
    val json = Json.toJson(suppliers)
    Ok(json).as(JSON)
  }

  def getCompanyInJson(id : Int) = Action{ implicit request =>
     val customer = Json.toJson( play.api.db.slick.DB.withSession { implicit session =>
     CompanyTable.getById(id).toCompanyJson
     })
    Ok(customer).as(JSON)
  }

  def listCustomers =  authorizedAction(Administrator){user => implicit request =>
       Ok(views.html.listcustomers(user))
  }

  def customerFiche(id : Int) = authorizedAction(Administrator){user => implicit request =>
       Ok(views.html.customer_fiche(user,id))
  }

  def getCustomerDiscountsInJson(customer_id : Int) = Action {
    val discounts =  Json.toJson( CustomerDiscount.getDiscountsByCustomerId(customer_id).map(d=>d.toCustomerDiscountJ) )
    Ok(discounts).as(JSON)

  }

  def updateCustomer = Action { request =>
  request.body.asJson.map{json =>
  json.validate[CompanyJson].map{
    case customer =>{
        play.api.db.slick.DB.withSession{implicit session =>
        CompanyTable.update(customer)
        AddressTable.update(customer.address.get)

      }
      val result = play.api.db.slick.DB.withSession{implicit session => CompanyTable.getById(customer.id.getOrElse(0))}
      Ok(Json.toJson(result.toCompanyJson)).as(JSON)
    }
  }.recoverTotal(e => BadRequest("Detected error:"+ JsError.toFlatJson(e)))

   }.getOrElse(BadRequest("Expecting Json data"))
  }

  def createCompanyForm = authorizedAction(Administrator){user => implicit request =>
    Ok(views.html.createcompany(user))
  }

  def createCompany = Action { request =>
    request.body.asJson.map{json =>
      json.validate[CompanyJson].map{
        case customer =>{
          val id = play.api.db.slick.DB.withSession{implicit session =>
            CompanyTable.create(customer)
          }
         // val result = play.api.db.slick.DB.withSession{implicit session => CompanyTable.getById(id.get)}
          Ok(Json.toJson(id))

        }
      }.recoverTotal(e => BadRequest("Detected error:"+ JsError.toFlatJson(e)))

    }.getOrElse(BadRequest("Expecting Json data"))
  }


  def createCustomerDiscount = Action { request =>
    implicit val dataReads =(
      (__ \ 'customer_id).read[Int] and
        (__ \ 'supplier_id).read[Int] and
        (__ \ 'discount).read[Double]
      ).tupled
    request.body.asJson.map{json =>
      json.validate[(Int,Int,Double)].map{
        case (customer_id, supplier_id, discount) => {
          CustomerDiscount.create(CustomerDiscount(customer_id,supplier_id, discount))
          Ok("")
        }
      }.recoverTotal(e=>BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }.getOrElse(BadRequest("Expecting Json Data"))
  }




  def updateCustomerDiscount = Action { request =>
    implicit val dataReads =(
      (__ \ 'id).read[Int] and
        (__ \ 'discount).read[Double]
      ).tupled

    implicit val dataWrites = (
      (__ \ 'id).write[Int] and
        (__ \ 'discount).write[Double]
      ).tupled

    request.body.asJson.map{json =>
      json.validate[(Int,Double)].map{
        case (id, discount) => {
          CustomerDiscount.updateDiscount(id,discount)
          Ok(Json.toJson((id,discount))).as(JSON)
        }
      }.recoverTotal(e=>BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }.getOrElse(BadRequest("Expecting Json Data"))

  }

}

trait JsonFormaters {
  // we need this formater to generate appropriate JSON for Products with customer prices
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
}

// TODO Add possibility to set customer discount for every supplier