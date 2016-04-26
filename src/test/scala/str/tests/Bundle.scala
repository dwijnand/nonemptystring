package str
package tests

import scala.Console.{ println => _, _ }

trait Bundle {
  def bundle: String
  def run(): Boolean

  override def toString = bundle
}

final class NamedProp(val label: String, p: Prop) {
  def prop = p :| label
  def check: Test.Result = Test.check(p)(identity)
}
object NamedProp {
  def apply(label: String, p: Prop): NamedProp                     = new NamedProp(label, p)
  implicit def liftSeqPair(x: String -> Iterable[Prop]): NamedProp = NamedProp(x._1, if (x._2.isEmpty) Prop.undecided else x._2 reduceLeft (_ && _))
  implicit def liftPair(x: String -> Prop): NamedProp              = NamedProp(x._1, x._2)
}

trait ScalaCheckBundle extends Bundle {
  def props: Seq[NamedProp]

  def pass = PassGreen
  def fail = FailRed
  def start = "+ " + BOLD + CYAN + bundle + RESET

  def pp(r: Result) = Pretty.pretty(r, Pretty.Params(0))
  def runOne(p: NamedProp): Boolean = p.check match {
    case x if x.passed => sideEffect(true,  println("+ %s  %s".format(pass, p.label)))
    case r             => sideEffect(false, println("- %s  %s\nFalsified after %s passed tests\n%s".format(fail, p.label, r.succeeded, pp(r))))
  }

  def run(): Boolean = {
    println("\n" + start)
    props map runOne forall (x => x)
  }

  @Test def runBundle(): Unit = junitAssert(run())
}
