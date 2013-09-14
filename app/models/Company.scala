package models
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/09/13
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
case class Company(
id : Option[Int] = None,
name : Option[String],
price_level : Option[Int], // for multi price system purposes only
tel : Option[String],
email : Option[String],
supplier : Boolean,
prospect : Boolean
){
  def address(implicit session : Session) : Option[Address] = (for{
    a <- AddressTable
    if(a.fk_company === id)
  }yield a).firstOption

  def contacts(implicit session : Session): List[Contact] = (for{
    c <- ContactTable
    if(c.fk_company === id)
  }yield c).list

 def toCompanyJson : CompanyJson = play.api.db.slick.DB.withSession { implicit session => CompanyJson(id,name,price_level,tel,email,supplier,prospect,address,contacts) }

}





case class CompanyJson(id : Option[Int], name : Option[String], price_level : Option[Int], tel : Option[String],
                       email : Option[String], supplier : Boolean, prospect : Boolean, address : Option[Address], contacts : List[Contact])


object CompanyJson {

//  implicit val addressFormat = Address.addressFormat
//  implicit val contactFormat = Contact.contactFormat

  //implicit val companyJsonFormat = Format(companyJsonReads,companyJsonWrites)
//  implicit val addressWrites : Writes[Address] = (
//    ( __ \ 'id).write[Option[Int]] and
//      ( __ \ 'address).write[Option[String]] and
//      ( __ \ 'city).write[Option[String]] and
//      ( __ \ 'zip).write[Option[String]] and
//      ( __ \ 'country).write[Option[String]] and
//      ( __ \ 'fk_company).write[Option[Int]]
//    )(unlift(Address.unapply))

implicit  val companyJsonWrites : Writes[CompanyJson] = (
    ( __ \ 'id).write[Option[Int]] and
    ( __ \ 'name).write[Option[String]] and
    ( __ \ 'price_level).write[Option[Int]] and
    ( __ \ 'tel).write[Option[String]] and
    ( __ \ 'email).write[Option[String]] and
    ( __ \ 'supplier).write[Boolean] and
    ( __ \ 'prospect).write[Boolean] and
    ( __ \ 'address).write[Option[Address]] and
    ( __ \ 'contacts).write[List[Contact]]
   )(unlift(CompanyJson.unapply))

implicit val companyJsonReads : Reads[CompanyJson] = (
      ( __ \ "id").readNullable[Int] and
      ( __ \ "name").readNullable[String] and
      ( __ \ "price_level").readNullable[Int] and
      ( __ \ "tel").readNullable[String] and
      ( __ \ "email").readNullable[String] and
      ( __ \ "supplier").read[Boolean] and
      ( __ \ "prospect").read[Boolean] and
      ( __ \ "address").readNullable[Address] and
      ( __ \ "contacts").lazyRead(list[Contact](Contact.contactReads))
    )(CompanyJson.apply _)
}

object CompanyTable extends Table[Company]("t_company"){

  def id = column[Option[Int]]("id",O.PrimaryKey, O.AutoInc)
  def name = column[Option[String]]("name")
  def price_level = column[Option[Int]]("price_level")
 //  def address = AddressTable.where(_.id === id)
 // def contacts = column[Option[List[Contact]]]("contacts")
 // def contacts = ContactTable.where(_.id === id)
  def tel = column[Option[String]]("tel")
  def email = column[Option[String]]("email")
  def supplier = column[Boolean]("supplier")
  def prospect = column[Boolean]("prospect")
  def * = id ~ name ~ price_level ~ tel ~ email ~ supplier ~ prospect <>(Company.apply _,Company.unapply _)


  def autoInc = name ~ price_level ~ tel ~ email ~ supplier ~ prospect returning id

  /**
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(CompanyTable.length).first


  def getAll(implicit s : Session) : List[Company] = {
    (for(c <- CompanyTable) yield c ).list
  }

  def getAllCustomers(implicit s : Session) : List[Company] = {
    (for(c <- CompanyTable if(!c.supplier)) yield c ).list
  }

  def getAllSuppliers(implicit s : Session) : List[Company] = {
    (for(c <- CompanyTable if(c.supplier)) yield c ).list
  }

  def getById(id : Int)(implicit s: Session) : Company = {
    (for(c<-CompanyTable if(c.id === id)) yield c).first
  }

  def update(company : CompanyJson)(implicit s : Session) : Int = {
//    (for(c<-CompanyTable if(c.id === id)) yield c).mutate(r => (r.row = Company(company.id,company.name,company.price_level,company.tel,company.email,company.supplier,company.prospect)))
 //   getById(company.id.getOrElse(0)).update(Company(company.id,company.name,company.price_level,company.tel,company.email,company.supplier,company.prospect))
    CompanyTable.filter(_.id===company.id).update(Company(company.id,company.name,company.price_level,company.tel,company.email,company.supplier,company.prospect))

  }
   /* insert company in to database from CompanyJson object */
  def create(company : CompanyJson)(implicit s : Session) : Option[Int] = {
    val id : Option[Int] = CompanyTable.autoInc.insert(company.name,company.price_level,company.tel,company.email,company.supplier,company.prospect)

    if(company.address.getOrElse(None) != None){
    val address = company.address.get
    AddressTable.add(Address(None,address.address,address.city,address.zip,address.country,id))
    }
    else
    AddressTable.add(Address(None,None,None,None,None,id))

    id
  }

  /**
   * insert Company object to DataBase
   * return id of inserted object as Option[Int]
   **/

  def add(c : Company)(implicit s : Session): Option[Int] = {
    CompanyTable.autoInc.insert(c.name,c.price_level,c.tel,c.email,c.supplier,c.prospect)
  }

}

