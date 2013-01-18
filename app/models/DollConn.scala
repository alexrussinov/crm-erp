package models

import org.squeryl.adapters.MySQLAdapter
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.Session
import play.api.Application
import play.api.db.DB


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 27/12/12
 * Time: 22:20
 * To change this template use File | Settings | File Templates.
 */
object DollConn {

  def doll_session(app : Application) = Session.create( DB.getConnection("dolibarr")(app), new MySQLAdapter)

}


