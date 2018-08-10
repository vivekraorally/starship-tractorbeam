package com.rallyhealth.starship.tractorbeam.client

import com.rallyhealth.authn.models.{QueryParamKeys, RallySession}
import com.rallyhealth.starship.tractorbeam.client.util.tractorbeamClientException
import com.rallyhealth.util.logging.HasLogger
import com.rallyhealth.wsclient.RallyWSClient
import play.api.data.validation.ValidationError
import play.api.http.Status._
import play.api.http.{ContentTypeOf, ContentTypes, Writeable, DefaultWriteables}
import play.api.libs.json._
import play.api.libs.ws.{WSRequestHolder, WSResponse}
import play.api.mvc.Codec

import scala.concurrent.{ExecutionContext, Future}


trait JsonHttpSerializerHelper extends DefaultWriteables {
  /**
   * This allows models to be passed in as the request body and have an implicit Writeable available for them as long
   * as there is a Format[T] in scope, useful for methods that use [[WSRequestHolder.post]] or the like
   */
  implicit def jsonWriteable[T](implicit format: Format[T], codec: Codec): Writeable[T] = {
    Writeable(o => codec.encode(format.writes(o).toString()), Some(ContentTypes.JSON))
  }

  /**
   * This allows the content-type to be set correctly to json for models which are using the implicit
   * [[jsonWriteable]].
   */
  implicit def jsonContentType[T : Format]: ContentTypeOf[T] = ContentTypeOf[T](Some(ContentTypes.JSON))

  /**
   * To make Unit deserialize from Json representation.
   */
  implicit object UnitReads extends Reads[Unit] {
    def reads(json: JsValue) = json match {
      case JsNull => JsSuccess({})
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsnull"))))
    }
  }

}

/**
 * Helper's used by the client while talking to tractorbeam
 */
trait tractorbeamClientHelper extends HasLogger with JsonHttpSerializerHelper {
  protected val ws: RallyWSClient
  implicit protected def executionContext: ExecutionContext
  def needsAuthn: Boolean = true


  trait Responder[R] {
    def respond(implicit response: WSResponse, r: Reads[R]): Future[R]
  }

  object BlankResponder extends Responder[Unit] {
    override def respond(implicit response: WSResponse, r: Reads[Unit]): Future[Unit] = Future successful {}
  }

  class JsonResponder[R] extends Responder[R] {
    override def respond(implicit response: WSResponse, r: Reads[R]): Future[R] = {
      response.json.validate[R]
        .map(Future.successful)
        .recoverTotal(jsonErrorHandler)
    }
  }

  /**
   * Performs a POST request operation for Web Services which the response is of an empty HTTP body.
   *
   * @tparam T is the original content to be converted to JSON passed as the body.
   * @param url the url of the endpoint to hit
   * @param queryString optionally any query parameters to pass in
   * @param body the request body to send, by default this is empty
   * @param successCode which http code from the server indicates success, by default it's NO_CONTENT
   * @param wrt Writeable[T] is an implicit to convert T to a JsResult
   * @param ct implicit to define the content type of the body. [[jsonContentType]] is an implicit for JSON.
   * @param session implicit RallySession to perform the WS request.
   */
  private def replyPostBuilder[T, R](
    url: String,
    queryString: Seq[(String, String)] = Seq.empty[(String, String)],
    body: T = (),
    successCode: Int = OK,
    responder: Responder[R]
    )(implicit wrt: Writeable[T], ct: ContentTypeOf[T], session: RallySession, r: Reads[R]): Future[R] = ws.url(url)
      .withAuthnQueryParam
      .withQueryString(queryString: _*)
      .post(body)(wrt, ct) flatMap { implicit response =>
        response.status match {
          case BAD_REQUEST => Future.failed {
            new IllegalArgumentException(response.body) with tractorbeamClientException
          }
          case `successCode` => responder.respond
          case status => onErrorStatus(status, url)
        }
      }

  def emptyReplyPost[T](
    url: String,
    queryString: Seq[(String, String)] = Seq.empty[(String, String)],
    body: T = (),
    successCode: Int = NO_CONTENT
    )(implicit wrt: Writeable[T], ct: ContentTypeOf[T], session: RallySession): Future[Unit] = replyPostBuilder(url, queryString, body, successCode, BlankResponder)

  def replyPost[T, R](
    url: String,
    queryString: Seq[(String, String)] = Seq.empty[(String, String)],
    body: T = (),
    successCode: Int = NO_CONTENT
    )(implicit wrt: Writeable[T], ct: ContentTypeOf[T], session: RallySession, r: Reads[R]): Future[R] = replyPostBuilder(url, queryString, body, successCode, new JsonResponder[R])

  def performGet[R](
    url: String,
    successCode: Int = OK,
    responder: Responder[R] = new JsonResponder[R]
    )(implicit read: Reads[R], session: RallySession): Future[R] = ws.url(url)
    .withAuthnQueryParam
    .get().flatMap { implicit response =>
    response.status match {
      case OK =>
        responder.respond
      case status =>
        onErrorStatus(status, url)
    }
  }

  /**
   * Adds AuthN authToken and sessionToken query params. AuthN client reads session and query params only.
   *
   * @param holder Play [[WSRequestHolder]] used by client to invoke REST operations
   */
  implicit class WithAuthnQueryParam(holder: WSRequestHolder) {
    def withAuthnQueryParam(implicit session: RallySession): WSRequestHolder =
      if (!needsAuthn) holder else holder.withQueryString(
        QueryParamKeys.AuthToken -> session.authToken.value,
        QueryParamKeys.SessionToken -> session.sessionToken.value
      )
  }

  /**
   * Does necessary logging due to error in json parsing.
   *
   * @param failure [[JsError]] caused due to parsing failure
   * @param response [[WSResponse]] that caused the json error
   * @return Failed [[Future]]
   */
  def jsonErrorHandler[T](failure: JsError)(implicit response: WSResponse): Future[T] = {
    logger.error(s"Invalid Json response from tractorbeam, I/P: ${response.body}, Failure: $failure")
    Future.failed(
      new RuntimeException(s"Invalid Json response from tractorbeam, $failure")
        with tractorbeamClientException
    )
  }

  /**
   * Handles the http status which was not expected by the client
   *
   * @param status HTTP status code
   * @param url The http URL that was invoked for the [[WSResponse]]. This will be logged.
   * @param response [[WSResponse]] causing this failure
   * @return Failed [[Future]]
   */
  def onErrorStatus[T](status: Int, url: String = "")(implicit response: WSResponse): Future[T] = {
    logger.error(s"Http status $status when communicating with tractorbeam, response: ${response.body}, url: $url")
    Future.failed(
      new RuntimeException(s"Received $status when communicating with tractorbeam url: $url.")
        with tractorbeamClientException
    )
  }
}

