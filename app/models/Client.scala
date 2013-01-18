package models
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.annotations._
import org.squeryl.Schema

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 27/12/12
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
case class Client(@Column("rowid")id : Int, nom : Option[String],  price_level : Int, address : Option[String], cp : Int,
                  ville : String, tel: String, email : String ) {

}

