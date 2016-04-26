package str
package tests

class StrSpec extends ScalaCheckBundle {
  def bundle = "Str"
  def props = Seq(
    "isEmpty" -> Seq(Prop(Str("").isEmpty == true), Prop(Str("abc").isEmpty == false)),
    "nonEmpty" -> Seq(Prop(Str("").nonEmpty == false), Prop(Str("abc").nonEmpty == true)),
    "fold" -> Seq(
      Prop(Str("").fold("empty")(_ + "-xyz") == "empty"),
      Prop(Str("abc").fold("empty")(_ + "-xyz") == "abc-xyz")
    ),
    "cata" -> Seq(
      Prop(Str("").cata(_ + "-xyz", "empty") == "empty"),
      Prop(Str("abc").cata(_ + "-xyz", "empty") == "abc-xyz")
    ),
    "getOrElse" -> Seq(
      Prop(Str("").getOrElse("") == ""),
      Prop(Str("").getOrElse("abc") == "abc"),
      Prop(Str("abc").getOrElse("") == "abc"),
      Prop(Str("abc").getOrElse("abc") == "abc"),
      Prop(Str("abc").getOrElse("def") == "abc")
    ),
    "orElse" -> Seq(
      Prop(Str("").orElse(Str("")) == Str("")),
      Prop(Str("").orElse(Str("abc")) == Str("abc")),
      Prop(Str("abc").orElse(Str("")) == Str("abc")),
      Prop(Str("abc").orElse(Str("abc")) == Str("abc")),
      Prop(Str("abc").orElse(Str("def")) == Str("abc"))
    ),
    "exists" -> Seq(
      Prop(Str("").exists(_ == "") == false),
      Prop(Str("").exists(_ == "abc") == false),
      Prop(Str("").exists(_ != "abc") == false),
      Prop(Str("abc").exists(_ == "") == false),
      Prop(Str("abc").exists(_ == "abc") == true),
      Prop(Str("abc").exists(_ != "abc") == false),
      Prop(Str("abc").exists(_ == "def") == false),
      Prop(Str("abc").exists(_ != "def") == true)
    ),
    "forall" -> Seq(
      Prop(Str("").forall(_ == "") == true),
      Prop(Str("").forall(_ == "abc") == true),
      Prop(Str("").forall(_ != "abc") == true),
      Prop(Str("abc").forall(_ == "") == false),
      Prop(Str("abc").forall(_ == "abc") == true),
      Prop(Str("abc").forall(_ != "abc") == false),
      Prop(Str("abc").forall(_ == "def") == false),
      Prop(Str("abc").forall(_ != "def") == true)
    )
  )
}
