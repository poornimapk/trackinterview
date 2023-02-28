import akka.http.scaladsl.model.DateTime
import sangria.validation.Violation
package object models {
  case class User(id: Int, email: String, firstName: String, lastName: String, createdAt: DateTime)

  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  
}
