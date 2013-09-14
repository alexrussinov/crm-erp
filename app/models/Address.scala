package models
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.ForeignKeyAction
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/09/13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
case class Address(
 id : Option[Int] = None,
 address : Option[String],
 city : Option[String],
 zip : Option[String],
 country : Option[String],
 fk_company : Option[Int]
 ){}

object Address {

  //  implicit val addressFormat = Format(addressReads,addressWrites)
   implicit val addressWrites : Writes[Address] = (
      ( __ \ 'id).write[Option[Int]] and
      ( __ \ 'address).write[Option[String]] and
      ( __ \ 'city).write[Option[String]] and
      ( __ \ 'zip).write[Option[String]] and
      ( __ \ 'country).write[Option[String]] and
      ( __ \ 'fk_company).write[Option[Int]]
    )(unlift(Address.unapply))

    implicit val addressReads : Reads[Address] = (
      ( __ \ "id").readNullable[Int] and
      ( __ \ "address").readNullable[String] and
      ( __ \ "city").readNullable[String] and
      ( __ \ "zip").readNullable[String] and
      ( __ \ "country").readNullable[String] and
      ( __ \ "fk_company").readNullable[Int]
    )(Address.apply _)
}

object AddressTable extends Table[Address]("t_address"){
  def id = column[Option[Int]]("id",O.PrimaryKey, O.AutoInc)
  def address = column[Option[String]]("address")
  def city = column[Option[String]]("city")
  def zip = column[Option[String]]("zip")
  def country = column[Option[String]]("country")
  def fk_company = column[Option[Int]]("fk_company")
  def company = foreignKey("FK_COMPANY_ADDRESS",fk_company,CompanyTable)(_.id,onDelete = ForeignKeyAction.Cascade,onUpdate = ForeignKeyAction.Cascade)

  def * = id ~ address ~ city ~ zip ~ country ~ fk_company <> (Address.apply _, Address.unapply _)
  def autoInc = address ~ city ~ zip ~ country ~ fk_company returning id

  /**
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(AddressTable.length).first

  def update(address : Address)(implicit s : Session)={
    AddressTable.filter(_.id===address.id).update(address)
  }

  def getCompanyAddress(company_id: Int)(implicit s : Session)= {
    (for( c <- AddressTable if(c.fk_company === company_id))yield c).first
  }

  def add(a : Address)(implicit s : Session) = {
    AddressTable.autoInc.insert(a.address,a.city,a.zip,a.country,a.fk_company)
  }
}
