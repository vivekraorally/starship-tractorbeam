// See https://wiki.audaxhealth.com/display/ENG/Build+Structure#BuildStructure-Localconfiguration
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.url("Rally Plugin Releases", url("https://artifacts.werally.in/artifactory/ivy-plugins-release"))(Resolver.ivyStylePatterns)

resolvers += "Rally Releases" at "https://artifacts.werally.in/artifactory/libs-release"

addSbtPlugin("com.rallyhealth" %% "rally-versioning" % "1.2.0") // must appear before rally-sbt-plugin which depends on version.

addSbtPlugin("com.rallyhealth" %% "rally-sbt-plugin" % "0.5.0")
// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.9")

addSbtPlugin("com.rallyhealth.sbt" %% "rally-docker-sbt-plugin" % "0.5.0")

addSbtPlugin("com.scalapenos" % "sbt-prompt" % "0.2.1")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.2.0")
