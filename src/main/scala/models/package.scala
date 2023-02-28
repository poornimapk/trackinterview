import akka.http.scaladsl.model.DateTime
import sangria.validation.Violation
package object models {
  case class User(id: Int, email: String, firstName: String, lastName: String, password: String, createdAt: DateTime)

  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  case class Company(id: Int, name: String, createdAt: DateTime)

  case class Recruiter(id: Int, email: String, firstName: String, lastName: String, companyId: Int, createdAt: DateTime)
}
