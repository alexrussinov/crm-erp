 package models

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations.Column

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 05/01/13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
case class Order(ref : Option[String], fk_soc : Int, date_creation: Timestamp, date_valid: Option[Timestamp],
                 fk_uther_author: Int, fk_statut : Int, tva : Option[Float], total_ht: Option[Float],
                 total_ttc: Option[Float], note: Option[String]) extends KeyedEntity[Int]{
  val id : Int = 0

}

case class OrderLine (fk_order_id : Int, product_id : Int, qty : Int, unity :String, prix_ht : Float,
                 prix_ttc : Float) extends KeyedEntity[Int] {
   val id : Int = 0
  def getLines (order_id: Int): List[OrderLine]= {
                (from(OrderDB.orderlinesTable)(s => where(s.fk_order_id === order_id) select(s))).toList
  }

  def insertLine = OrderDB.orderlinesTable insert (this)
}

object OrderDB extends Schema {
  val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")
  val orderTable : Table[Order] = table[Order]("t_order")
}
