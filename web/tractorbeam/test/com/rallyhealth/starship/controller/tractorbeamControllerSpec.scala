package com.rallyhealth.starship.controller

import com.nimbusds.jwt.JWTClaimsSet
import com.rallyhealth.authn.RallyId
import com.rallyhealth.authn.client.SessionWindow
import com.rallyhealth.authn.models.UserAuthNInfo
import com.rallyhealth.starship.BaseApplicationNoMongoTestSupport
import com.rallyhealth.starship.actor.ActorFactory
import com.rallyhealth.starship.model.ToolName
import com.rallyhealth.starship.service.authorization.StarshipAuthorizationService
import com.rallyhealth.starship.util.{AuthenticateTestFixture, DuoTestFixture, StarshipControllerTestHelpers}
import com.rallyhealth.starship.tractorbeam.model.generators.tractorbeamNameGenerators
import com.rallyhealth.starship.session.{AuthProcessor, AuthProcessorImpl, LegacyFederationValidator}
import com.rallyhealth.starship.tractorbeam.service.tractorbeamService
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class tractorbeamControllerSpec
  extends PlaySpec
  with OneAppPerSuite
  with MockitoSugar
  with ScalaFutures
  with StarshipControllerTestHelpers
  with BaseApplicationNoMongoTestSupport
  with tractorbeamNameGenerators {

  implicit override lazy val app: FakeApplication = fakeApplicationWithGlobal
  override def toolNameUnderTest: ToolName = ToolName.ToolTestData

  val userRallyId = "userRallyId"
  val adminRallyId = "adminRallyId"

  import org.scalacheck.ops._

  class FixtureParam
    extends DuoTestFixture
    with AuthenticateTestFixture
    with MockitoSugar {
    val actorFactory: ActorFactory = mock[ActorFactory]
    val starshipAuthorizationService: StarshipAuthorizationService = mock[StarshipAuthorizationService]
    val tractorbeamService: tractorbeamService = mock[tractorbeamService]
    val starshipConstructorArgs = StarshipControllerArg(
      actorFactory = actorFactory,
      starshipAuthorizationService = starshipAuthorizationService,
      authProcessor = authProcessor,
      mfaClient = mfaClient,
      starshipConfig = starshipConfig
    )

    val rest = new tractorbeamControllerImpl(tractorbeamService, starshipConstructorArgs)
  }

  private def prepareFixture: FixtureParam = {
    val fixture = new FixtureParam
    when(fixture.starshipAuthorizationService.allowed(eqTo(defaultAdminId),
      eqTo(ToolName.ToolTestData),
      any(),
      any(),
      any())).thenReturn(Future.successful(true))
    fixture
  }

  "tractorbeamController" should {

    "check gettractorbeams entry point" in {
      val fixture = prepareFixture
      val serviceResponse = genListOftractorbeamName(2).getOrThrow

      when(fixture.tractorbeamService.gettractorbeams)
        .thenReturn(Future.successful(serviceResponse))
      val request = mockAuthenticateWithMfa(
        fixture,
        FakeRequest("GET", s"/rest/tractorbeam/v1/tractorbeams")
      )

      val response = call(fixture.rest.gettractorbeams, request)
      status(response) mustEqual OK
      contentType(response) mustEqual Some("application/json")
      contentAsJson(response) mustEqual Json.toJson(serviceResponse)
    }
  }
}
