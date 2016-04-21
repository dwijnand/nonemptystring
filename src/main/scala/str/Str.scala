package str

import scala.{ PartialFunction => ?=> }

final case class Str(private val value: String) extends AnyVal {
  import Str.empty

  @inline final def unsafeGet: String = value

  @inline final def  isEmpty  : Boolean =  value.isEmpty
  @inline final def nonEmpty  : Boolean = !value.isEmpty
  @inline final def isDefined : Boolean = !value.isEmpty

  @inline final def getOrElse(alt: => String): String = if (isEmpty) alt else value

  @inline final def     map(f: String => String): Str = if (isEmpty) this else Str(f(value))
  @inline final def flatMap(f: String => Str   ): Str = if (isEmpty) this else f(value)

  @inline final def fold[A](alt: => A)(f: String => A): A = if (isEmpty) alt else f(value)
  @inline final def cata[A](f: String => A, alt: => A): A = if (isEmpty) alt else f(value)

  @inline final def filter   (p: String => Boolean): Str = if (isEmpty ||  p(value)) this else empty
  @inline final def filterNot(p: String => Boolean): Str = if (isEmpty || !p(value)) this else empty

  @inline final def contains(s: String): Boolean = nonEmpty && value == s

  @inline final def exists(p: String => Boolean): Boolean = nonEmpty && p(value)
  @inline final def forall(p: String => Boolean): Boolean =  isEmpty || p(value)

  @inline final def foreach(f: String => Unit): Unit = if (nonEmpty) f(value)

  @inline final def collect(pf: String ?=> String): Str =
    if (isEmpty) this else Str(pf.applyOrElse(value, (_: String) => ""))

  @inline final def orElse(alt: => Str): Str = if (isEmpty) alt else this

  @inline override final def toString = value
}

object Str extends (String => Str) {
  @inline final val Empty: Str = Str("")
  @inline final def empty: Str = Empty
}
