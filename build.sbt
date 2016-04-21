import SbtMisc._

lazy val str = project in file(".")

organization := "com.dwijnand"
     version := "1.0.0-SNAPSHOT"
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
   startYear := Some(2016)
 description := "a micro-library to deal with non-empty string"
    homepage := Some(url("https://github.com/dwijnand/str"))

scalaVersion := "2.11.8"

       maxErrors := 15
triggeredMessage := Watched.clearWhenTriggered

scalacOptions ++= "-encoding utf8"
scalacOptions ++= "-deprecation -feature -unchecked -Xlint"
scalacOptions  += "-language:higherKinds"
scalacOptions  += "-language:implicitConversions"
scalacOptions  += "-language:postfixOps"
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yinline-warnings"
scalacOptions  += "-Yno-adapted-args"
scalacOptions ++= "-Yno-predef -Yno-imports"
scalacOptions  += "-Ywarn-dead-code"
scalacOptions  += "-Ywarn-numeric-widen"
scalacOptions  += "-Ywarn-unused"
scalacOptions  += "-Ywarn-unused-import"
scalacOptions  += "-Ywarn-value-discard"

scalacOptions in (Compile, console) -= "-Ywarn-unused-import"
scalacOptions in (Test,    console) -= "-Ywarn-unused-import"

             fork in Test := false
      logBuffered in Test := false
parallelExecution in Test := true

         fork in run := true
cancelable in Global := true

noDocs

pomExtra := pomExtra.value ++ {
    <developers>
        <developer>
            <id>dwijnand</id>
            <name>Dale Wijnand</name>
            <email>dale wijnand gmail com</email>
            <url>dwijnand.com</url>
        </developer>
    </developers>
    <scm>
        <connection>scm:git:github.com/dwijnand/str.git</connection>
        <developerConnection>scm:git:git@github.com:dwijnand/str.git</developerConnection>
        <url>https://github.com/dwijnand/str</url>
    </scm>
}

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
