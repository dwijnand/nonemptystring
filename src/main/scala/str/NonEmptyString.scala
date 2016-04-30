package str

import java.lang.String
import scala.{ AnyVal, IllegalArgumentException, inline, None, Option, Some, throws }
import scala.reflect.macros.Context

final class NonEmptyString private (val value: String) extends AnyVal {
  @inline override def toString = value
}

object NonEmptyString {
  def apply(s: String): NonEmptyString           = macro NonEmptyStringMacros.applyImpl
  def unapply(x: NonEmptyString): Option[String] = Some(x.value)

  @throws[IllegalArgumentException]
  def unsafeFromString(s: String): NonEmptyString =
    if (s.isEmpty) throw new IllegalArgumentException else new NonEmptyString(s)

  def fromString(s: String): Option[NonEmptyString] = if (s.isEmpty) None else Some(new NonEmptyString(s))

  implicit def unlift(x: NonEmptyString): String = x.value
}

object NonEmptyStringMacros {
  def applyImpl(c: Context)(s: c.Expr[String]): c.Expr[NonEmptyString] = {
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
}
