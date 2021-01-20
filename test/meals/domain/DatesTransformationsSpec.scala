package meals.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import java.time.{LocalDateTime, Year}

class DatesTransformationsSpec extends AnyFlatSpec {

  "DatesTransformations" should "get first and last day of last week of 2020" in {
    val (firstDay, lastDay) = DatesTransformations.range(Year.of(2020), 53)

    firstDay shouldBe LocalDateTime.parse("2020-12-28T00:00")
    lastDay shouldBe LocalDateTime.parse("2021-01-03T23:59:59.999999999")
  }

  it should "get first and last day of first week of 2021" in {
    val (firstDay, lastDay) = DatesTransformations.range(Year.of(2021), 1)

    firstDay shouldBe LocalDateTime.parse("2021-01-04T00:00")
    lastDay shouldBe LocalDateTime.parse("2021-01-10T23:59:59.999999999")
  }

  it should "get first and last day of second week of 2021" in {
    val (firstDay, lastDay) = DatesTransformations.range(Year.of(2021), 2)

    firstDay shouldBe LocalDateTime.parse("2021-01-11T00:00")
    lastDay shouldBe LocalDateTime.parse("2021-01-17T23:59:59.999999999")
  }

  it should "get year and week of January 1st 2021" in {
    val (year, week) = DatesTransformations.yearWeek(LocalDateTime.parse("2021-01-01T00:00:00"))

    year shouldBe Year.of(2020)
    week shouldBe 53
  }

  it should "get year and week of Saturday, January 16th 2021" in {
    val (year, week) = DatesTransformations.yearWeek(LocalDateTime.parse("2021-01-16T16:08:21"))

    year shouldBe Year.of(2021)
    week shouldBe 2
  }

}
