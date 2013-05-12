package models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations._
import org.squeryl.Schema
import play.api.Play._
import com.codahale.jerkson.Json

/**
 *  Class that represent the Customer entity
 */

case class Customer(@Column("rowid")id : Int, nom : Option[String],  price_level : Option[Int], address : Option[String], cp : Option[String],
                  ville : Option[String], tel: Option[String], email : Option[String] ) {

}

object Customer extends Schema {
  val customerTable : Table[Customer] = table[Customer]("llx_societe")

  def getAllJson = {
    val json = transaction(DollConn.doll_session(current)){
      val customers = from(customerTable)(s => select(s))
      Json.generate(customers)
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