package sangria

//import sangria.schema.{Field, ListType, ObjectType}
import models._
import sangria.schema._
import sangria.macros.derive._

object GraphQLSchema {
  val UserType = ObjectType[Unit, User](
    "User",
    fields[Unit, User](
      Field("id", IntType, resolve = _.value.id),
      Field("email", StringType, resolve = _.value.email),
      Field("firstName", StringType, resolve = _.value.firstName),
      Field("lastName", StringType, resolve = _.value.lastName)
    )
  )

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allUsers", ListType(UserType), resolve = c => c.ctx.dao.allUsers)
    )
  )

  val SchemaDefinition = Schema(QueryType)
}
