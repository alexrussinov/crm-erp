 package models

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.dsl._
import org.scala_tools.time.Imports._
 import scala.math.BigDecimal
 import lib.mat
 import java.io._
 import lib.CustomIO._
 import play.api.libs.json._
 import play.api.libs.json.Json
 import play.api.libs.functional.syntax._
 import scala.collection.mutable.ArrayBuffer
 import play.api.Play.current


 /**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 05/01/13
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
 */
case class Order(fk_soc : Int, order_date: String, date_creation: Timestamp,
                 fk_uther_author: Int, fk_statut : Int, tva : Option[Double], total_ht: Option[Double],
                 total_ttc: Option[Double], note: Option[String], sent : Boolean = false, sent_date : Option[Timestamp]=None) extends KeyedEntity[Int] {
  val id : Int = 0

  val ref : String = ""


  val date_modif : Option[Timestamp] = None



//  val lines : List[OrderLine] = OrderLine.getLines(this.id)
//
//  val customer : Customer = Customer.getById(this.fk_soc)


   //lazy val lines : OneToMany[OrderLine] = OrderDB.OrderToOrderLines.left(this)

   /*def createOrder = inTransaction{
    val od = Order.orderTable insert (this)
      update (Order.orderTable) (s=> where(s.id === od.id ) set(s.ref := Order.generateRef(od.id)))
   }*/

}
// represent order for generating json
case class OrderJ(id: Int, ref: String, date_modif : Option[Timestamp], fk_soc : Int, order_date: String, date_creation: Timestamp,
                   fk_uther_author: Int, fk_statut : Int, tva : Option[Double], total_ht: Option[Double],
                   total_ttc: Option[Double], note: Option[String],sent : Boolean, sent_date : Option[Timestamp])

case class OrderLine (fk_order_id : Int, product_id : Int, product_ref: String, label: String, tva: Double, qty : Double, unity :String, prix_ht : Double,
                 prix_ttc : Double) extends KeyedEntity[Int] {
   val id : Int = 0

//  implicit val orderLineWrites = new Writes[OrderLine] {
//    def writes(ol: OrderLine): JsValue = {
//      Json.obj(
//        "fk_order_id" -> ol.fk_order_id,
//        "product_id" -> ol.product_id,
//        "product_ref" -> ol.product_ref,
//        "label" -> ol.label,
//        "tva"-> ol.tva,
//        "qty" -> ol.qty,
//        "unity"-> ol.unity,
//        "prix_ht"-> ol.prix_ht,
//        "prix_ttc"-> ol.prix_ttc
//      )
//    }
//  }

  def insertLine : Int = inTransaction {
    val line : OrderLine = OrderDB.orderlinesTable insert (this)
    val order = Order.getById(this.fk_order_id)
    // calculate total_ht et total_ttc
    val totals = Order.getTotals(order.id)
//    update(OrderDB.orderTable)(s => where(s.id === this.fk_order_id) set(s.total_ht := Some(mat.round( order.total_ht.getOrElse(0.00)+(this.prix_ht*this.qty) ) ),
//      s.total_ttc:= Some(mat.round( (order.total_ttc.getOrElse(0.00)+(this.prix_ht*this.qty)*(1+this.tva/100)) )) ) )
    update(OrderDB.orderTable)(s => where(s.id === this.fk_order_id) set(s.total_ht := Some(mat.round( totals("total_ht") ) ),
      s.total_ttc:= Some(mat.round( totals("total_ttc")) ) ) )
    line.id
  }

  override def toString() = product_id + "," + product_ref + "," + qty + "," + unity
  def toCsvString(delimiter : String) : String = product_id + delimiter + product_ref + delimiter + label + delimiter + qty + delimiter + unity

 // lazy val order : ManyToOne[Order] = OrderDB.OrderToOrderLines.right(this)

}

// we need this class that represents an order line for generating JSON
case class OrderLineJ(id : Int, fk_order_id : Int, product_id : Int, product_ref: String, label: String, tva: Double, qty : Double, unity :String, prix_ht : Double,
                       prix_ttc : Double)

object Order extends Schema {
  //   implicit val orderWrites = new Writes[Order] {
  //     def writes(o: Order): JsValue = {
  //       Json.obj(
  //         "fk_societe" -> o.fk_soc,
  //         "order_date" -> o.order_date,
  //         "date_creation" -> o.date_creation,
  //         "fk_user_author" -> o.fk_uther_author,
  //         "fk_statut"-> o.fk_statut,
  //         "tva" -> o.tva,
  //         "total_ht"-> o.total_ht,
  //         "total_ttc"-> o.total_ttc,
  //         "note"-> o.note
  //       )
  //     }
  //   }


