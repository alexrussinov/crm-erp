import models._
import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter, MySQLAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.GlobalSettings

import play.api.Application
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.Play.current
import scala.Some

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
     //default user
    if (Users.findAll.isEmpty) {
      Seq(
        Users("test@test.com", "12345", 1,Some(1))

      ) foreach Users.create
    }
     //default supplier
    if (Supplier.getAll.isEmpty){
      Seq(
      Supplier("Default supplier","Some address","some tel","mail@mail.com")
      ) foreach(f=>f.create_supplier)
    }
      //Default customer discount for test user
      if(CustomerDiscount.getAll.isEmpty){
      CustomerDiscount(1,1,25.00).create_discount
      }

    //Defaults Product
    play.api.db.slick.DB.withSession { implicit session =>
      if (ProductTable.count == 0) {
          Seq(
            Product(None, "some ref", "some label",Some("some desc."),Some("image url"),"kg",
              None,1,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 14.99)
          ).foreach(ProductTable.insert)
      }
    }
    //Defaults Category
    play.api.db.slick.DB.withSession { implicit session =>
      if (CategoryTable.count == 0) {
        Seq(
         CategoryT(None,"Charcuterie"),
         CategoryT(None,"Produits du Vieux Village",1),
         CategoryT(None,"Produits traditionels",1),
         CategoryT(None,"Jambon, Viande fumée",1),
         CategoryT(None,"Produits crus - fumé",1),
         CategoryT(None,"Produits à Griller - fumé",1),
         CategoryT(None,"Saucisses",1),
         CategoryT(None,"Pates, Terrines, Saucissons",1),
         CategoryT(None,"Produits mis en pot",1),
         CategoryT(None,"Autres",1),
         CategoryT(None,"Produits Latiers"),
         CategoryT(None,"Yaourts 1",5),
         CategoryT(None,"Fromages 1",5),
         CategoryT(None,"Lait, Créme fraiche 1",5),
         CategoryT(None,"Poissonerie"),
         CategoryT(None,"Boissons"),
         CategoryT(None,"Plats préparés"),
         CategoryT(None,"Conficerie"),
         CategoryT(None,"Lesgumes"),
         CategoryT(None, "Epicerie"),
         CategoryT(None, "Consérves",20),
         CategoryT(None, "Sauces, Moutardes, Mayonnaises",20),
         CategoryT(None, "Pâtes, Céréales, Farines",20),
         CategoryT(None, "Thé, Café",20),
         CategoryT(None, "Confiture, Miel",20),
         CategoryT(None, "Soupes séches, liquide, concentrâtes...",20),
         CategoryT(None, "Epices et Aromates",20),
         CategoryT(None, "Autres",20),
         CategoryT(None, "Yaourts",11),
         CategoryT(None, "Fromages",11),
         CategoryT(None, "Lait, Créme frâiche",11),
         CategoryT(None, "Beure, Margarine",11),
         CategoryT(None, "Autres...",11)
        ).foreach(CategoryTable.insert)
      }
    }

  }

  def getSession(adapter:DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)


}

