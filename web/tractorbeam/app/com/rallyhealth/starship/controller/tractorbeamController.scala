package com.rallyhealth.starship.controller

import com.rallyhealth.starship.controller.util.{PlayResponseHelpers, StarshipControllerResponseHelpers}
import com.rallyhealth.starship.model.rest.{DefaultStarshipFormats, StarshipErrorResponse}
import com.rallyhealth.starship.model.{TargetType, ToolName}
import com.rallyhealth.starship.tractorbeam.model._
import com.rallyhealth.starship.tractorbeam.service.tractorbeamService
import com.rallyhealth.util.logging.DefaultLogger
import com.wordnik.swagger.annotations._
import play.api.mvc.EssentialAction
import scala.concurrent.Future

import scala.util.control.NonFatal

@Api(value="/tractorbeam", description="Endpoints to interace with tractorbeam")
trait tractorbeamController {

  /**
    * Returns the index html page / SPA of tractorbeam
    */
  def index(path: String): EssentialAction

  @ApiOperation(
    value = "Get all tractorbeams a particular Service belongs too.",
    nickname = "GetRelmas",
    httpMethod = "GET",
    response = classOf[List[tractorbeamName]])
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "Cookie",
        value = "Play Session Cookie obtained at login",
        required = true,
        dataType = "String",
        paramType = "header"
      )))
  def gettractorbeams: EssentialAction
}



/**
 * tractorbeam Controller, which holds all entrypoints for the tractorbeam
 * <p>
 *   @see [[StarshipController]] and [[tractorbeamToolInfo]] for available tasks and sessions.
 *   @see [[StarshipControllerArg]] for what dependencies and restrictions are used by this controller.
 *
 * @param tractorbeamService is the service that tackles tractorbeam repository to retrieve data.
 * @param starshipControllerArg is a beast dragon that holds the gate and kicks everyone not allowed out.
 */
class tractorbeamControllerImpl (
  tractorbeamService: tractorbeamService,
  protected val starshipControllerArg: StarshipControllerArg
  ) extends StarshipController
  with tractorbeamController
  with DefaultPlayContext
  with PlayResponseHelpers
  with StarshipControllerResponseHelpers
  with DefaultLogger
  with DefaultStarshipFormats {

  import tractorbeamToolInfo._
  import play.api.mvc._

  /** Define the scope of this Controller in [[StarshipController]] */
  override val toolName: ToolName = ToolName.ToolTestData

  override def index(path: String) = secureActionAsync()(Main, Index, PageLoadEndpoint) { (jwt, _) =>
    Future.successful(PageLoadAction(Ok(views.html.tractorbeam.index())))
  }

  /**
   * Entry point gettractorbeams takes a ServiceName and returns the universe of tractorbeams this Service might belong to.
   * @return a Future with a list of tractorbeamdetails defined above.
   */
  override def gettractorbeams: EssentialAction = secureActionAsync()(Main, Gettractorbeams) {(_, request) =>
    (for {
       result <- tractorbeamService.gettractorbeams.toJson
      } yield CompletedAction(Some(result), TargetType.Services, None, None, None))
      .recover {
        case NonFatal(ex) => new AbortedAction(
          StarshipErrorResponse(
            errorKey = StarshipErrorResponse.GenericError,
            description = s"Bad things happened"
          ), Some(ex)
        ) with BadRequestResponse
      }
  }
}
