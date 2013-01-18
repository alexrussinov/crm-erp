package controllers

import models._
import play.api._
import data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import db.DB
import play.api.mvc._
import play.api.mvc.Results._
import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.Play.current
import jp.t2v.lab.play20.auth._
import play.api.Play._


object Application extends Controller with LoginLogout with AuthConf with Auth{
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def getProducts = Action {
    val json = transaction(DollConn.doll_session(current)) {
      val products = from(AppDB.productTable)(productTable =>
        select(productTable)
      )
      Json.generate(products)
    }
    Ok(json).as(JSON)
  }

  def searchProducts(value : Option[String]) = Action {
    val json = transaction(DollConn.doll_session(current)){
      val products = from(AppDB.productTable) ( s => where ( (s.ref like value.map(_+ "%").?) or
        (s.label like value.map("%"+ _ + "%").?) ) select(s))
      Json.generate(products)
    }

    Ok(json).as(JSON)
  }

  def showProducts = authorizedAction(NormalUser){ user => implicit request =>
    Ok(views.html.products())
  }

  /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(Users.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  /** Alter the login page action to suit your application. */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
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
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => gotoLoginSucceeded(user.get.id)
    )
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
  val idManifest: ClassManifest[Id] = classManifest[Id]

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
  def logoutSucceeded(request: RequestHeader): Result = Redirect(routes.Application.login)

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
  //override lazy val cookieSecureOption: Boolean = play.api.Play.current.configuration.getBoolean("auth.cookie.secure").getOrElse("true")

}

object AccountCreation extends Controller with LoginLogout with AuthConf with Auth {

  val createaccountform = Form (
  mapping(
  "email" -> email.verifying("User with such email existe", e => !Users.findByEmail(e).isDefined),
  "password" -> nonEmptyText(minLength = 6),
  "admin" -> number
  )(Users.apply)(Users.unapply)

  )

  def createAccountForm = authorizedAction(Administrator){ user => implicit request =>
    Ok(views.html.createaccount(createaccountform, "Create user"))
  }

  def createAccount = Action { implicit request =>
     createaccountform.bindFromRequest.fold(
       formWithErrors => BadRequest(views.html.createaccount(formWithErrors, "Errors!!!")),
       user => {
         Users.createUser(user.email, user.pass, user.admin)
         Redirect(routes.Application.showProducts)
       }
     )
  }
}

