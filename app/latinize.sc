
import java.text.Normalizer
import java.text.Normalizer.Form
import lib.Normalize._
import scala.collection.immutable.StringOps
//removeDiacritics("garçon")
//"garçon".replaceAll("\\p{InCombiningDiacriticalMarks}+", "")

val str = Normalizer.normalize("téléphone",Normalizer.Form.NFD)
val exp = "\\p{InCombiningDiacriticalMarks}+".r
exp.replaceAllIn(str,"")


