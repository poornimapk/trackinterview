package sangria

//import sangria.schema.{Field, ListType, ObjectType}
import models._
import sangria.schema._
import sangria.macros.derive._
import sangria.execution.deferred.{Fetcher, HasId, DeferredResolver}

import scala.concurrent.ExecutionContext.Implicits.global

object GraphQLSchema {

  //implicit val UserType = deriveObjectType[Unit, User]()
  implicit val UserType = ObjectType[Unit, User](
    "User",
    fields[Unit, User](
      Field("id", IntType, resolve = _.value.id),
      Field("email", StringType, resolve = _.value.email),
      Field("firstName", StringType, resolve = _.value.firstName),
      Field("lastName", StringType, resolve = _.value.lastName)
    )
  )

  implicit val userHasId = HasId[User, Int](_.id)
  val usersFetcher = Fetcher(
    (ctx: MyContext, ids: Seq[Int]) => ctx.dao.getUsers(ids)
  )

  val Resolver = DeferredResolver.fetchers(usersFetcher)

  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Any](
      Field("allUsers", ListType(UserType), description = Some(s"Returns all Users"), arguments = Nil, resolve = c => c.ctx.dao.allUsers),
      Field("user",
        OptionType(UserType),
        description = Some(s"Returns one user based on the input Id argument"),
        arguments = Id :: Nil,
        //resolve = c => c.ctx.dao.getUser(c.arg(Id))
        resolve = c => usersFetcher.deferOpt(c.arg((Id)))
      ),
      Field("users",
        ListType(UserType),
        description = Some(s"Returns one or more users based on the input Ids argument"),
        arguments = Ids :: Nil,
        //resolve = c => c.ctx.dao.getUsers(c.arg(Ids))
        resolve = c => usersFetcher.deferSeq(c.arg(Ids))
      )
    )
  )

  val SchemaDefinition:Schema[MyContext, Any] = Schema(QueryType)
}
