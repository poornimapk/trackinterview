package db
import DBSchema._
import slick.jdbc.H2Profile.api._

class DAO(db: Database) {
  def allUsers = db.run(Users.result)
}
