package db
import DBSchema._
import models._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class DAO(db: Database) {
  def allUsers = db.run(Users.result)

 /* def getUser(id: Int): Future[Option[User]] = db.run(Users.filter(_.id === id).result.headOption)
*/
  def getUsers(ids: Seq[Int]) = db.run(
    Users.filter(_.id inSet ids).result
  )
}
