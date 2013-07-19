package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Table, Schema}

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 15/07/13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
case class CustomerDiscount(customer_id : Int, supplier_id : Int, discount : Double) {
  val id = 0

  def create_discount : Int = inTransaction{
    val d = CustomerDiscountDB.customerDiscountTable insert this
    d.id
  }
}

object CustomerDiscount {


  def getCustomerDiscountById (id : Int) : CustomerDiscount = inTransaction {
    val discount = from(CustomerDiscountDB.customerDiscountTable)(s=> where(s.id === id) select(s))
    discount.head
  }
}


object CustomerDiscountDB extends Schema {
  val customerDiscountTable : Table[CustomerDiscount] = table[CustomerDiscount]("t_customer_discount")
}