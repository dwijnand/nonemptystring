package nonemptystring
package tests

import scala.{ IllegalArgumentException, Some, StringContext, Throwable }
import scala.util.control.NonFatal

class NonEmptyStringSpec extends ScalaCheckBundle {
  def bundle = "NonEmptyString"
  def props = Seq(
    "apply" -> Seq(
      Prop(NonEmptyString("abc").value == "abc"),
      Prop(illTyped("""NonEmptyString("")""")("Cannot create a NonEmptyString with the empty string")),
      Prop(illTyped("""{ val s = "abc"; NonEmptyString(s).value == "abc" }""")
        ("Can only create an NonEmptyString with a constant string")
      )
    ),
    "unapply" -> Seq(
      Prop(NonEmptyString.unapply(NonEmptyString("abc")) == Some("abc")),
      Prop(NonEmptyString("abc") match { case NonEmptyString("abc") => true })
    ),
    "unsafeFromString" -> Seq(
      Prop(NonEmptyString.unsafeFromString("abc") == NonEmptyString("abc")),
      Prop(throws(NonEmptyString.unsafeFromString(""))(new IllegalArgumentException("empty string")))
    ),
    "fromString" -> Seq(
      Prop(NonEmptyString.fromString("abc") == Some(NonEmptyString("abc"))),
      Prop { val s = "abc"; use(s); NonEmptyString.fromString(s) == Some(NonEmptyString("abc")) },
      Prop(NonEmptyString.fromString("").isEmpty)
    ),
    "unlift" -> Prop((NonEmptyString("abc"): String) == "abc"),
    "nes-interpolator" -> Seq(
      Prop(nes"abc" == NonEmptyString("abc")),
      Prop { val a = "abc"; use(a); nes"[abc = $a]" == NonEmptyString("abc") },
      Prop(illTyped(""" nes"" """)("Cannot create a NonEmptyString with the empty string")),
      Prop(illTyped("""{ val a = ""; nes"$a" }""")
        ("Cannot create a NonEmptyString with possibly an empty interpolated string")
      )
    )
  )

  // To suppress "never used" warnings..
  def use[A](x: A): A = x

  def throws[A](thunk: => A)(exp: Throwable): Boolean = {
    try {
      thunk
      println(s"Expected $exp, didn't throw")
      false
    } catch {
      case NonFatal(t) =>
        if (
          exp.getMessage == t.getMessage &&
          exp.getCause == t.getCause &&
          exp.getClass.isAssignableFrom(t.getClass)
        )
          true
        else {
          println(s"Expected $exp, got $t")
          false
        }
    }
  }
}
