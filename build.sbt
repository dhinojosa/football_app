name := """football_app"""
organization := "com.xyzcorp"

version := sys.env.getOrElse("BUILD_ID", "0.1")
val testContainersScalaVersion = "0.40.11"
lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  guice,
  "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersScalaVersion % "test",
  "com.dimafeng" %% "testcontainers-scala-mongodb" % testContainersScalaVersion % "test",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0",
  "org.webjars.npm" % "bootstrap" % "5.2.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.xyzcorp.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.xyzcorp.binders._"


import com.typesafe.sbt.packager.docker.DockerChmodType

dockerChmodType := DockerChmodType.UserGroupWriteExecute

dockerExposedPorts ++= Seq(9000, 9001)

dockerBaseImage := "eclipse-temurin:11"

Docker/packageName := sys.env.getOrElse("JOB_NAME", "football-app")

Universal/javaOptions ++= Seq(
    // JVM memory tuning
    "-J-Xmx1024m",
    "-J-Xms512m",

    // Since play uses separate pidfile we have to provide it with a proper path
    // name of the pid file must be play.pid
    // s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

    // alternative, you can remove the PID file
    s"-Dpidfile.path=/dev/null",

    // Use separate configuration file for production environment
    s"-Dconfig.file=/opt/docker/conf/production.conf",

    // Use separate logger configuration file for production environment
    s"-Dlogger.file=/opt/docker/conf/production-logback.xml",

    // reference a logback config file that has no file appenders
    //s"-Dlogback.configurationFile=production-logback.xml"

    // You may also want to include this setting if you use play evolutions
    //"-DapplyEvolutions.default=true"
)

