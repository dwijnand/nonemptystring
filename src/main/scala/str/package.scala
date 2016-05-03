import scala.StringContext

package object str {
  implicit def nonEmptyStringContext(sc: StringContext): NonEmptyStringContext = NonEmptyStringContext(sc)
}
