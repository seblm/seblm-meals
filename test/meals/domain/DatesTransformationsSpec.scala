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

  it should "get score" in {
    val reference = LocalDateTime.parse("2021-04-18T12:00")

    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-22T12:00")) shouldBe 179
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-21T12:00")) shouldBe 180
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-20T12:00")) shouldBe 181
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-19T12:00")) shouldBe 182

    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-18T12:00")) shouldBe 183

    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-17T12:00")) shouldBe 182
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-16T12:00")) shouldBe 181
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-15T12:00")) shouldBe 180
    DatesTransformations.score(reference, LocalDateTime.parse("2020-04-14T12:00")) shouldBe 179

    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-14T12:00")) shouldBe 4
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-15T12:00")) shouldBe 3
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-16T12:00")) shouldBe 2
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-17T12:00")) shouldBe 1

    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-18T12:00")) shouldBe 0

    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-19T12:00")) shouldBe 1
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-20T12:00")) shouldBe 2
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-21T12:00")) shouldBe 3
    DatesTransformations.score(reference, LocalDateTime.parse("2020-10-22T12:00")) shouldBe 4

    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-08T12:00")) shouldBe 173
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-09T12:00")) shouldBe 174
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-10T12:00")) shouldBe 175
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-11T12:00")) shouldBe 176
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-12T12:00")) shouldBe 0 // 177
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-13T12:00")) shouldBe 0 // 178
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-14T12:00")) shouldBe 0 // 179
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-15T12:00")) shouldBe 0 // 180
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-16T12:00")) shouldBe 0 // 181
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-17T12:00")) shouldBe 0 // 182

    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-18T12:00")) shouldBe 0 // 183

    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-19T12:00")) shouldBe 0 // 182
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-20T12:00")) shouldBe 0 // 181
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-21T12:00")) shouldBe 0 // 180
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-22T12:00")) shouldBe 0 // 179
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-23T12:00")) shouldBe 0 // 178
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-24T12:00")) shouldBe 0 // 177
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-25T12:00")) shouldBe 176
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-26T12:00")) shouldBe 175
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-27T12:00")) shouldBe 174
    DatesTransformations.score(reference, LocalDateTime.parse("2021-04-28T12:00")) shouldBe 173
  }

}
