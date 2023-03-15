import akka.http.scaladsl.model.DateTime
import sangria.execution.deferred.HasId
import sangria.validation.Violation
package object models {

  trait Identifiable {
    val id: Int
  }

  object Identifiable {
    implicit def hasId[T <: Identifiable]: HasId[T, Int] = HasId(_.id)
  }
  case class User(id: Int, email: String, firstName: String, lastName: String, password: String, createdAt: DateTime) extends Identifiable

  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  case class Company(id: Int, name: String, createdAt: DateTime) extends Identifiable

  case class Recruiter(id: Int, email: String, firstName: String, lastName: String, companyId: Int, createdAt: DateTime) extends Identifiable

  case class Job(id: Int, userId: Int, companyId: Int, recruiterId: Int, description: String, link: String, status: String, comment: String, createdAt: DateTime) extends Identifiable
}
