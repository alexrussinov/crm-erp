package models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations._
import org.squeryl.Schema
import play.api.Play._
import play.api.libs.json._
import play.api.libs.json.Json



/**
 *  Class that represent the Customer entity
 */

case class Customer(@Column("rowid")id : Int, nom : Option[String],  price_level : Option[Int], address : Option[String], cp : Option[String],
                  ville : Option[String], tel: Option[String], email : Option[String] ) {

}

object Customer extends Schema {
  val customerTable : Table[Customer] = table[Customer]("llx_societe")
//  implicit val customerWrites = new Writes[Customer] {
//    def writes(c: Customer): JsValue = {
//      Json.obj(
//        "id" -> c.id,
//        "nom" -> c.nom,
//        "price_level" -> c.price_level,
//        "address" -> c.address,
//        "cp"-> c.cp,
//        "ville" -> c.ville,
//        "tel" -> c.tel,
//        "email"-> c.email
//      )
//    }
//  }
  implicit val customerFormat = Json.format[Customer]

  def getAllJson = {
    val json = transaction(DollConn.doll_session(current)){
      val customers: List[Customer] = from(customerTable)(s => select(s)).toList
      Json.toJson(customers)
    }
    json
  }

  def getById(id : Int): Customer = {
    transaction(DollConn.doll_session(current)){
      val customer = from(customerTable)(s => where(s.id === id) select(s))
      customer.head
    }

  }
}