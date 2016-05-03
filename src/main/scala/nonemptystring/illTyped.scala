package nonemptystring

import java.lang.String
import java.util.regex.Pattern
import scala.{ Boolean, StringContext }
import scala.reflect.macros.{ TypecheckException, blackbox }

object illTyped {
  def apply(code: String)(error: String): Boolean = macro applyImpl

  def applyImpl(c: blackbox.Context)(code: c.Expr[String])(error: c.Expr[String]): c.Expr[Boolean] = {
    import c.universe._
    def abort(msg: String) = c.abort(c.enclosingPosition, msg)

    val Literal(Constant(errorStr: String)) = error.tree
    val errorPattern = Pattern.compile(errorStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    val errorMessage = "Expected error matching: " + errorStr

    val Literal(Constant(codeStr: String)) = code.tree
    try {
      val dummy1 = TermName(c.freshName)
      val dummy2 = TermName(c.freshName)
      c.typecheck(c.parse(s"object $dummy1 { val $dummy2 = { $codeStr } }"))
      abort(s"Type-checking succeeded unexpectedly.\n$errorMessage")
    } catch {
      case e: TypecheckException =>
        val msg = e.getMessage
        if (!errorPattern.matcher(msg).matches)
          abort(s"Type-checking failed in an unexpected way.\n$errorMessage\nActual error: $msg")
    }

    c.Expr[Boolean](Literal(Constant(true)))
  }
}
