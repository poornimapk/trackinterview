package db

import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps
import models._
import akka.http.scaladsl.model.DateTime
import java.sql.Timestamp

object DBSchema {
  /**
   * Load schema and populate sample data within this Sequence od DBActions
   */
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.clicks),
    ts => DateTime(ts.getTime)
  )
  class UsersTable(tag: Tag) extends Table[User](tag, "Users") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL")
    def firstName = column[String]("FIRST_NAME")
    def lastName = column[String]("LAST_NAME")
    def password = column[String]("PASSWORD")
    def createdAt = column[DateTime]("CREATED_AT")
    def * = (id, email, firstName, lastName, password, createdAt).mapTo[User]
  }

  val Users = TableQuery[UsersTable]

  class CompaniesTable(tag: Tag) extends Table[Company](tag, "Companies") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def createdAt = column[DateTime]("CREATED_AT")
    def * = (id, name, createdAt).mapTo[Company]
  }

  val Companies = TableQuery[CompaniesTable]

  class RecruitersTable(tag: Tag) extends Table[Recruiter](tag, "Recruiters") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL")
    def firstName = column[String]("FIRST_NAME")
    def lastName = column[String]("LAST_NAME")
    def companyId = column[Int]("COMPANY_ID")
    def createdAt = column[DateTime]("CREATED_AT")
    def * = (id, email, firstName, lastName, companyId, createdAt).mapTo[Recruiter]
  }

  val Recruiters = TableQuery[RecruitersTable]

  val databaseSetup = DBIO.seq(
    Users.schema.create,
    Companies.schema.create,
    Recruiters.schema.create,
    Users forceInsertAll Seq(
      User(1, "sample1@gmail.com", "Sample1FN", "Sample1LN", "pass123", DateTime(2023, 2, 27)),
      User(2, "sample2@gmail.com", "Sample2FN", "Sample2LN", "pass234", DateTime(2023, 2, 28)),
      User(3, "sample3@gmail.com", "Sample3FN", "Sample3LN", "pass345", DateTime(2023, 2, 26))
    ),
    Companies forceInsertAll Seq(
      Company(1, "SampleCompany1", DateTime(2023, 2, 27)),
      Company(2, "SampleCompany2", DateTime(2023, 2, 28)),
      Company(3, "SampleCompany3", DateTime(2023, 2, 26)),
    ),
    Recruiters forceInsertAll Seq(
      Recruiter(1, "recruiter1@email.com", "Rec1FirstName", "Rec1LastName", 1, DateTime(2023, 2, 27)),
      Recruiter(2, "recruiter2@email.com", "Rec2FirstName", "Rec2LastName", 1, DateTime(2023, 2, 27)),
      Recruiter(3, "recruiter3@email.com", "Rec3FirstName", "Rec3LastName", 1, DateTime(2023, 2, 27)),
      Recruiter(4, "recruiter4@email.com", "Rec4FirstName", "Rec4LastName", 2, DateTime(2023, 2, 28))
    )
  )
  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)
    new DAO(db)

  }
}
