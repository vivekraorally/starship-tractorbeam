package com.rallyhealth.starship.tractorbeam.client

import com.rallyhealth.authn.models.{AuthToken, RallySession, SessionToken}
import com.rallyhealth.starship.model.rest.HealthCheckResponse
import com.rallyhealth.starship.tractorbeam.client.util.tractorbeamClientException
import com.rallyhealth.wsclient.RallyWSClient
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.FunSpec
import org.scalatest.Matchers._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequestHolder, WSResponse}

import scala.concurrent.Future

class tractorbeamClientSpec
  extends FunSpec
  with MockitoSugar
  with ScalaFutures {

  protected implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(150, Millis))

  class Fixture {
    lazy val clientConfig: tractorbeamClientConfig = mock[tractorbeamClientConfig]
    lazy val wsClient: RallyWSClient = mock[RallyWSClient]
    lazy val client: tractorbeamClient = new WStractorbeamClient(wsClient, clientConfig)

    implicit lazy val defaulSession = RallySession(AuthToken("a"), SessionToken("b"))

    /**
     * Aux method to mock url and queryString to provide a requestHolder.
     * @return [[WSRequestHolder]] a mocked request holder.
     */
    def mockUrlQueryString[T](urlpath: Option[String] = None): WSRequestHolder = {
      val requestHolder: WSRequestHolder = mock[WSRequestHolder]
      when(clientConfig.starshipBaseURL).thenReturn("test")
      if (urlpath.nonEmpty) when(wsClient.url("test/rest/tractorbeam/v1/" + urlpath.get)).thenReturn(requestHolder)
      when(requestHolder.withQueryString(anyVararg())).thenReturn(requestHolder)
      requestHolder
    }
  }

  describe("healthCheck"){
    it("should return Future.failed on any non 200 status") {
      val fixture = new Fixture
      import fixture._
      when(clientConfig.starshipBaseURL).thenReturn("test")
      val requestHolder: WSRequestHolder = mock[WSRequestHolder]
      when(requestHolder.withQueryString(anyVararg())).thenReturn(requestHolder)
      val wSResponse: WSResponse = mock[WSResponse]
      when(requestHolder.get()).thenReturn(Future.successful(wSResponse))
      when(wSResponse.status).thenReturn(500)
      when(wsClient.url(s"test/rest/tractorbeam/v1/monitor")).thenReturn(requestHolder)

      val futureRes = client.healthCheck

      whenReady(futureRes.failed) { th =>
        th.isInstanceOf[tractorbeamClientException] shouldBe true
      }
    }

    it("should return parsed HealthCheckResponse on any 200 success") {
      val fixture = new Fixture
      import fixture._
      when(clientConfig.starshipBaseURL).thenReturn("test")
      val requestHolder: WSRequestHolder = mock[WSRequestHolder]
      when(requestHolder.withQueryString(anyVararg())).thenReturn(requestHolder)
      val wSResponse: WSResponse = mock[WSResponse]
      when(requestHolder.get()).thenReturn(Future.successful(wSResponse))
      when(wSResponse.status).thenReturn(200)
      val response: HealthCheckResponse = HealthCheckResponse("OK", System.currentTimeMillis(), "Test", "Dev")
      when(wSResponse.json).thenReturn(Json.toJson(
        response
      ))
      when(wsClient.url(s"test/rest/tractorbeam/v1/monitor")).thenReturn(requestHolder)

      val futureRes = client.healthCheck

      whenReady(futureRes) { res =>
        res shouldBe response
      }
    }
  }
}
