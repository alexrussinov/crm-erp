 package models

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.dsl._
import org.scala_tools.time.Imports._
 import com.codahale.jerkson.Json
 import scala.math.BigDecimal
 import lib.mat

 /**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 05/01/13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
case class Order(fk_soc : Int, order_date: String, date_creation: Timestamp,
                 fk_uther_author: Int, fk_statut : Int, tva : Option[Double], total_ht: Option[Double],
                 total_ttc: Option[Double], note: Option[String]) extends KeyedEntity[Int] {
  val id : Int = 0

  val ref : String = ""

  val date_modif = Some(new Timestamp(DateTime.now.getMillis))

//   lazy val lines : OneToMany[OrderLine] = OrderDB.OrderToOrderLines.left(this)

   /*def createOrder = inTransaction{
    val od = Order.orderTable insert (this)
      update (Order.orderTable) (s=> where(s.id === od.id ) set(s.ref := Order.generateRef(od.id)))
   }*/

}

case class OrderLine (fk_order_id : Int, product_id : Int, product_ref: String, label: String, tva: Double, qty : Double, unity :String, prix_ht : Double,
                 prix_ttc : Double) extends KeyedEntity[Int] {
   val id : Int = 0

  def insertLine : Int = inTransaction {
    OrderDB.orderlinesTable insert (this)
    val order = Order.getById(this.fk_order_id)
    update(OrderDB.orderTable)(s => where(s.id === this.fk_order_id) set(s.total_ht := Some(mat.round( order.total_ht.getOrElse(0.00)+(this.prix_ht*this.qty) ) ),
      s.total_ttc:= Some(mat.round( (order.total_ttc.getOrElse(0.00)+(this.prix_ht*this.qty)*(1+this.tva/100)) )) ) )
  }



 // lazy val order : ManyToOne[Order] = OrderDB.OrderToOrderLines.right(this)

}

object Order extends Schema {

  //val orderTable : Table[Order] = table[Order]("t_order")

 // val OrderToOrderLines = oneToManyRelation(orderTable, OrderLine.orderlinesTable).via((o,l)=> o.id === l.fk_order_id)



  def generateRef(id : Int)  = {
    def getMonth : String = {
      if (DateTime.now.monthOfYear().get < 10) "0"+ DateTime.now.monthOfYear().getAsString
      else DateTime.now.monthOfYear().getAsString
    }
    "CO"+ getMonth +  DateTime.now.year().getAsShortText + "-" + id
  }

  def createOrder(fk_soc: Int, order_date: String, date_creation: Timestamp, user_id: Int, fk_statut: Int, tva: Option[Double], total_ht : Option[Double],
                  total_ttc : Option[Double], note: Option[String]): Int = inTransaction{
    val o = OrderDB.orderTable insert (Order(fk_soc, order_date, date_creation, user_id, fk_statut, tva, total_ht,
                                   total_ttc, note))
    update (OrderDB.orderTable) (s=> where(s.id === o.id ) set(s.ref := Order.generateRef(o.id)))

    o.id
  }

  def getById (id : Int) : Order = {
    inTransaction{
      val order = from(OrderDB.orderTable)(s=> where(s.id === id) select(s))
      order.head
    }

  }

  def getAllJson = {
    val json = inTransaction{
    val orders = from(OrderDB.orderTable)(orders => select(orders))
      Json.generate(orders)
    }

     json
  }


}

object OrderLine extends Schema {
  //val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")


    def getLinesJson (order_id: Int) : String= {
    val json = inTransaction{
    /* val orderlines = from(orderlinesTable)(ln =>
       where (order_id in
         from(Order.orderTable)(o => where(o.id === ln.fk_order_id) select(o.id)))
         select(ln)
         )*/

      val orderlines = from(OrderDB.orderlinesTable)(l => where(l.fk_order_id === order_id) select(l))

      Json.generate(orderlines)
    }
    json
  }


  def getLineById (id : Int) : OrderLine = {
     inTransaction{
              val line = from(OrderDB.orderlinesTable)(l=> where (l.id === id) select (l))
       line.head
     }
  }

  def updateLine (id : Int, qty : Double) = inTransaction {
    update(OrderDB.orderlinesTable)(s => where (s.id === id) set(s.qty :=  qty))
  }
    //
  def deleteLine (id : Int) : Int = inTransaction{
   OrderDB.orderlinesTable.deleteWhere(s => s.id === id)
  }

}

 object OrderDB extends Schema {
   val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")
   val orderTable : Table[Order] = table[Order]("t_order")
   //val OrderToOrderLines = oneToManyRelation(orderTable, orderlinesTable).via((o,l)=> o.id === l.fk_order_id)

 }