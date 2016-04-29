package str

import java.lang.String
import scala.{ AnyVal, IllegalArgumentException, inline, None, Option, StringContext, Some, throws }
import scala.reflect.macros.blackbox

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
}

class NonEmptyStringMacros(val c: blackbox.Context) {
  import c.universe._
  def applyImpl(s: Tree): Tree = s match {
    case Literal(Constant(s: String)) =>
      if (s.isEmpty)
        abort("Cannot create a NonEmptyString with the empty string")
      else
        q"""_root_.str.NonEmptyString.unsafeFromString($s)"""
    case t => abort("Can only create an NonEmptyString with a constant string")
  }

  def abort(msg: String) = c.abort(c.enclosingPosition, msg)
}
