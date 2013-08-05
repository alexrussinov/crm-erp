package lib

import java.text.Normalizer

object Normalize {


  def removeDiacritics(str : String) : String = {

    val str_nlz = Normalizer.normalize(str,Normalizer.Form.NFD)
    val exp = "\\p{InCombiningDiacriticalMarks}+".r
    exp.replaceAllIn(str_nlz,"")
  }

}
