package models

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/05/13
 * Time: 11:33
 * Class represent Product entity
 */
abstract class Product {
  val id : Int
  val reference : String
  val label : String
  val description : String
  val image_url : String
  val unity : String
  val category_id : Int
  val supplier_id : Int
  val manufacture : String
  val reference_supplier : String
  val id_supplier : String

}
