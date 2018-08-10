package com.rallyhealth.starship.tractorbeam.service

import com.rallyhealth.starship.config.{StarshipConfig, StarshipConfigImpl}
import com.rallyhealth.starship.util.AsyncTestHelpers
import com.rallyhealth.starship.tractorbeam.model._
import com.rallyhealth.starship.tractorbeam.model.generators.tractorbeamNameGenerators
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar

class tractorbeamServiceImplTest
  extends WordSpec
  with AsyncTestHelpers
  with MockitoSugar
  with ScalaFutures
  with tractorbeamNameGenerators {

  implicit val ec = scala.concurrent.ExecutionContext.global

  val createConfig: StarshipConfig = new StarshipConfigImpl() {
    override lazy val environmentName = "test"
  }

  class Fixture {
    lazy val config: StarshipConfig = createConfig
    lazy val service: tractorbeamServiceImpl = new tractorbeamServiceImpl(config)
  }

  "a tractorbeamServiceImpl gettractorbeams" should {
    "get all Possible tractorbeam Names" in {
      val fixture = new Fixture

      whenReady(fixture.service.gettractorbeams) { result: List[tractorbeamName] =>
        assert(result.length == 10)
      }
    }
  }
}
