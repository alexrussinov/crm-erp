package models

import org.squeryl.{Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import play.api.Play._




/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/12/12
 * Time: 02:27
 * To change this template use File | Settings | File Templates.
 */
case class Product(@Column("rowid") id : Int, ref : Option[String], label : Option[String], price : Option[Double])  {


  // TODO: Add fields for product entity
}

object Product extends Schema {
  val productTable: Table[Product] = table[Product]("llx_product")

  def getById(id : Int) : Product = {
    transaction(DollConn.doll_session(current)){
      val product = from(productTable)(s => where (s.id === id) select(s))
      product.head
    }
  }

}