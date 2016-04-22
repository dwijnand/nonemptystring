package str.tests

class FooBundle extends ScalaCheckBundle {
  def bundle = "foo"
  def props = Seq(
    "bar" -> Prop(1 == 1),
    "baz" -> Prop("abc".nonEmpty)
  )
}
