import models.Users
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.GlobalSettings

import play.api.Application

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 23/12/12
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter, app))
      case Some("com.mysql.jdbc.Driver") => Some(() => getSession(new MySQLAdapter, app ))
      case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver or com.mysql.jdbc.Driver")
    }

    if (Users.findAll.isEmpty) {
      Seq(
        Users("test@test.com", "12345", 1,Some(1))

      ) foreach Users.create
    }
  }

  def getSession(adapter:DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)

}

