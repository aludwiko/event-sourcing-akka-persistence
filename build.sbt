import sbt.Keys.{libraryDependencies, _}
scalaVersion := "2.12.6"

scalafmtOnCompile in ThisBuild := true
scalafmtVersion in ThisBuild := "1.4.0"

organization in ThisBuild := "info.ludwikowski"

val macwireVersion = "2.3.1"
lazy val macwireDependencies = Seq(
  "com.softwaremill.macwire" %% "macros"  % macwireVersion,
  "com.softwaremill.macwire" %% "util"    % macwireVersion,
  "com.softwaremill.common"  %% "tagging" % "2.2.1"
)

val akkaVersion     = "2.5.13"
lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-slf4j"                 % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"                % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"               % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding"      % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence"           % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query"     % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.85"
)

val kafkaVersion = "1.0.1"
lazy val kafkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.21"
)

lazy val loggerDependencies = Seq(
  "ch.qos.logback"             % "logback-classic"  % "1.2.3",
  "ch.qos.logback"             % "logback-core"     % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.0",
  "org.slf4j"                  % "log4j-over-slf4j" % "1.7.25",
  "de.siegmar"                 % "logback-gelf"     % "1.1.0",
  "org.codehaus.janino"        % "janino"           % "3.0.8"
)

lazy val protobufDependencies = Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)

lazy val otherDependencies = Seq(
  "org.typelevel"                %% "cats-core"           % "1.1.0",
  "com.softwaremill.quicklens"   %% "quicklens"           % "1.4.11",
  "com.iheart"                   %% "ficus"               % "1.4.3"
)

lazy val testDependencies = Seq(
  "org.scalatest"              %% "scalatest"                        % "3.0.5",
  "org.mockito"                % "mockito-core"                      % "2.18.3",
  "org.scalacheck"             %% "scalacheck"                       % "1.14.0",
  "com.typesafe.akka"          %% "akka-testkit"                     % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream-testkit"              % akkaVersion,
  "com.github.dnvriend"        %% "akka-persistence-inmemory"        % "2.5.1.1",
  "org.clapper"                %% "classutil"                        % "1.3.0",
).map(_ % Test)

lazy val `event-sourcing` = project
  .in(file("."))
  .aggregate(`es-core`, `es-protobuf`)

lazy val `es-protobuf` = (project in file("protobuf"))
  .settings(
    name := "protobuf",
    libraryDependencies ++= protobufDependencies
  )
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value / "protobuf"
    )
  )

lazy val `es-core` = (project in file("core"))
  .dependsOn(`es-protobuf`)
  .settings(
    resolvers ++= Seq(
      Resolver.bintrayRepo("lonelyplanet", "maven")
    ),
    name := "es-core",
    mainClass in Compile := Some("info.ludwikowski.es.Main"),
    fork in (Test) := true,
    javaOptions in Test += "-Xmx2G",
    parallelExecution in Test := false,
    libraryDependencies ++=
      macwireDependencies ++
        akkaDependencies ++
        loggerDependencies ++
        kafkaDependencies ++
        otherDependencies ++
        protobufDependencies ++
        testDependencies,
  )
  .settings(
    smlBuildSettings ++ Seq(
      wartremoverWarnings in (Compile, compile) --= Seq(
        Wart.Product,
        Wart.Serializable,
        Wart.JavaSerializable
      ),
      wartremoverWarnings in (Test, compile) --= Seq(
        Wart.NonUnitStatements,
        Wart.PublicInference,
        Wart.Any,
        Wart.DefaultArguments,
        Wart.ImplicitParameter,
        Wart.Equals,
        Wart.Product,
        Wart.Serializable
      ),
      scalacOptions in (Test, compile) --= Seq(
        "-Ywarn-value-discard"
      ),
      scalacOptions -= "-Xcheckinit" // https://github.com/scala/bug/issues/10437
    )
  )
