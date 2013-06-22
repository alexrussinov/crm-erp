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
 import play.api.libs.json.{Reads, JsValue}
 import java.io._
 import lib.CustomIO._

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

//  val lines : List[OrderLine] = OrderLine.getLines(this.id)
//
//  val customer : Customer = Customer.getById(this.fk_soc)


   //lazy val lines : OneToMany[OrderLine] = OrderDB.OrderToOrderLines.left(this)

   /*def createOrder = inTransaction{
    val od = Order.orderTable insert (this)
      update (Order.orderTable) (s=> where(s.id === od.id ) set(s.ref := Order.generateRef(od.id)))
   }*/

}

case class OrderLine (fk_order_id : Int, product_id : Int, product_ref: String, label: String, tva: Double, qty : Double, unity :String, prix_ht : Double,
                 prix_ttc : Double) extends KeyedEntity[Int] {
   val id : Int = 0

  def insertLine : Int = inTransaction {
    val line : OrderLine = OrderDB.orderlinesTable insert (this)
    val order = Order.getById(this.fk_order_id)
    val totals = Order.getTotals(order.id)
//    update(OrderDB.orderTable)(s => where(s.id === this.fk_order_id) set(s.total_ht := Some(mat.round( order.total_ht.getOrElse(0.00)+(this.prix_ht*this.qty) ) ),
//      s.total_ttc:= Some(mat.round( (order.total_ttc.getOrElse(0.00)+(this.prix_ht*this.qty)*(1+this.tva/100)) )) ) )
    update(OrderDB.orderTable)(s => where(s.id === this.fk_order_id) set(s.total_ht := Some(mat.round( totals("total_ht") ) ),
      s.total_ttc:= Some(mat.round( totals("total_ttc")) ) ) )
    line.id
  }

  override def toString() = product_id + "," + product_ref + "," + qty + "," + unity

 // lazy val order : ManyToOne[Order] = OrderDB.OrderToOrderLines.right(this)

}

object Order extends Schema {


 // val orderTable : Table[Order] = table[Order]("t_order")

 // val OrderToOrderLines = oneToManyRelation(orderTable, OrderLine.orderlinesTable).via((o,l)=> o.id === l.fk_order_id)

  /* Methode calculate total_ht & total_ttc for the given order*/
  def getTotals(id : Int) : Map[String,Double]={

    val lines: List[OrderLine] = OrderLine.getLines(id)
    if(lines.length >0){
    var total_ht : Double = 0
    var total_ttc : Double = 0
    for(line <- lines){
       total_ht += line.qty*line.prix_ht
       total_ttc += line.qty*line.prix_ttc
    }
    Map("total_ht"->total_ht, "total_ttc"->total_ttc)
    }
    else
      Map("total_ht"->0.0, "total_ttc"->0.0)
  }
  /*Methode to generate reference for the order*/
  def generateRef(id : Int)  = {
    def getMonth : String = {
      if (DateTime.now.monthOfYear().get < 10) "0"+ DateTime.now.monthOfYear().getAsString
      else DateTime.now.monthOfYear().getAsString
    }
    "CO"+ getMonth +  DateTime.now.year().getAsShortText + "-" + id
  }
  /*Create order*/
  def createOrder(fk_soc: Int, order_date: String, date_creation: Timestamp, user_id: Int, fk_statut: Int, tva: Option[Double], total_ht : Option[Double],
                  total_ttc : Option[Double], note: Option[String]): Int = inTransaction{
    val o = OrderDB.orderTable insert (Order(fk_soc, order_date, date_creation, user_id, fk_statut, tva, total_ht,
                                   total_ttc, note))
    update (OrderDB.orderTable) (s=> where(s.id === o.id ) set(s.ref := Order.generateRef(o.id)))

    o.id
  }
  /*Fetch order by id*/
  def getById (id : Int) : Order = {
    inTransaction{
      val order = from(OrderDB.orderTable)(s=> where(s.id === id) select(s))
      order.head
    }

  }
  /*Fetch order in Json String */
  def getByIdInJson(id : Int) : String  = {
    val order : Order = getById(id)
    val lines : List[OrderLine] = OrderLine.getLines(id)
    val customer: Customer = Customer.getById(order.id)
    val order_json_object = Json.generate(Map("ord"->order,"customer"->customer,"lines"->lines))
    //Json.generate(order)
    order_json_object
  }
  /*Fetch all orders in Json String*/
  def getAllJson : String = {
    val json = inTransaction{
    val orders = from(OrderDB.orderTable)(orders => select(orders))
      Json.generate(orders)
    }

     json
  }

