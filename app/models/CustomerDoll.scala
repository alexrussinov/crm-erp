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

case class CustomerDoll(@Column("rowid")id : Int, nom : Option[String],  price_level : Option[Int], address : Option[String], cp : Option[String],
                  ville : Option[String], tel: Option[String], email : Option[String] ) {

}

object CustomerDoll extends Schema {
  val customerTable : Table[CustomerDoll] = table[CustomerDoll]("llx_societe")
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
  implicit val customerFormat = Json.format[CustomerDoll]

  def getAllJson = {
    val json = transaction(DollConn.doll_session(current)){
      val customers: List[CustomerDoll] = from(customerTable)(s => select(s)).toList
      Json.toJson(customers)
    }
    json
  }

  def getById(id : Int): CustomerDoll = {
    transaction(DollConn.doll_session(current)){
      val customer = from(customerTable)(s => where(s.id === id) select(s))
      customer.head
    }

  }

  def getByIdInJson(id : Int) : JsValue = {
   val json = transaction(DollConn.doll_session(current)){
     from(customerTable)(c => where(c.id === id) select(c))
    }
    Json.toJson(json)
  }
}