ThisBuild / name := "BeelineTest"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.11.12"

val sparkVersion = "2.4.0"

//lazy val beelinetest = (project in file("."))
//  .settings(
//    assembly / assemblyJarName := "BeelineTestSolution.jar",
//    assembly / mainClass := Some("ru.noname.Solution")
//  )

ThisBuild / libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % sparkVersion)
    .exclude("org.apache.hadoop","hadoop-hdfs")
    .exclude("org.apache.hadoop","hadoop-common")
    .exclude("org.apache.hadoop","hadoop-client")
    .exclude("org.apache.hadoop","hadoop-auth")
    .exclude("org.apache.hadoop","hadoop-annotations"),
  ("org.apache.spark" %% "spark-sql" % sparkVersion)
    .exclude("org.apache.hadoop","hadoop-hdfs")
    .exclude("org.apache.hadoop","hadoop-common")
    .exclude("org.apache.hadoop","hadoop-client")
    .exclude("org.apache.hadoop","hadoop-auth")
    .exclude("org.apache.hadoop","hadoop-annotations")
)

ThisBuild / assemblyMergeStrategy := {
  case PathList("org","aopalliance", xs @ _*) => MergeStrategy.last
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case "git.properties" => MergeStrategy.last
  case "overview.html" => MergeStrategy.last  // Added this for 2.1.0 I think
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}