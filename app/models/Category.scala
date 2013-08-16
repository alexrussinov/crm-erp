package models
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/05/13
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
case class Category(
id : Int,
name: String,
subcutegory : List[Category]
)

object Category{

  implicit val categoryFormat : Writes[Category] =(
   (__ \'id).write[Int] and
       (__ \'name).write[String]and
          (__ \'subcategory).lazyWrite(Writes.traversableWrites[Category](categoryFormat))
    )(unlift(Category.unapply))
}



case class CategoryT(
id : Option[Int] = None,
name : String,
parent : Int = 0
)

object CategoryTable extends Table[CategoryT]("t_category"){
  def id = column[Option[Int]]("id",O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def parent =column[Int]("parent")
  def * = id ~ name ~ parent <>(CategoryT.apply _,CategoryT.unapply _)

  def autoInc = * returning id

  /**
   * Count all products
   */
  def count(implicit s:Session): Int =
    Query(CategoryTable.length).first

  def getAll (implicit session : Session) : List[Category] = {
   val categorys : List[CategoryT] = (for (c<-CategoryTable)yield c).list
    def getSubCategories(cat : CategoryT, lst : List[CategoryT]) : List[Category] = {
      if (lst == Nil) Nil
      else
        (for (e <- lst if(e.parent == cat.id.get))yield Category(e.id.get,e.name,getSubCategories(e,lst))).toList
    }

    for (c <- categorys if(c.parent==0)) yield Category(c.id.get,c.name,getSubCategories(c,categorys))
  }
}

