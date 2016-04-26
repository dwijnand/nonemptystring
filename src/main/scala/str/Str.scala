package str

import java.lang.String
import scala.{ AnyVal, Boolean, inline }

final case class Str(unsafeGet: String) extends AnyVal {
  @inline private final def value: String = unsafeGet

  @inline final def isEmpty: Boolean  = value.isEmpty
  @inline final def nonEmpty: Boolean = !value.isEmpty

  @inline final def fold[A](alt: => A)(f: String => A): A = if (isEmpty) alt else f(value)
  @inline final def cata[A](f: String => A, alt: => A): A = if (isEmpty) alt else f(value)

  @inline final def getOrElse(alt: => String): String = if (isEmpty) alt else value
  @inline final def orElse(alt: => Str): Str          = if (isEmpty) alt else this

  @inline final def exists(p: String => Boolean): Boolean = nonEmpty && p(value)
  @inline final def forall(p: String => Boolean): Boolean = isEmpty || p(value)

  @inline override final def toString = value
}
