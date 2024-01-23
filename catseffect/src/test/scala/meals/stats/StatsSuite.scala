package meals.stats

import cats.effect.{IO, Resource, ResourceIO}
import cats.syntax.either.*
import io.r2dbc.spi.{Connection, ConnectionFactories, Result}
import munit.CatsEffectSuite
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.concurrent.duration.*

class StatsSuite extends CatsEffectSuite:

  private def createConnection(factory: Publisher[_ <: Connection]): ResourceIO[Connection] =
    Resource.make[IO, Connection](IO.async_[Connection]: cb =>
      factory.subscribe(new ConnectionSubscriber(cb)))(connection =>
      IO.async_[Unit]: cb =>
        connection.close().subscribe(new VoidSubscriber(cb))
    )

  private class ConnectionSubscriber(cb: Either[Throwable, Connection] => Unit) extends Subscriber[Connection]:
    private val logger: Logger = LoggerFactory.getLogger("seblm-meals.ConnectionSubscriber")
    override def onSubscribe(s: Subscription): Unit =
      logger.info("onSubscribe")
      s.request(1)
    override def onNext(connection: Connection): Unit =
      logger.debug("onNext")
      cb(connection.asRight)
    override def onError(throwable: Throwable): Unit =
      logger.error("onError", throwable)
      cb(throwable.asLeft)
    override def onComplete(): Unit = logger.info("onComplete")
  end ConnectionSubscriber

  private class VoidSubscriber(cb: Either[Throwable, Unit] => Unit) extends Subscriber[Void]:
    private val logger: Logger = LoggerFactory.getLogger("seblm-meals.VoidSubscriber")
    override def onSubscribe(subscription: Subscription): Unit =
      logger.info("onSubscribe")
      subscription.request(1)
    override def onNext(void: Void): Unit =
      logger.debug("onNext")
      cb(().asRight)
    override def onError(throwable: Throwable): Unit =
      logger.error("onError", throwable)
      cb(throwable.asLeft)
    override def onComplete(): Unit = logger.info("onComplete")
  end VoidSubscriber

  private class StringSubscriber(cb: Either[Throwable, String] => Unit) extends Subscriber[String]:
    private val logger: Logger = LoggerFactory.getLogger("seblm-meals.StringSubscriber")
    override def onSubscribe(subscription: Subscription): Unit =
      logger.info("onSubscribe")
      subscription.request(100000)
    override def onNext(string: String): Unit =
      logger.debug("onNext {}", string)
      cb(Right(string))
    override def onError(throwable: Throwable): Unit =
      logger.error("onError", throwable)
      cb(Left(throwable))
    override def onComplete(): Unit =
      logger.info("onComplete")
      ()
  end StringSubscriber

  private class ResultSubscriber(cb: Either[Throwable, Vector[String]] => Unit) extends Subscriber[Result]:
    private val logger: Logger = LoggerFactory.getLogger("seblm-meals.ResultSubscriber")
    private val values: mutable.Seq[String] = mutable.Seq()
    override def onSubscribe(s: Subscription): Unit =
      logger.info("onSubscribe")
      s.request(1)
    override def onNext(value: Result): Unit =
      logger.debug("onNext {}", value)
      value.map(_.get("description", classOf[String])).subscribe(new StringSubscriber(values.appended))
    override def onError(throwable: Throwable): Unit =
      logger.error("onError", throwable)
      cb(throwable.asLeft)
    override def onComplete(): Unit =
      logger.info("onComplete")
      cb(values.toVector.asRight)
      ()

  override def munitIOTimeout: Duration = 3.seconds

  test("all meals for monday dinner") {
    val logger: Logger = LoggerFactory.getLogger("seblm-meals.StatsSuite")
    val user = Option(System.getenv("POSTGRESQL_ADDON_USER")).getOrElse("seblm-meals")
    val password = Option(System.getenv("POSTGRESQL_ADDON_PASSWORD")).getOrElse("seblm-database-password")
    val host = Option(System.getenv("POSTGRESQL_ADDON_HOST")).getOrElse("localhost")
    val db = Option(System.getenv("POSTGRESQL_ADDON_DB")).getOrElse("seblm-meals")
    val url = s"r2dbc:postgresql://$user:$password@$host/$db"
    val connectionResource = for {
      factory <- IO(ConnectionFactories.get(url)).map(_.create()).toResource
      connection <- createConnection(factory)
    } yield connection
    val result = connectionResource.use { connection =>
      for {
        statement <- IO(
          connection.createStatement(
            """SELECT meals.description FROM meals
              |INNER JOIN meals_by_time ON meals.id = meals_by_time.meal_id
              |WHERE to_char(meals_by_time.time, 'day HH24:MI') = 'monday    20:00'
              |LIMIT 10""".stripMargin
          )
        )
        result <- IO.async_[Vector[String]]: cb =>
          statement.execute().subscribe(new ResultSubscriber(cb))
      } yield result
    }

    assertIO(result.guarantee(IO(logger.debug("end of result"))), Vector("first one", "second one"))
  }
