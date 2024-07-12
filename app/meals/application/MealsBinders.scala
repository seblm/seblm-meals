package meals.application

import play.api.mvc.{PathBindable, QueryStringBindable}

import java.time.{LocalDate, LocalDateTime, Year}
import scala.util.Try

object MealsBinders:

  given localDateTimeBinder: QueryStringBindable[LocalDateTime] = new QueryStringBindable[LocalDateTime]:

    private val binder = summon[QueryStringBindable[String]]

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalDateTime]] =
      binder
        .bind(key, params)
        .map(_.flatMap: localDateTime =>
          Try(LocalDateTime.parse(localDateTime.replace(' ', 'T'))).toEither.left
            .map(_ => s"Parameter $key '$localDateTime' is not valid"))

    override def unbind(key: String, localDateTime: LocalDateTime): String = localDateTime.toString.replace('T', ' ')

  given localDatePathBinder: PathBindable[LocalDate] = new PathBindable[LocalDate]:

    private val binder = summon[PathBindable[String]]

    override def bind(key: String, value: String): Either[String, LocalDate] =
      binder
        .bind(key, value)
        .flatMap: localDate =>
          Try(LocalDate.parse(localDate)).toEither.left.map(_ => s"Parameter $key '$localDate' is not valid")

    override def unbind(key: String, localDate: LocalDate): String = localDate.toString

  given yearBinder: PathBindable[Year] = new PathBindable[Year]:

    private val intBinder = summon[PathBindable[Int]]

    override def bind(key: String, yearAsString: String): Either[String, Year] =
      intBinder.bind(key, yearAsString).map(Year.of)

    override def unbind(key: String, year: Year): String = year.getValue.toString
