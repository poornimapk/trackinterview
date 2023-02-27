package sangria
import models._
import sangria.schema._

object Arguments {
  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))
}
