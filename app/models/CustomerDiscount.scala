package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 15/07/13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
case class CustomerDiscount(customer_id : Int, supplier_id : Int, discount : Double) extends KeyedEntity[Int] {
  val id = 0

  def create_discount : Int = inTransaction {
    val d = CustomerDiscountDB.customerDiscountTable insert this
    d.id
   }

  def toCustomerDiscountJ = CustomerDiscountJ(id,customer_id,supplier_id,discount)
}

case class CustomerDiscountJ(id : Int, customer_id : Int, supplier_id : Int, discount : Double)

object CustomerDiscountJ{

  implicit val customerDiscountWrites : Writes[CustomerDiscountJ] = (
    ( __ \ 'id).write[Int] and
      (__ \ 'customer_id).write[Int] and
      (__ \ 'supplier_id).write[Int] and
      (__ \ 'discount).write[Double]
    )(unlift(CustomerDiscountJ.unapply))

  implicit val customerDiscountReads : Reads[CustomerDiscountJ] = (
    (__ \ "id").read[Int] and
      (__ \ "customer_id").read[Int] and
      (__ \ "supplier_id").read[Int] and
      (__ \ "discount").read[Double]
    )(CustomerDiscountJ.apply _)
}


object CustomerDiscount {



  def create(c_discount : CustomerDiscount) = inTransaction{
    val c = CustomerDiscountDB.customerDiscountTable insert c_discount
    c.id
  }


  def getAll : List[CustomerDiscount] = inTransaction {
    val discounts = from(CustomerDiscountDB.customerDiscountTable)(s=> select(s))
    discounts.toList
  }

  def getCustomerDiscountsById (id : Int) : CustomerDiscount = inTransaction {
    val discount = from(CustomerDiscountDB.customerDiscountTable)(s=> where(s.id === id) select(s))
    discount.head
  }

  def getDiscountsByCustomerId(customer_id : Int) : List[CustomerDiscount] = inTransaction{
    val discount = from(CustomerDiscountDB.customerDiscountTable)(s=> where(s.customer_id === customer_id) select(s))
    discount.toList
  }

  def updateDiscount(discount_id : Int, discount : Double) = inTransaction{
    update(CustomerDiscountDB.customerDiscountTable)(s=>where(s.id === discount_id)set(s.discount := discount))
  }
}




object CustomerDiscountDB extends Schema {
  val customerDiscountTable : Table[CustomerDiscount] = table[CustomerDiscount]("t_customer_discount")
}