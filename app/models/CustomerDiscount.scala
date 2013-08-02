package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl._


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
}




object CustomerDiscountDB extends Schema {
  val customerDiscountTable : Table[CustomerDiscount] = table[CustomerDiscount]("t_customer_discount")
}