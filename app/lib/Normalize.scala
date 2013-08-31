package lib

import java.text.Normalizer

object Normalize {


  def removeDiacritics(str : String) : String = {

    val str_nlz = Normalizer.normalize(str,Normalizer.Form.NFD)
    val exp = "\\p{InCombiningDiacriticalMarks}+".r
    val exp2 = "ł".r
    exp2.replaceAllIn(exp.replaceAllIn(str_nlz,""),"l")
  }

}
