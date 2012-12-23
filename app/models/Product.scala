package models

import org.squeryl.{Schema}
import org.squeryl.annotations.Column




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

object AppDB extends Schema {
  val productTable = table[Product]("llx_product")
}