name := """twitter-stream-scala"""

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.twitter4j"            % "twitter4j-stream" % "4.0.1",
  "com.netflix.rxjava"       % "rxjava-scala"     % "0.18.3",
  "net.databinder.dispatch" %% "dispatch-core"    % "0.11.1",
  "org.json4s"              %% "json4s-jackson"   % "3.2.9" exclude("org.scala-lang", "scalap"),
  "redis.clients"            % "jedis"            % "2.4.2",
  "org.zeromq"               % "jeromq"           % "0.3.2"
)

net.virtualvoid.sbt.graph.Plugin.graphSettings

Revolver.settings
