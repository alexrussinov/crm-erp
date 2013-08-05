package models

import scala.io._
import lib.Normalize._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 21/07/13
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
object Imports{

  def importFromCsvWithHeaders(pathToFile : String, delim : String) : Seq[Map[String,String]]={

       val source = Source.fromFile(pathToFile)(Codec("ISO8859-1")).getLines
       lazy val lines= source.toSeq
       val headers = lines.head.split(delim)
       lines.tail.map(l => headers zip removeDiacritics(l).split(delim)).map(s=>s.toMap)

  }



}
