package str
package tests

class NonEmptyStringSpec extends ScalaCheckBundle {
  def bundle = "NonEmptyString"
  def props = Seq(
    "apply" -> Seq(
      Prop(NonEmptyString("abc").value == "abc")
//      Prop { val s = "abc"; NonEmptyString(s).value == "abc" }
//      Prop(NonEmptyString(""))
    ),
    "unapply" -> Seq(
      Prop(NonEmptyString.unapply(NonEmptyString("abc")) contains "abc"),
      Prop(NonEmptyString("abc") match { case NonEmptyString("abc") => true })
    ),
    "unsafeFromString" -> Seq(
      Prop(NonEmptyString.unsafeFromString("abc") == NonEmptyString("abc"))
//      Prop(NonEmptyString.unsafeFromString("") == NonEmptyString("boom"))
    ),
    "fromString" -> Seq(
      Prop(NonEmptyString fromString "abc" contains NonEmptyString("abc")),
      Prop { val s = "abc"; use(s); NonEmptyString fromString s contains NonEmptyString("abc") },
      Prop(NonEmptyString.fromString("").isEmpty)
    ),
    "unlift" -> Prop((NonEmptyString("abc"): String) == "abc")
  )

  // To suppress "never used" warnings..
  def use[A](x: A): A = x
}
