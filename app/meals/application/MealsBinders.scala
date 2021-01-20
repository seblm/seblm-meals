package meals.application

import meals.domain.MealTime
import meals.domain.MealTime.{Dinner, Lunch}
import play.api.mvc.{PathBindable, QueryStringBindable}

import java.time.Year

object MealsBinders {

  implicit def mealTimeBinder(implicit intBinder: QueryStringBindable[Int]): QueryStringBindable[MealTime] =
    new QueryStringBindable[MealTime] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MealTime]] =
        intBinder
          .bind(key, params)
          .map(_.flatMap {
            case 12    => Right(Lunch)
            case 20    => Right(Dinner)
            case other => Left(s"Parameter $key '$other' is not either 12 nor 20")
          })

      override def unbind(key: String, mealTime: MealTime): String = mealTime.time.toString

    }

  implicit def yearBinder(implicit intBinder: PathBindable[Int]): PathBindable[Year] = new PathBindable[Year] {

    override def bind(key: String, yearAsString: String): Either[String, Year] =
      intBinder.bind(key, yearAsString).map(Year.of)

    override def unbind(key: String, year: Year): String = year.getValue.toString

  }

}
