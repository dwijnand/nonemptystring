import SbtMisc._
import org.scalajs.sbtplugin.cross.CrossProject

lazy val strRoot  = project in file(".") aggregate (str, strJs) configure (buildSetup map (_.simple): _*)
lazy val str      = strCross.jvm
lazy val strJs    = strCross.js
lazy val strCross = CrossProject("str", "strJs", file("."), CrossType.Pure) configure (buildSetup map (_.cross): _*)

lazy val buildSetup: Seq[ProjectMod] = Seq(
  organization := "com.dwijnand",
       version := "1.0.0-SNAPSHOT",
      licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
     startYear := Some(2016),
   description := "a micro-library to deal with empty strings",
       scmInfo := Some(ScmInfo(url("https://github.com/dwijnand/str"), "scm:git:git@github.com:dwijnand/str.git")),
      homepage := scmInfo.value map (_.browseUrl),

        scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0-M4"),

  scalaJSUseRhino in Global := false,

         maxErrors := 15,
  triggeredMessage := Watched.clearWhenTriggered,

  scalacOptions ++= "-encoding utf8",
  scalacOptions ++= "-deprecation -feature -unchecked -Xlint",
  scalacOptions  += "-language:higherKinds",
  scalacOptions  += "-language:implicitConversions",
  scalacOptions  += "-language:postfixOps",
  scalacOptions  += "-Xfuture",
  scalacOptions  += "-Yinline-warnings".for212Plus("-Yopt-warnings:_").value,
  scalacOptions  += "-Yno-adapted-args",
  scalacOptions ++= "-Yno-predef -Yno-imports",
  scalacOptions  += "-Ywarn-dead-code",
  scalacOptions  += "-Ywarn-numeric-widen",
  scalacOptions  += "-Ywarn-unused".if211Plus.value,
  scalacOptions  += "-Ywarn-unused-import".if211Plus.value,
  scalacOptions  += "-Ywarn-value-discard",

  scalacOptions in (Compile, console)  -= "-Ywarn-unused-import",
  scalacOptions in (Test,    console)  -= "-Ywarn-unused-import",
  scalacOptions in (Compile, console) --= "-Yno-predef -Yno-imports",
  scalacOptions in (Test,    console) --= "-Yno-predef -Yno-imports",

  initialCommands in console += "\nimport str._",

  libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.13.1" % "test",
  ProjectMod(
    _    settings (libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"),
    _ jvmSettings (libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test")
      jsConfigure (_ enablePlugins ScalaJSJUnitPlugin)
  ),

               fork in Test := false,
        logBuffered in Test := false,
  parallelExecution in Test := true,
                testOptions += Tests.Argument(TestFrameworks.JUnit, "-s"),

           fork in run := true,
  cancelable in Global := true,

  noDocs,

  pomExtra := pomExtra.value ++ {
    <developers>
      <developer>
        <id>dwijnand</id>
        <name>Dale Wijnand</name>
        <url>https://dwijnand.com</url>
      </developer>
    </developers>
  }
)

// Force the root project to not share sources with the cross project, but still have base = file(".")
baseDirectory := file("./project/root")

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
