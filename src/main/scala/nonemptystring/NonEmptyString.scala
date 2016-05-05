package nonemptystring

import java.lang.String
import scala.{ Any, IllegalArgumentException, None, Option, Some, StringContext, throws }
import scala.collection.immutable.Seq
import scala.reflect.macros.blackbox
import eu.timepit.refined.api.{ Refined, Validate }
import eu.timepit.refined.macros.RefineMacro
import eu.timepit.refined.api.RefType
import eu.timepit.refined.collection._

object NonEmptyString {
  def apply(t: String)(implicit rt: RefType[Refined], v: Validate[String, NonEmpty]): NonEmptyString =
    macro RefineMacro.impl[Refined, String, NonEmpty]

  def unapply(x: NonEmptyString): Some[String] = Some(x.get)

  @throws[IllegalArgumentException]
  def unsafeFromString(s: String): NonEmptyString =
    if (s.isEmpty) throw new IllegalArgumentException("empty string") else create(s)

  def fromString(s: String): Option[NonEmptyString] = if (s.isEmpty) None else Some(create(s))

  private def create(s: String): NonEmptyString = Refined[String, NonEmpty](s)
}

@macrocompat.bundle
class NonEmptyStringMacros(val c: blackbox.Context) {
  import c.universe._

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
