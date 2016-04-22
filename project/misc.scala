import sbt._
import sbt.Keys._

import scala.language.implicitConversions

import org.scalajs.sbtplugin.cross.CrossProject
import sbt.internals.{ DslConfigs, DslDisablePlugins, DslEnablePlugins, DslEntry, ProjectSettings }

import scala.collection.immutable

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

  def scalaPartV = scalaVersion(CrossVersion.partialVersion)

  implicit final class AnyWithForScalaVersion[A](val _o: A) {
    def ifScala(p: Int => Boolean) = scalaPartV(_ collect { case (2, y) if p(y) => _o })
    def ifScalaLte(v: Int) = ifScala(_ <= v)
    def ifScalaMag(v: Int) = ifScala(_ == v)
    def ifScalaGte(v: Int) = ifScala(_ >= v)
    def if211Plus = ifScalaGte(11)
    def for212Plus(alt: => A) = ifScalaLte(11)(_ getOrElse alt)
  }

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

  def wordSeq(s: String): immutable.Seq[String] = (s split "\\s+" filterNot (_ == "")).to[immutable.Seq]

  implicit def appendWords: Append.Values[Seq[String], String] = new Append.Values[Seq[String], String] {
    def appendValues(a: Seq[String], b: String): Seq[String] = a ++ wordSeq(b)
  }

  implicit def removeWords: Remove.Values[Seq[String], String] = new Remove.Values[Seq[String], String] {
    def removeValues(a: Seq[String], b: String): Seq[String] = a filterNot wordSeq(b).contains
  }

  final case class ProjectMod(simple: Project => Project, cross: CrossProject => CrossProject)

  implicit def settingsDefinitionToProjectMod(sd: SettingsDefinition): ProjectMod =
    ProjectMod(_ settings (sd.settings: _*), _ settings (sd.settings: _*))

  // Manually chain the implicit conversion
  implicit def settingsToProjectMod(ss: Seq[Setting[_]]): ProjectMod = settingsDefinitionToProjectMod(ss)

  implicit def dslEntryToProjectMod(dslEntry: DslEntry): ProjectMod = dslEntry match {
    case ProjectSettings(ss)        => ProjectMod(_ settings       (ss: _*),      _ settings       (ss: _*)     )
    case DslEnablePlugins(plugins)  => ProjectMod(_ enablePlugins  (plugins: _*), _ enablePlugins  (plugins: _*))
    case DslDisablePlugins(plugins) => ProjectMod(_ disablePlugins (plugins: _*), _ disablePlugins (plugins: _*))
    case DslConfigs(configs)        => ProjectMod(_ configs        (configs: _*), _ configs        (configs: _*))
  }
}
