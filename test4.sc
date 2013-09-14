/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/09/13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.Some


val a = CompanyJson(Some(1),Some("Bac-Pol"),None,Some("+33688818161"),None,false,false,Some(Address(Some(1),None,Some("City"),Some("02100"),None,Some(1))),List())






val b = CompanyJson(Some(2),Some("Kora"),None,Some("+33688818161"),None,false,false,None,List())



val c = CompanyJson(Some(3),Some("Virtu"),None,Some("+33688818161"),None,false,false,None,List())




Json.toJson(Address(Some(1),Some("city"),None,None,None,Some(1)))



val json = Json.toJson(List(a,b,c))













Json.fromJson[List[CompanyJson]](json).get == List(a,b,c)

















