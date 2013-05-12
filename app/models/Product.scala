package models

import org.squeryl.{Table, Schema}
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._
import play.api.Play._




/**
 * Class for representing Order entity
 * */
case class Product(@Column("rowid") id : Int, ref : Option[String], label : Option[String],
                   price : Option[Double], tva_tx: Option[Double], unite : String)  {


  // TODO: Add fields for product entity
}

case class ProductPrice(@Column("rowid") id : Int, fk_product : Int, price_level : Int, price: Double, price_ttc:Double,
                        tva_tx: Double)

object Product extends Schema {
  val productTable: Table[Product] = table[Product]("llx_product")
  val productpriceTable : Table[ProductPrice] = table[ProductPrice]("llx_product_price")

  def getById(id : Int) : Product = {
    transaction(DollConn.doll_session(current)){
      val product = from(productTable)(s => where (s.id === id) select(s))
      product.head
    }
  }

  def getProductPrice(product_id : Int, price_level : Int): ProductPrice = {
    if(price_level != 0)
    transaction(DollConn.doll_session(current)){
      val productPrice = from(productpriceTable)(s => where(s.fk_product === product_id and s.price_level === price_level)
        select(s))
      productPrice.head
    }
    else ProductPrice(0,0,0,0.00,0.00,0.00)



  }



}