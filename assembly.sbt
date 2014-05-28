import AssemblyKeys._

assemblySettings

jarName in assembly := "twitter-stream.jar"

assemblyOption in assembly ~= { _.copy(prependShellScript = Some(defaultShellScript)) }

outputPath in assembly := baseDirectory.value / (jarName in assembly).value

mainClass in assembly := Some("bbuzz.example.ElasticsearchPrinter")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
  case PathList(xs @ _*) if xs.last == "io.netty.versions.properties" => MergeStrategy.rename
  case x   => old(x)
}}