  implicit val orderFormat : Writes[OrderJ] = (
      ( __ \ 'id).write[Int] and
      ( __ \ 'ref).write[String] and
      ( __ \ 'date_modif).write[Option[Timestamp]] and
      ( __ \ 'fk_soc).write[Int] and
      ( __ \ 'order_date).write[String] and
      ( __ \ 'date_creation).write[Timestamp] and
      ( __ \ 'fk_user_author).write[Int] and
      ( __ \ 'fk_statut).write[Int] and
      ( __ \ 'tva).write[Option[Double]] and
      ( __ \ 'total_ht).write[Option[Double]] and
      ( __ \ 'total_ttc).write[Option[Double]] and
      ( __ \ 'note).write[Option[String]] and
      ( __ \ 'sent).write[Boolean] and
      ( __ \ 'sent_date).write[Option[Timestamp]]
    )(unlift(OrderJ.unapply))
 // val orderTable : Table[Order] = table[Order]("t_order")

 // val OrderToOrderLines = oneToManyRelation(orderTable, OrderLine.orderlinesTable).via((o,l)=> o.id === l.fk_order_id)

  /* Methode calculate total_ht & total_ttc for the given order*/
  def getTotals(id : Int) : Map[String,Double]={

    val lines: List[OrderLine] = OrderLine.getLines(id)
    if(lines.length >0){
    var total_ht : Double = 0
    var total_ttc : Double = 0
    for(line <- lines){
      if(line.unity == "piece(0.5kg)"){
       total_ht += (line.qty/2)*line.prix_ht
       total_ttc += (line.qty/2)*line.prix_ttc
      }
      else{
        total_ht += line.qty*line.prix_ht
        total_ttc += line.qty*line.prix_ttc
      }
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
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

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
  /**
   * Fetch order in Json String with lines and customer information
   * */
  def getByIdInJson(id : Int) : JsValue  = {
    val order : Order = getById(id)
//    val lines : List[OrderLine] = OrderLine.getLines(id)
    val lines : JsValue =OrderLine.getLinesJson(id)

 /*   old implementation to use with Dolibarr database */
//    val customer: CustomerDoll = CustomerDoll.getById(order.fk_soc)

    /* new implementation to use with native DB  */
    val customer = play.api.db.slick.DB.withSession { implicit session =>
      CompanyTable.getById(order.fk_soc).toCompanyJson
    }
    val order_json_object = Json.toJson(Map("ord"->Json.toJson(toOrderJ(order)),"customer"->Json.toJson(customer),"lines"->lines))
    //Json.generate(order)
    order_json_object
  }
  /*Fetch all orders in Json String*/
  def getAllJson : JsValue = {
    val json = inTransaction{
    val orders: List[OrderJ] = from(OrderDB.orderTable)(orders => select(orders)).toList map (o=>toOrderJ(o))
      Json.toJson(orders)
    }

     json
  }
  // fetch orders for current customer
  def getCustomerOrders(customer_id : Int, number:Int = -1) : List[OrderJ] = {
    val list_of_orders = inTransaction{
      val orders: List[OrderJ] = from(OrderDB.orderTable)(orders => where(orders.fk_soc === customer_id) select(orders)).toList map (o=>toOrderJ(o))
      if(number == -1)
      orders
      else orders.take(number)
    }

    list_of_orders
  }
   // TODO May be refactor to more elegant and immutable solution
  /**
   *
   * @param customer_id - Id of the customer
   * @return an ArrayBuffer total ordered per month
   */
  def getCustomerTotalOrdersPerMonth(customer_id : Int) : ArrayBuffer[Double] = {
    val orders = getCustomerOrders(customer_id)
//    val result : Map[String,Double] = Map("janvier"->0,"fevrier"->0,"mars"->0,
//                     "avril"->0,"mai"->0,"juin"->0,"juillet"->0,
//                     "aout"->0, "septembre"->0, "octobre"->0,"novembre"->0,"decembre"->0)
    val result : ArrayBuffer[Double] = ArrayBuffer(0,0,0,0,0,0,0,0,0,0,0,0)

     orders.map{f=>
     f.order_date.split("-")(1)match{
       case "01" => result(0)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "02" => result(1)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "03" => result(2)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "04" => result(3)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "05" => result(4)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "06" => result(5)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "07" => result(6)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "08" => result(7)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "09" => result(8)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "10" => result(9)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "11" => result(10)+=mat.round(f.total_ttc.getOrElse(0.0))
       case "12" => result(11)+=mat.round(f.total_ttc.getOrElse(0.0))
       }
     }
    result
  }

  def validate(id : Int):Int={
    inTransaction{
     val r = update(OrderDB.orderTable)(o => where(o.id===id)set(o.fk_statut :=  1))
      r
    }
  }
  def modify(id : Int):Int={
    inTransaction{
      val r = update(OrderDB.orderTable)(o => where(o.id===id)set(o.fk_statut :=  0,o.date_modif:=Some(new Timestamp(DateTime.now.getMillis)) ))
      r
    }
  }

  def sent(id : Int): Int ={
    inTransaction{
      update(OrderDB.orderTable)(o => where(o.id === id)set(o.sent := true, o.sent_date := Some(new Timestamp(DateTime.now.getMillis))))
    }
  }
  def delete(id : Int): Int = {
    inTransaction{
      OrderDB.orderlinesTable.deleteWhere(l => l.fk_order_id === id)
      OrderDB.orderTable.deleteWhere(o => o.id === id)
    }
  }

  def generateOrderInCSV(order_id : Int): String = {
    val order= getById(order_id)
    val initial_list: List[String] = "id;ref;label;qty;unity"::Nil
    val order_lines: List[String] = initial_list ::: OrderLine.getLines(order_id).map (s=>s.toCsvString(";"))

    printToFile(new File("doc/"+order.ref+".csv"))(p => {
         order_lines.foreach(p.println)
    })
    val path : String = "doc/"+order.ref+".csv"
    path
  }

  // transform Order object to OrderJ object to perform JSON deserialisation with orderFormat,
  //we need this to have id, ref, date_modif fields in generated json
  def toOrderJ(o : Order): OrderJ = {
    OrderJ(o.id,o.ref,o.date_modif, o.fk_soc, o.order_date, o.date_creation, o.fk_uther_author,
      o.fk_statut,o.tva, o.total_ht, o.total_ttc, o.note, o.sent,o.sent_date)
  }

  def totalQty(order_id: Int): Map[String,Double] = {
    var total_kg : Double = 0.0
    var total_piece : Double = 0.0
    val lines = OrderLine.getLines(order_id)
    for (line <- lines){
      if(line.unity == "kg")
        total_kg+=line.qty
      if(line.unity == "piece")
        total_piece+=line.qty
    }
    Map("kg"->total_kg,"piece"->total_piece)
  }
}

object OrderLine extends Schema {
  val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")
  implicit val orderLineFormat = Json.format[OrderLineJ]

    /*Fetch lines in Json String for given order_id*/
    def getLinesJson (order_id: Int) : JsValue = {
    val json = inTransaction{
    /* val orderlines = from(orderlinesTable)(ln =>
       where (order_id in
         from(Order.orderTable)(o => where(o.id === ln.fk_order_id) select(o.id)))
         select(ln)
         )*/

      val orderlines : List[OrderLineJ] = from(OrderDB.orderlinesTable)(l => where(l.fk_order_id === order_id) select(l)).toList map (l=>toLineJ(l))

      Json.toJson(orderlines)
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

  def toLineJ(line : OrderLine): OrderLineJ = {
    OrderLineJ(line.id,line.fk_order_id,line.product_id, line.product_ref,line.label,line.tva,line.qty,line.unity,line.prix_ht,line.prix_ttc)
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

 object OrderJ{
   implicit val orderFormat : Writes[OrderJ] = (
     ( __ \ 'id).write[Int] and
       ( __ \ 'ref).write[String] and
       ( __ \ 'date_modif).write[Option[Timestamp]] and
       ( __ \ 'fk_soc).write[Int] and
       ( __ \ 'order_date).write[String] and
       ( __ \ 'date_creation).write[Timestamp] and
       ( __ \ 'fk_user_author).write[Int] and
       ( __ \ 'fk_statut).write[Int] and
       ( __ \ 'tva).write[Option[Double]] and
       ( __ \ 'total_ht).write[Option[Double]] and
       ( __ \ 'total_ttc).write[Option[Double]] and
       ( __ \ 'note).write[Option[String]] and
       ( __ \ 'sent).write[Boolean] and
       ( __ \ 'sent_date).write[Option[Timestamp]]
     )(unlift(OrderJ.unapply))
 }
 /*DB tables mapping*/
 object OrderDB extends Schema {
   val orderlinesTable : Table[OrderLine] = table[OrderLine]("t_orderlines")
   val orderTable : Table[Order] = table[Order]("t_order")
   //val OrderToOrderLines = oneToManyRelation(orderTable, orderlinesTable).via((o,l)=> o.id === l.fk_order_id)

 }