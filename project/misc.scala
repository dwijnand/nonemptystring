import sbt._, Keys._

object SbtMisc {
  val noDocs    = Def.settings(sources in (Compile, doc) := Nil, publishArtifact in (Compile, packageDoc) := false)
  val noPackage = Def.settings(Keys.`package` := file(""), packageBin := file(""), packagedArtifacts := Map())
  val noPublish = Def.settings(
    publishArtifact := false,
    publish         := {},
    publishLocal    := {},
    publishM2       := {},
    publishTo       := Some(Resolver.file("devnull", file("/dev/null")))
  )
  val noArtifacts = Def.settings(noPackage, noPublish)

  // Remove with sbt 0.13.12+
  implicit def appendOption[T]: Append.Sequence[Seq[T], Option[T], Option[T]] =
    new Append.Sequence[Seq[T], Option[T], Option[T]] {
      def appendValue(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)
      def appendValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a :+ _)
    }

  // Remove with sbt 0.13.12+
  implicit def removeOption[T]: Remove.Value[Seq[T], Option[T]] with Remove.Values[Seq[T], Option[T]] =
    new Remove.Value[Seq[T], Option[T]] with Remove.Values[Seq[T], Option[T]] {
      def removeValue(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
      def removeValues(a: Seq[T], b: Option[T]): Seq[T] = b.fold(a)(a filterNot _.==)
    }

  implicit def appendWords: Append.Values[Seq[String], String] = new Append.Values[Seq[String], String] {
    def appendValues(a: Seq[String], b: String): Seq[String] = a ++ (b split "\\s+" filterNot (_ == ""))
  }
}
