import models.{Category, CategoryT}
import play.api.libs.json._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 08/08/13
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
val categorys =  Seq(
  CategoryT(Some(1),"Charcuterie"),
  CategoryT(Some(2),"Sub category charc. 1",1),
  CategoryT(Some(3),"Sub category charc. 2",1),
  CategoryT(Some(4),"Sub category charc. 3",1),
  CategoryT(Some(5),"Produits Latiers"),
  CategoryT(Some(6),"Sub category latiers 1",5),
  CategoryT(Some(7),"Poissonerie"),
  CategoryT(Some(8), "Autres produits")
).toList





def getSubCAtegorys(cat : CategoryT, lst : List[CategoryT]) : List[Category] = {
  if (lst == Nil) Nil
  else
  (for (e <- lst if(e.parent == cat.id.get))yield Category(e.id.get,e.name,getSubCAtegorys(e,lst))).toList
}

val result = for (c <- categorys if(c.parent==0)) yield Category(c.id.get,c.name,getSubCAtegorys(c,categorys))




Json.toJson(result)








