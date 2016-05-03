package nonemptystring

import java.lang.String
import scala.{ Any, AnyVal, inline, IllegalArgumentException, None, Option, Some, StringContext, throws }
import scala.collection.immutable.Seq
import scala.reflect.macros.blackbox

/** `NonEmptyString` is a type that removes the empty string from the possible values of `String`. This is done
  * by guaranteeing it on construction, via one of the methods on the companion object or the `nes` string
  * interpolator.
  *
  * Because all non-empty strings are still strings, an unlift implicit is present so that string-like methods
  * are available.
  *
  * The implementation is a value class which means, within the limits of value classes, there should be no
  * runtime cost.
  *
  * The implementation is not a case class because you can't overload case class companion object apply and also
  * case classes have copy methods that could break the non-empty guarantee. However, given the implementation is
  * a value class, hashCode and equals are correctly defined in terms of the underlying value.
  *
  * @param value the underlying string value
  */
final class NonEmptyString private (val value: String) extends AnyVal {
  @inline override def toString = value
}

object NonEmptyString {
  def apply(s: String): NonEmptyString           = macro NonEmptyStringMacros.applyImpl
  def unapply(x: NonEmptyString): Option[String] = Some(x.value)

  @throws[IllegalArgumentException]
  def unsafeFromString(s: String): NonEmptyString =
    if (s.isEmpty) throw new IllegalArgumentException("empty string") else new NonEmptyString(s)

  def fromString(s: String): Option[NonEmptyString] = if (s.isEmpty) None else Some(new NonEmptyString(s))

  implicit def unlift(x: NonEmptyString): String = x.value
}

@macrocompat.bundle
class NonEmptyStringMacros(val c: blackbox.Context) {
  import c.universe._

  def applyImpl(s: c.Expr[String]): c.Expr[NonEmptyString] = s.tree match {
    case Literal(Constant(s: String)) =>
      if (s.isEmpty)
        abort("Cannot create a NonEmptyString with the empty string")
      else {
        c.Expr[NonEmptyString](q""" _root_.nonemptystring.NonEmptyString unsafeFromString $s """)
      }
    case _ => abort("Can only create an NonEmptyString with a constant string")
  }

  def nesImpl(args: c.Expr[Any]*): c.Expr[NonEmptyString] = c.prefix.tree match {
    case Apply(_, Seq(Apply(_, parts))) =>
      if (parts.length != args.length + 1)
        abort("wrong number of arguments (" + args.length + ") for interpolated string with " +
          parts.length + " parts")

      if (parts forall { case Literal(Constant("")) => true ; case _ => false })
        if (parts.size == 1)
          abort("Cannot create a NonEmptyString with the empty string")
        else
          abort("Cannot create a NonEmptyString with possibly an empty interpolated string")

      c.Expr[NonEmptyString](q"""
        val parts = _root_.scala.List[_root_.java.lang.String](..$parts)
        val args = _root_.scala.List[_root_.scala.Any](..$args)
        val pi = parts.iterator
        val ai = args.iterator
        val bldr = new _root_.java.lang.StringBuilder(_root_.scala.StringContext treatEscapes pi.next())
        while (ai.hasNext) {
          bldr append ai.next
          bldr append (_root_.scala.StringContext treatEscapes pi.next)
        }
        val res = bldr.toString
        _root_.nonemptystring.NonEmptyString unsafeFromString res
      """)
  }

  def abort(msg: String) = c.abort(c.enclosingPosition, msg)
}
