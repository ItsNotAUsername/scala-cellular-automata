name := "scala-cell-automata"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalafx" %% "scalafx" % "14-R19"

// Add dependency on JavaFX libraries, OS dependent
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m =>
  "org.openjfx" % s"javafx-$m" % "14.0.1" classifier "win"
)