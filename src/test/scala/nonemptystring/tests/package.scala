package nonemptystring

import scala.Console.{ println => _, _ }
import scala.collection.immutable.WrappedString

package object tests {
  val PassGreen = GREEN + "\u2713" + RESET // check mark
  val FailRed   = RED + "\u2717" + RESET   // cross mark

  type ->[+A, +B]   = scala.Product2[A, B]
  type Any          = scala.Any
  type AnyVal       = scala.AnyVal
  type Boolean      = scala.Boolean
  type inline       = scala.inline
  type Iterable[+A] = scala.collection.immutable.Iterable[A]
  type Seq[+A]      = scala.collection.immutable.Seq[A]
  type String       = java.lang.String
  type Unit         = scala.Unit

  val Iterable = scala.collection.immutable.Iterable
  val Seq      = scala.collection.immutable.Seq

  private def stdout                  = scala.Console.out
  private def echoOut(msg: Any): Unit = stdout println msg
  private def render[A](x: A): String = x.toString

  def identity[A](x: A): A = x
  def println[A](x: A): Unit = echoOut(render(x))

  implicit def wrapString(s: String): WrappedString = if (s ne null) new WrappedString(s) else null

  implicit class ArrowAssocRef[A](val self: A) extends AnyVal {
    @inline def -> [B](y: B): (A, B) = (self, y)
  }

  type Prop       = org.scalacheck.Prop
  type Result     = org.scalacheck.Test.Result
  type Test       = org.junit.Test
  type TestParams = Test.Parameters

  val Pretty = org.scalacheck.util.Pretty
  val Prop   = org.scalacheck.Prop
  val Test   = org.scalacheck.Test

  def junitAssert(body: => Boolean): Unit      = org.junit.Assert assertTrue body
  def sideEffect[A](result: A, exprs: Any*): A = result
}
