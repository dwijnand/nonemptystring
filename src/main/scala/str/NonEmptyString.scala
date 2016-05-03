package str

import java.lang.String
import scala.{ Any, AnyVal, IllegalArgumentException, None, Option, Some, inline, throws }
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

object NonEmptyStringMacros {
  def applyImpl(c: blackbox.Context)(s: c.Expr[String]): c.Expr[NonEmptyString] = {
    import c.universe._
    def abort(msg: String) = c.abort(c.enclosingPosition, msg)
    s.tree match {
      case Literal(Constant(s: String)) =>
        if (s.isEmpty)
          abort("Cannot create a NonEmptyString with the empty string")
        else {
          val NonEmptyString = Select(Select(Ident(nme.ROOTPKG), newTermName("str")), newTermName("NonEmptyString"))
          val result = Apply(Select(NonEmptyString, newTermName("unsafeFromString")), scala.List(Literal(Constant(s))))
          c.Expr[NonEmptyString](result)
        }
      case t => abort("Can only create an NonEmptyString with a constant string")
    }
  }

  def nesImpl(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[NonEmptyString] = {
    import c.universe._
    def abort(msg: String) = c.abort(c.enclosingPosition, msg)

    c.prefix.tree match {
      case Apply(_, Seq(Apply(_, parts))) =>
        if (parts.length != args.length + 1)
          abort("wrong number of arguments (" + args.length + ") for interpolated string with " +
            parts .length + " parts")

        if (parts forall { case Literal(Constant("")) => true ; case _ => false })
          if (parts.size == 1)
            abort("Cannot create a NonEmptyString with the empty string")
          else
            abort("Cannot create a NonEmptyString with possibly an empty interpolated string")
    }

    val NonEmptyString = Select(Select(Ident(nme.ROOTPKG), newTermName("str")), newTermName("NonEmptyString"))
    val result = Apply(Select(NonEmptyString, newTermName("unsafeFromString")), scala.List(Literal(Constant("abc"))))
    c.Expr[NonEmptyString](result)
  }
}
