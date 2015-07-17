enablePlugins(ScalaJSPlugin)

name := "scalajs-todo-list"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.8.1",
  "com.github.japgolly.scalajs-react" %%% "core" % "0.9.0"
)

jsDependencies ++= Seq(
  "org.webjars" % "react" % "0.12.2" / "react-with-addons.js" commonJSName "React"
)

skip in packageJSDependencies := false

persistLauncher in Compile := true
persistLauncher in Test := false
