package str

import java.lang.String
import scala.{ AnyVal, Boolean, IllegalArgumentException, inline, None, Option, Some, throws, Unit }

final class NonEmptyString private (val value: String) extends AnyVal {
  @inline final type This = NonEmptyString

  @inline final def contains(s: String): Boolean          = value == s
  @inline final def exists(p: String => Boolean): Boolean = p(value)
  @inline final def forall(p: String => Boolean): Boolean = p(value)
  @inline final def flatMap(f: String => This): This      = f(value)
  @inline final def to[A](f: String => A): A              = f(value)
  @inline final def foreach(f: String => Unit): Unit      = f(value)

  @throws[IllegalArgumentException]
  @inline final def unsafeMap(f: String => String): This = NonEmptyString unsafeFromString f(value)

  @inline override final def toString = value
}

object NonEmptyString { // extends (String => NonEmptyString) {
  // TODO: Make a macro
//  def apply(s: String): NonEmptyString = if (s.isEmpty) ??? else new NonEmptyString(s)

  @throws[IllegalArgumentException]
  def unsafeFromString(s: String): NonEmptyString =
    if (s.isEmpty) throw new IllegalArgumentException else new NonEmptyString(s)

  def fromString(s: String): Option[NonEmptyString] = if (s.isEmpty) None else Some(new NonEmptyString(s))
}
