package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import play.api.libs.json._
import play.api.libs.json.Writes
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/05/13
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
case class Supplier(name : String, address : String, tel : String, email : String) extends KeyedEntity[Int] {
    val id = 0

  def create_supplier : Int = inTransaction{
       val s = SupplierDB.supplierTable insert this
    s.id
  }
}

object Supplier {

  implicit val supplierFormat : Writes[Supplier]=(
    (__ \ 'name).write[String] and
      (__ \ 'address).write[String] and
         (__ \ 'tel).write[String] and
            (__ \ 'email).write[String]
    )(unlift(Supplier.unapply))

  def getById(id : Int) : Supplier = inTransaction {
    val supplier = from(SupplierDB.supplierTable)(s=>where(s.id === id)select(s))
    supplier.head
  }

  def getAll : List[Supplier] = inTransaction{
    val res = from(SupplierDB.supplierTable) (s=>select(s))
    res.toList
  }

  def getAllInJson : JsValue = inTransaction {
    val suppliers = from(SupplierDB.supplierTable) (s=>select(s))
    Json.toJson(suppliers)
  }
}

object SupplierDB extends Schema {
  val supplierTable : Table[Supplier] = table[Supplier]("t_supplier")
}