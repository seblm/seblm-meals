package meals.stats

import cats.effect.{IO, Resource}
import munit.CatsEffectSuite
import org.slf4j.{Logger, LoggerFactory}
import skunk.Session
import skunk.implicits.sql
import skunk.codec.text.text
import natchez.Trace.Implicits.noop

import scala.concurrent.duration.*

class StatsSuite extends CatsEffectSuite:

  test("all meals for monday dinner"):
    val logger: Logger = LoggerFactory.getLogger("seblm-meals.StatsSuite")
    val user = Option(System.getenv("POSTGRESQL_ADDON_USER")).getOrElse("seblm-meals")
    val password = Option(System.getenv("POSTGRESQL_ADDON_PASSWORD")).getOrElse("seblm-database-password")
    val host = Option(System.getenv("POSTGRESQL_ADDON_HOST")).getOrElse("localhost")
    val db = Option(System.getenv("POSTGRESQL_ADDON_DB")).getOrElse("seblm-meals")
    val session: Resource[IO, Session[IO]] =
      Session.single(
        host = host,
        user = user,
        database = db,
        password = Some(password),
      )
    val result: IO[List[String]] = session.use: session =>
      for
        meals <- session.execute[String](
            sql"""SELECT meals.description FROM meals
                 |INNER JOIN meals_by_time ON meals.id = meals_by_time.meal_id
                 |WHERE to_char(meals_by_time.time, 'day HH24:MI') = 'monday    20:00'
                 |LIMIT 10""".stripMargin.query(text)
          )
      yield meals

    assertIO(
      result.guarantee(IO(logger.debug("end of result"))),
      List(
        "velouté de légumes",
        "galettes de légumes riz",
        "paniers aux épinards",
        "salade de pâtes",
        "chou-fleur pomme de terre lardons",
        "soupe courgettes",
        "pâtes haricots verts carottes lardons",
        "tarte à la tomate quiche lorraine",
        "courgettes lardons céréales de campagne",
        "galette de légumes carottes courgettes"
      )
    )
