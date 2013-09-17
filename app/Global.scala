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
        Users("admin@test.com", "12345", 1,Some(9)),
        Users("user@test.com", "12345", 0,Some(9))

      ) foreach Users.create
    }
     //default supplier
//    if (Supplier.getAll.isEmpty){
//      Seq(
//      Supplier("Default supplier","Some address","some tel","mail@mail.com"),
//      Supplier("Szubryt","Some address","some tel","mail@mail.com"),
//      Supplier("Kora","Some address","some tel","mail@mail.com"),
//      Supplier("J.Michalska","Some address","some tel","mail@mail.com")
//      ) foreach(f=>f.create_supplier)
//    }
      //Default customer discount for test user
      if(CustomerDiscount.getAll.isEmpty){
        /* discount for customer with id=1, for products from supplier with id=10, discount is 25% from base price*/
      CustomerDiscount(1,10,25.00).create_discount
      }

    //Defaults Product
    play.api.db.slick.DB.withSession { implicit session =>
      if (ProductTable.count == 0) {
          Seq(
            Product(None, "some ref", "some label",Some("some desc."),Some("/assets/images/products/szubryt/baleron_gotowany_2_1_1.jpg"),"kg",
              None,10,Some("Some manufacture"),Some("Suppl. Ref."), false, 5.5, 7.99, 14.99)
          ).foreach(ProductTable.add)
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
         CategoryT(None, "Autres...",11),
         CategoryT(None, "Produits traditionels",15),
         CategoryT(None, "Poisson fumé",15),
         CategoryT(None, "Pierogi",17),
         CategoryT(None, "Crêpes, Galettes",17),
         CategoryT(None, "Autres",17)
        ).foreach(CategoryTable.add)
      }
    }
    //default companies
    play.api.db.slick.DB.withSession { implicit session =>
    if(CompanyTable.count == 0){
      Seq(
        /* Customers*/
        CompanyJson(None,Some("Saveurs de Pologne"),None,Some("+33618013355"),Some("contact@saveursdepologne.com"),false,false,None,Nil),
        CompanyJson(None,Some("Remy Schimowski"),None,Some("+33683683704"),Some("remy.schimowski@numericable.fr"),false,false,None,Nil),
        CompanyJson(None,Some("Saj"),None,Some("+33630078314"),Some("contact@edouardsaj.fr"),false,false,None,Nil),
        CompanyJson(None,Some("SARL Rauwel"),None,Some("+33668691169"),None,false,false,None,Nil),
        CompanyJson(None,Some("SARL SPM Manorek"),None,Some("+33143558644"),Some("imanorek@free.fr"),false,false,None,Nil),
        CompanyJson(None,Some("Adriana et Margo"),None,Some("+33662755006"),Some("adriana.margot@free.fr"),false,false,None,Nil),
        CompanyJson(None,Some("Association Jacky"),None,Some("+33142386320"),Some("jackyacc@o2.pl"),false,false,None,Nil),
        CompanyJson(None,Some("Sklep Petrus"),None,None,None,false,false,None,Nil),
        CompanyJson(None,Some("Alex"),None,None,Some("imexbox@gmail.com"),false,false,None,Nil),
        /*Suppliers*/
        CompanyJson(None,Some("Default Supplier"),None,None,Some("imexbox@gmail.com"),true,false,None,Nil),
        CompanyJson(None,Some("Szubryt"),None,None,Some("biuro@szubryt.pl"),true,false,None,Nil),
        CompanyJson(None,Some("KORA"),None,None,Some("kora_ryby@wp.pl"),true,false,None,Nil),
        CompanyJson(None,Some("BAC-POL"),None,None,Some("sabina.dorula@bacpol.com.pl"),true,false,None,Nil),
        CompanyJson(None,Some("Tradis"),None,None,None,true,false,None,Nil),
        CompanyJson(None,Some("Jot-l"),None,None,Some("h10.bok@jot-l.pl"),true,false,None,Nil),
        CompanyJson(None,Some("Virtu"),None,None,Some("michal.bilnik@virtu.com.pl"),true,false,None,Nil),
        CompanyJson(None,Some("Bruno-Tassi"),None,None,Some("K.Pajtak@brunotassi.com.pl"),true,false,None,Nil),
        CompanyJson(None,Some("Sloneczne-Pole"),None,None,Some("biuro@slonecznepole.com.pl"),true,false,None,Nil),
        CompanyJson(None,Some("J. Michalska"),None,None,Some("zwgjm@wp.pl"),true,false,None,Nil),
        CompanyJson(None,Some("Hurtownia Monika"),None,None,Some("fhmonikakukulka1@o2.pl"),true,false,None,Nil)
      )foreach(CompanyTable.create)

     }

    }

  }

  def getSession(adapter:DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)


}

