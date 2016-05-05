import java.lang.String
import scala.{ Any, AnyVal, StringContext }
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty

package object nonemptystring {
  /** `NonEmptyString` is a type that removes the empty string from the possible values of `String`. This is done
    * by guaranteeing it on construction, via one of the methods on the companion object or the `nes` string
    * interpolator.
    *
    * Because all non-empty strings are still strings, an unlift implicit is present so that string-like methods
    * are available.
    *
    * The implementation is a value class which means, within the limits of value classes, there should be no
    * runtime cost.
    *
    * The implementation is not a case class because you can't overload case class companion object apply and also
    * case classes have copy methods that could break the non-empty guarantee. However, given the implementation is
    * a value class, hashCode and equals are correctly defined in terms of the underlying value.
    */
  type NonEmptyString = String Refined NonEmpty

  final implicit class NonEmptyStringContext(val _sc: StringContext) extends AnyVal {
    def nes[A >: Any](args: A*): NonEmptyString = macro NonEmptyStringMacros.nesImpl
  }

  implicit def unliftNonEmptyString(x: NonEmptyString): String = x.get
}
