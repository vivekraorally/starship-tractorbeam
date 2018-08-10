import sbt._
import com.scalapenos.sbt.prompt._
import SbtPrompt.autoImport._
/**
 * Defines all dependencies that are created internally within rally.
 */
object RallyDependencies {

  //Alpha ordering
  private val starshipLibVersion = "14.0.1"

  //Alpha ordering
  lazy val starshipBackend: ModuleID =
    excludeWarpDriveTransitive("com.rallyhealth.starship" %% "starship-backend" % starshipLibVersion % "test->test;compile->compile")
  lazy val starshipModels: ModuleID =
    excludeWarpDriveTransitive("com.rallyhealth.starship" %% "starship-models" % starshipLibVersion)
  lazy val starshipWebCommon: ModuleID =
    excludeWarpDriveTransitive("com.rallyhealth.starship" %% "starship-web-common" % starshipLibVersion % "test->test;compile->compile")

  def excludeWarpDriveTransitive(id: ModuleID) =
    id.exclude("com.rallyhealth.starship", "starship-tractorbeam-client")
      .exclude("com.rallyhealth.starship", "starship-tractorbeam-model")

}

/**
 * Defines all dependencies not created by rally.
 */
object NonRallyDependencies {
  //Alpha ordering
  private val akkaVersion = "2.3.3"
  private val embedMongoVersion = "1.47.3"
  private val jodaTimeVersion = "2.7"
  private val macWireVersion = "1.0.1"
  private val mockitoVersion = "1.10.19"
  private val playVersion = "2.3.10"
  private val playJsonVersion = "2.3.10"
  private val playMongoEvolutionVersion = "0.3.0"
  private val swaggerPlay2Version = "1.3.12"
  private val swaggerPlay2UtilVersion = "1.3.12"
  private val scalaCheckVersion = "1.12.2"
  private val scalaTestVersion = "2.2.4"
  private val scalaTestPlusVersion = "1.2.0"
  private val slf4jVersion = "1.7.12"

  //Alpha ordering
  lazy val akkaTestkit =
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  lazy val embedMongo: ModuleID =
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % embedMongoVersion % Test
  lazy val jodaTime: ModuleID =
    "joda-time" % "joda-time" % jodaTimeVersion
  lazy val loggingSlf4j: ModuleID =
    "org.slf4j" % "slf4j-api" % slf4jVersion
  lazy val macWireMacro: ModuleID =
    "com.softwaremill.macwire" %% "macros" % macWireVersion
  lazy val macWireRuntime: ModuleID =
    "com.softwaremill.macwire" %% "runtime" % macWireVersion
  lazy val mockito: ModuleID =
    "org.mockito" % "mockito-all" % mockitoVersion
  lazy val netty: ModuleID = //TODO: FR-4252 remove after we upgrade play
    "io.netty" % "netty" % "3.9.9.Final" force()
  lazy val play: ModuleID =
    "com.typesafe.play" %% "play" % playVersion
  lazy val playJson: ModuleID =
    "com.typesafe.play" %% "play-json" % playJsonVersion
  lazy val playMongoEvolution: ModuleID =
    "com.scalableminds" %% "play-mongev" % playMongoEvolutionVersion
  lazy val scalaCheck: ModuleID =
    "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  lazy val scalaReflect: ModuleID =
    "org.scala-lang" % "scala-reflect" % StarshipDependencies.scalaVersion
  lazy val scalaTest: ModuleID =
    "org.scalatest" % "scalatest_2.11" % scalaTestVersion % Test
  lazy val scalaTestPlus: ModuleID =
    "org.scalatestplus" %% "play" % scalaTestPlusVersion % Test
  lazy val swaggerPlay2: ModuleID =
    "com.wordnik" %% "swagger-play2" % swaggerPlay2Version
  lazy val swaggerPlay2Util: ModuleID =
    "com.wordnik" %% "swagger-play2-utils" % swaggerPlay2UtilVersion
}

/**
 * Prompt for the project
 */
object Prompt {

  lazy val starshipPrompt = PromptTheme(List(
    gitBranch(clean = fg(green), dirty = fg(blue)).padLeft("[").padRight("] "),
    currentProject(fg(magenta)),
    text(" ➾ ", NoStyle)
  ))

  val $askiiStarship = """

       _____ _____ ___  ______  _____ _   _ ___________
      /  ___|_   _/ _ \ | ___ \/  ___| | | |_   _| ___ \
      \ `--.  | |/ /_\ \| |_/ /\ `--.| |_| | | | | |_/ /
       `--. \ | ||  _  ||    /  `--. \  _  | | | |  __/
      /\__/ / | || | | || |\ \ /\__/ / | | |_| |_| |
      \____/  \_/\_| |_/\_| \_|\____/\_| |_/\___/\_|
                _  __         __                  _____                      __         __
               / |/ /__ __ __/ /________  ___    / ___/__ ___  ___ _______ _/ /____ ___/ /
              /    / -_) // / __/ __/ _ \/ _ \  / (_ / -_) _ \/ -_) __/ _ `/ __/ -_) _  /
             /_/|_/\__/\_,_/\__/_/  \___/_//_/  \___/\__/_//_/\__/_/  \_,_/\__/\__/\_,_/

 (replace using http://patorjk.com/software/taag/)
     ___________________        ____....-----....____
    (________________LL_)   ==============================
        ______\   \_______.--'.  `---..._____...---'
        `-------..__            ` ,/
                    `-._ -  -  - |
                        `-------'
  """
  println($askiiStarship)
}

object StarshipDependencies {

  val scalaVersion = "2.11.6"

  val web = Seq(
    RallyDependencies.starshipBackend,
    RallyDependencies.starshipModels,
    RallyDependencies.starshipWebCommon,
    NonRallyDependencies.akkaTestkit,
    NonRallyDependencies.embedMongo,
    NonRallyDependencies.loggingSlf4j,
    NonRallyDependencies.macWireMacro,
    NonRallyDependencies.macWireRuntime,
    NonRallyDependencies.mockito,
    NonRallyDependencies.netty,
    NonRallyDependencies.playMongoEvolution,
    NonRallyDependencies.swaggerPlay2Util,
    NonRallyDependencies.swaggerPlay2,
    NonRallyDependencies.scalaTestPlus
  )

  val model = Seq(
    RallyDependencies.starshipModels
  )

  val client = model ++ Seq(
    NonRallyDependencies.macWireMacro,
    NonRallyDependencies.macWireRuntime,
    NonRallyDependencies.mockito,
    NonRallyDependencies.scalaTestPlus
  )

  val backend = client ++ Seq(
    RallyDependencies.starshipBackend,
    RallyDependencies.starshipModels,
    RallyDependencies.starshipWebCommon,
    NonRallyDependencies.akkaTestkit,
    NonRallyDependencies.embedMongo,
    NonRallyDependencies.loggingSlf4j,
    NonRallyDependencies.macWireMacro,
    NonRallyDependencies.macWireRuntime,
    NonRallyDependencies.mockito,
    NonRallyDependencies.netty,
    NonRallyDependencies.playMongoEvolution,
    NonRallyDependencies.swaggerPlay2Util,
    NonRallyDependencies.swaggerPlay2,
    NonRallyDependencies.scalaTestPlus
  )
}
