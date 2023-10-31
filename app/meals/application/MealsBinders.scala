package meals.application

import play.api.mvc.{PathBindable, QueryStringBindable}

import java.time.{LocalDateTime, Year}
import scala.util.Try

object MealsBinders:

  implicit def localDateTimeBinder(implicit binder: QueryStringBindable[String]): QueryStringBindable[LocalDateTime] =
    new QueryStringBindable[LocalDateTime] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalDateTime]] =
        binder
          .bind(key, params)
          .map(_.flatMap { localDateTime =>
            Try(LocalDateTime.parse(localDateTime.replace(' ', 'T'))).toEither.left
              .map(_ => s"Parameter $key '$localDateTime' is not valid")
          })

      override def unbind(key: String, localDateTime: LocalDateTime): String = localDateTime.toString.replace('T', ' ')

    }

  implicit def yearBinder(implicit intBinder: PathBindable[Int]): PathBindable[Year] = new PathBindable[Year] {

    override def bind(key: String, yearAsString: String): Either[String, Year] =
      intBinder.bind(key, yearAsString).map(Year.of)

    override def unbind(key: String, year: Year): String = year.getValue.toString

  }
