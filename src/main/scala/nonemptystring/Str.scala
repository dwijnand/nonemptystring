package nonemptystring

import java.lang.String
import scala.{ AnyVal, Boolean, inline }

/** `Str` represents a coproduct of the empty string and non-empty strings, forcing you to reason about the
  * behaviour of your code in the presence of an empty string.
  *
  * For this reason it doesn't provide typical string-like methods, nor does it provide an implicit to unlift it
  * back to String.
  *
  * The implementation is a value class which means, within the limits of value classes, there should be no
  * runtime cost.
  *
  * @param value the underlying string value
  */
final case class Str(value: String) extends AnyVal {
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
