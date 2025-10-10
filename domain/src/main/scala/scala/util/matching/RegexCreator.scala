package scala.util.matching

import java.util.regex.Pattern

/** Allow to call package private constructor scala.util.matching.Regex(pattern: Pattern)
  */
object RegexCreator {
  def create(pattern: Pattern): Regex = new Regex(pattern)
}
