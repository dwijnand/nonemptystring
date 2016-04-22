package str.tests

import scala.collection.immutable

class FooBundle extends ScalaCheckBundle {
  def bundle = "foo"
  def props = immutable.Seq(
    "bar" -> Prop(1 == 1),
    "baz" -> Prop("abc".nonEmpty)
  )
}
