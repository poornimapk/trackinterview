package sangria

//import sangria.schema.{Field, ListType, ObjectType}
import models._
import sangria.schema._
import sangria.macros.derive._
import sangria.execution.deferred.{Fetcher, HasId, DeferredResolver}
import sangria.ast.StringValue
import akka.http.scaladsl.model.DateTime

import scala.concurrent.ExecutionContext.Implicits.global

object GraphQLSchema {

  implicit val GraphQLDateTime = ScalarType[DateTime](
    "DateTime",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(dt, _, _, _, _) => DateTime.fromIsoDateTimeString(dt).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String => DateTime.fromIsoDateTimeString(s).toRight(DateTimeCoerceViolation)
      case _ => Left(DateTimeCoerceViolation)
    }
  )
  implicit val UserType = deriveObjectType[Unit, User](
    ReplaceField("createdAt", Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt))
  )

  implicit val CompanyType = deriveObjectType[Unit, Company](
    ReplaceField("createdAt", Field("createdAt", GraphQLDateTime, resolve = _.value.createdAt))
  )
  /*implicit val UserType = ObjectType[Unit, User](
    "User",
    fields[Unit, User](
      Field("id", IntType, resolve = _.value.id),
      Field("email", StringType, resolve = _.value.email),
      Field("firstName", StringType, resolve = _.value.firstName),
      Field("lastName", StringType, resolve = _.value.lastName)
    )
  )*/

  implicit val userHasId = HasId[User, Int](_.id)
  val usersFetcher = Fetcher(
    (ctx: MyContext, ids: Seq[Int]) => ctx.dao.getUsers(ids)
  )

  implicit val companyHasId = HasId[Company, Int](_.id)
  val companiesFetcher = Fetcher(
    (ctx: MyContext, ids: Seq[Int]) => ctx.dao.getCompanies(ids)
  )

  val Resolver = DeferredResolver.fetchers(usersFetcher, companiesFetcher)

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
      ),
      Field("allCompanies", ListType(CompanyType), description = Some(s"Returns all Companies"), arguments = Nil, resolve = c => c.ctx.dao.allCompanies),
      Field("companies",
        ListType(CompanyType),
        description = Some(s"Returns one or more companies based on the input Ids argument"),
        arguments = Ids :: Nil,
        //resolve = c => c.ctx.dao.getUsers(c.arg(Ids))
        resolve = c => companiesFetcher.deferSeq(c.arg(Ids))
      ),
    )
  )

  val SchemaDefinition:Schema[MyContext, Any] = Schema(QueryType)
}
