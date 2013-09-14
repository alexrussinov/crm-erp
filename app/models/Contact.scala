package models
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/09/13
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
case class Contact(
id : Option[Int] = None,
first_name : Option[String],
name : Option[String],
position : Option[String],
tel : Option[String],
mobile : Option[String],
email : Option[String],
fk_company : Option[Int]
) {}

object Contact {
 // implicit val contactFormat = Format(contactReads, contactWrites)
  implicit val contactWrites : Writes[Contact] = (
      (__ \ 'id).write[Option[Int]] and
      (__ \ 'first_name).write[Option[String]] and
      (__ \ 'name).write[Option[String]] and
      (__ \ 'position).write[Option[String]] and
      (__ \ 'tel).write[Option[String]] and
      (__ \ 'mobile).write[Option[String]] and
      (__ \ 'email).write[Option[String]] and
      (__ \ 'fk_company).write[Option[Int]]
    )(unlift(Contact.unapply))

  implicit val contactReads : Reads[Contact] = (
      (__ \ 'id).readNullable[Int] and
      (__ \ 'first_name).readNullable[String] and
      (__ \ 'name).readNullable[String] and
      (__ \ 'position).readNullable[String] and
      (__ \ 'tel).readNullable[String] and
      (__ \ 'mobile).readNullable[String] and
      (__ \ 'email).readNullable[String] and
      (__ \ 'fk_company).readNullable[Int]
    )(Contact.apply _)
}

object ContactTable extends Table[Contact]("t_contact"){
     def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
     def first_name = column[Option[String]]("first_name")
     def name = column[Option[String]]("name")
     def position = column[Option[String]]("position")
     def tel = column[Option[String]]("tel")
     def mobile = column[Option[String]]("mobile")
     def email = column[Option[String]]("email")
     def fk_company = column[Option[Int]]("fk_company")
     def company = foreignKey("FK_COMPANY_CONTACT",fk_company,CompanyTable)(_.id, onDelete = ForeignKeyAction.Cascade, onUpdate = ForeignKeyAction.Cascade)
     def * = id ~ first_name ~ name ~ position ~ tel ~ mobile ~ email ~ fk_company <> (Contact.apply _, Contact.unapply _)
     def autoInc = first_name ~ name ~ position ~ tel ~ mobile ~ email ~ fk_company returning id

  /**
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(ContactTable.length).first

  def add(c : Contact)(implicit s : Session) = {
    ContactTable.autoInc.insert(c.first_name, c.name,c.position, c.tel, c.mobile, c.email, c.fk_company)
  }
}
