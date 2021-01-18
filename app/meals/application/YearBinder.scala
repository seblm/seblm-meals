package meals.application

import play.api.mvc.PathBindable

import java.time.Year

object YearBinder {

  implicit def yearBinder(implicit intBinder: PathBindable[Int]): PathBindable[Year] = new PathBindable[Year] {

    override def bind(key: String, yearAsInt: String): Either[String, Year] =
      intBinder.bind(key, yearAsInt).map(Year.of)

    override def unbind(key: String, year: Year): String =
      year.getValue.toString

  }

}
