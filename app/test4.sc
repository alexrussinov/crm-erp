import java.io.File
import lib.{CustomIO, Convertion}
import models.ProductTable
import play.api.db.slick.DB
import play.api.libs.json.{Json, JsValue}
import play.api.Play.current

//CustomIO.getFileTree(new File("imports/")) filter (_.isFile) map (f=>Map(f.getName->f.getPath))
val t  = (Some(1),"some ref","some label",Some("some desc."),Some("image url"),"kg",None,1,Some("Some manufacture"),5.5,11.24)


val t2 : Tuple3[Int,String,Double] = (1, "hello", 5.5)

val res = List(t)


val res2 = res.filter(s=>(s._2.toLowerCase.contains("s")||s._3.toLowerCase.contains("s")))


val f = res2 map(s=>(s._1.get,Some(s._2),Some(s._3),s._11,s._10))








