package str
package tests

import scala.{ Some, StringContext }

class NonEmptyStringSpec extends ScalaCheckBundle {
  def bundle = "NonEmptyString"
  def props = Seq(
    "apply" -> Seq(
      Prop(NonEmptyString("abc").value == "abc")
//      Prop { val s = "abc"; NonEmptyString(s).value == "abc" }
//      Prop(NonEmptyString(""))
    ),
    "unapply" -> Seq(
      Prop(NonEmptyString.unapply(NonEmptyString("abc")) == Some("abc")),
      Prop(NonEmptyString("abc") match { case NonEmptyString("abc") => true })
    ),
    "unsafeFromString" -> Seq(
      Prop(NonEmptyString.unsafeFromString("abc") == NonEmptyString("abc"))
//      Prop(NonEmptyString.unsafeFromString("") == NonEmptyString("boom"))
    ),
    "fromString" -> Seq(
      Prop(NonEmptyString.fromString("abc") == Some(NonEmptyString("abc"))),
      Prop { val s = "abc"; use(s); NonEmptyString.fromString(s) == Some(NonEmptyString("abc")) },
      Prop(NonEmptyString.fromString("").isEmpty)
    ),
    "unlift" -> Prop((NonEmptyString("abc"): String) == "abc"),
    "nes-interpolator" -> Seq(
      Prop(nes"abc" == NonEmptyString("abc")),
      Prop { val a = "abc"; use(a); nes"[abc = $a]" == NonEmptyString("abc") }
//      Prop(nes"" == NonEmptyString("boom")),
//      Prop { val a = ""; nes"$a" == NonEmptyString("boom") }
    )
  )

  // To suppress "never used" warnings..
  def use[A](x: A): A = x
}
