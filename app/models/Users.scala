package models

import org.squeryl.annotations._
import org.squeryl.{KeyedEntity, Table, Schema}
import org.squeryl.PrimitiveTypeMode._
import play.api.Play._
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 11/01/13
 * Time: 18:47
 * To change this template use File | Settings | File Templates.
 */
case class Users (email : String, pass: String, admin : Int, customer_id: Option[Int] ) extends KeyedEntity[Int]{
   val id : Int = 0


}


object Users extends Schema {
  val usersTable: Table[Users] = table[Users]("t_user")

  def findById(id : Int) = {
    inTransaction{
    val us = (from(usersTable)(s => where(s.id === id) select(s)))
     us.headOption }
  }

  def authenticate(email: String, password: String): Option[Users] = {
    findByEmail(email).filter { account => BCrypt.checkpw( password, account.pass) }
  }

  def checkPswd(user_id : Int, password : String) : Boolean = {
    val user = findById(user_id).get
    BCrypt.checkpw(password, user.pass)
  }

  def findByEmail(email : String) : Option[Users]= {
    inTransaction{
      val us = from(usersTable)(s => where (s.email === email) select(s))

      us.headOption }
  }

  def findAll : List[Users] = {
    inTransaction{
      from(usersTable)(s => select(s)).toList
    }
  }

  def createUser(email : String, password : String, adm : Int, customer_id:Option[Int]): Users= {

    val us = inTransaction(usersTable insert Users(email, BCrypt.hashpw(password, BCrypt.gensalt()), adm, customer_id) )
    us
  }

  def create (user : Users): Option[Users] = {

    val uss = inTransaction(usersTable insert Users(user.email,BCrypt.hashpw(user.pass, BCrypt.gensalt()),user.admin,user.customer_id) )

    Some(uss)
  }

  def updatePassword(user_id : Int, password : String) = {
    inTransaction {
      update(Users.usersTable)(u => where (u.id === user_id) set(u.pass :=  BCrypt.hashpw(password, BCrypt.gensalt())))
    }
  }

  def delete(id : Int) = {
    inTransaction(usersTable.deleteWhere(us=>us.id===id))
  }

}