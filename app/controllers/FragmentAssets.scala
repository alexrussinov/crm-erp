package controllers

import play.api.mvc._

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 12/06/13
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
object FragmentAssets extends Controller {
  val FRAGMENTS_PATH_PREFIX = List("public", "fragments")


//  def at(file: String): Action[AnyContent] = {
//    Assets.at("/" + FRAGMENTS_PATH_PREFIX.mkString("/"), file)
//  }
}