  def validate(id : Int):Int={
    inTransaction{
     val r = update(OrderDB.orderTable)(o => where(o.id===id)set(o.fk_statut :=  1))
      r
    }
  }
  def delete(id : Int): Int = {
    inTransaction{
      OrderDB.orderTable.deleteWhere(o=> o.id === id)
    }
  }

  def generateOrderInCSV(order_id : Int): String = {
    val order= getById(order_id)
    val initial_list: List[String] = "id,ref,qty,unity"::Nil
    val order_lines: List[String] = initial_list ::: OrderLine.getLines(order_id) map (s=>s.toString())

    printToFile(new File("doc/"+order.ref+".csv"))(p => {
         order_lines.foreach(p.println)
    })
    "doc/"+order.ref+".csv"
  }
}

object OrderLine extends Schema {
  val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")

    /*Fetch lines in Json String for given order_id*/
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
   /*Fetch lines for given order but return List of OrderLine objects*/
  def getLines(order_id : Int):List[OrderLine] ={ inTransaction {
    if(order_id > 0)
    from(OrderDB.orderlinesTable)(l => where(l.fk_order_id === order_id) select(l)).toList
    else List()
    }
  }

  /*Fetch order line by id*/
  def getLineById (id : Int) : OrderLine = {
     inTransaction{
              val line = from(OrderDB.orderlinesTable)(l=> where (l.id === id) select (l))
       line.head
     }
  }
  /*Update given line with given qty return line id*/
  def updateLine (id : Int, qty : Double) = inTransaction {
    update(OrderDB.orderlinesTable)(s => where (s.id === id) set(s.qty :=  qty))
    val line = getLineById(id)
    val totals = Order.getTotals(line.fk_order_id)
    update(OrderDB.orderTable)(s => where(s.id === line.fk_order_id) set(s.total_ht := Some(mat.round( totals("total_ht") ) ),
      s.total_ttc:= Some(mat.round( totals("total_ttc")) ) ) )
    line.id

  }
  /*Delete given line*/
  def deleteLine (id : Int) : Int = inTransaction{
      val line = getLineById(id)
      val result = OrderDB.orderlinesTable.deleteWhere(ln=>ln.id === id)
      val order = Order.getById(line.fk_order_id)
      val totals = Order.getTotals(line.fk_order_id)
      update(OrderDB.orderTable)(s => where(s.id === line.fk_order_id) set(s.total_ht := Some(mat.round( totals("total_ht") ) ),
        s.total_ttc:= Some(mat.round( totals("total_ttc")) ) ) )
      result

  }
  // convert json value in to OrderLine
//  def readJs(json : JsValue)(implicit  reads : Reads[T]): OrderLine = {
//    val client_id = (json \ "user_id").asOpt[Int]
//    val order_id = (json \ "order_id").asOpt[Int]
//    val product_id = (json \ "product_id").asOpt[Int]
//    val qty = (json \ "qty").asOpt[Double]
//    val client : Customer = Customer.getById(client_id.head)
//    val product_price : ProductPrice = ProductDoll.getProductPrice(product_id.head, client.price_level.head)
//    val product : ProductDoll = ProductDoll.getById(client_id.head)
//    OrderLine(order_id.head,product.id,product.ref.head,product.label.head,product.tva_tx.head, qty.head, product.unite,product_price.price,product_price.price_ttc)
//
//  }

}
 /*DB tables mapping*/
 object OrderDB extends Schema {
   val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")
   val orderTable : Table[Order] = table[Order]("t_order")
   //val OrderToOrderLines = oneToManyRelation(orderTable, orderlinesTable).via((o,l)=> o.id === l.fk_order_id)

 }