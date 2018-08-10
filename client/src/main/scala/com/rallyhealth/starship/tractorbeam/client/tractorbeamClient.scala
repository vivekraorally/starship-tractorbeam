package com.rallyhealth.starship.tractorbeam.client

import com.rallyhealth.argosy.ContextPassingExecutionContext
import com.rallyhealth.starship.model.rest.HealthCheckResponse
import com.rallyhealth.util.logging.{DefaultLogger, HasLogger}
import com.rallyhealth.wsclient.RallyWSClient
import play.api.http.Status._

import scala.concurrent.{ExecutionContext, Future}

/**
 * A client for invoking tractorbeam rest services.
 */
trait tractorbeamClient extends HasLogger {

  /**
   * Checks health of tractorbeam. Could be used to check connectivity to tractorbeam.
   */
  def healthCheck: Future[HealthCheckResponse]
}

/**
 * tractorbeam client implementation that passes authn session details along with every request.
 *
 * @param ws The WS client to use to invoke tractorbeam
 * @param tractorbeamClientConfig The client configuration instance
 */
class WStractorbeamClient(
  protected val ws: RallyWSClient,
  tractorbeamClientConfig: tractorbeamClientConfig
) extends tractorbeamClient
  with tractorbeamClientHelper
  with DefaultLogger {

  protected lazy val baseStarshipURL: String = tractorbeamClientConfig.starshipBaseURL
  protected lazy val basetractorbeamURL = s"$baseStarshipURL/rest/tractorbeam/v1"

  implicit protected lazy val executionContext: ExecutionContext =
    ContextPassingExecutionContext.Implicits.global

  /**
   * Checks health of tractorbeam. Could be used to check connectivity to tractorbeam.
   */
  override def healthCheck: Future[HealthCheckResponse] = {
    val url: String = s"$basetractorbeamURL/monitor"
    ws.url(url)
      .get()
      .flatMap { implicit response =>
        response.status match {
          case OK =>
            response.json.validate[HealthCheckResponse]
              .map(Future.successful)
              .recoverTotal(jsonErrorHandler)
          case status =>
            onErrorStatus(status, url)
        }
    }
  }

}
