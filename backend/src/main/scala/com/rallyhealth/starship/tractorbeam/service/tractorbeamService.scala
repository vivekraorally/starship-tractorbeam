package com.rallyhealth.starship.tractorbeam.service

import com.rallyhealth.starship.config.StarshipConfig
import com.rallyhealth.starship.tractorbeam.model._
import com.rallyhealth.starship.tractorbeam.model.generators.tractorbeamNameGenerators
import org.scalacheck.ops._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

/**
 * Service that takes care of tractorbeam logic using tractorbeam collections.
 *
 */
trait tractorbeamService {

  /**
   * Generated
   *
   * @return a list of [[tractorbeamName]] a particular service is authorized to act on.
   */
  def gettractorbeams: Future[List[tractorbeamName]]
}

class tractorbeamServiceImpl(
  cfg: StarshipConfig
  )(implicit ec: ExecutionContext
  ) extends tractorbeamService
    with tractorbeamNameGenerators {

  override def gettractorbeams: Future[List[tractorbeamName]] =
    Future.successful(genListOftractorbeamName(10).getOrThrow)

}

