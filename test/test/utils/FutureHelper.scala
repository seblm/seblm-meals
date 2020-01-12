package test.utils

import org.scalactic.source
import org.scalatest.MustMatchers._
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures.PatienceConfig

import scala.concurrent.Future
import scala.util.Try

object FutureHelper {

  def eventually[T](fun: => Future[T])(implicit config: PatienceConfig, pos: source.Position): Try[T] = {
    val result = fun
    Eventually.eventually {
      result mustBe Symbol("completed")
      result.value.get
    }
  }

}
