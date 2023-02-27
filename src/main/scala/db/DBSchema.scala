package db

import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import models._

object DBSchema {
  /**
   * Load schema and populate sample data within this Sequence od DBActions
   */
  class UsersTable(tag: Tag) extends Table[User](tag, "Users") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL")
    def firstName = column[String]("FIRSTNAME")
    def lastName = column[String]("LASTNAME")

    def * = (id, email, firstName, lastName).mapTo[User]
  }

  val Users = TableQuery[UsersTable]

  val databaseSetup = DBIO.seq(
    Users.schema.create,
    Users forceInsertAll Seq(
      User(1, "sample1@gmail.com", "Sample1FN", "Sample1LN"),
      User(2, "sample2@gmail.com", "Sample2FN", "Sample2LN"),
      User(3, "sample3@gmail.com", "Sample3FN", "Sample3LN"),
    )
  )


  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new DAO(db)

  }
}
