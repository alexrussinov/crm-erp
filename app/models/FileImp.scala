package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 26/07/13
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
case class FileImp (name : String, path : String)

object FileImp {
  implicit val fileFormat :Writes[FileImp] = (
    ( __ \ 'name).write[String]and
      (__ \ 'path).write[String]
    )(unlift(FileImp.unapply))
}
