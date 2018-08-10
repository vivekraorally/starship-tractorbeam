package com.rallyhealth.starship.module

import com.rallyhealth.starship.controller.{tractorbeamController, tractorbeamControllerImpl}
import com.rallyhealth.starship.tractorbeam.service.{tractorbeamService, tractorbeamServiceImpl}
import com.rallyhealth.starship.tractorbeam.config.{tractorbeamConfig, tractorbeamConfigImpl}
import com.softwaremill.macwire._

/**
 *  The MacWire web module for DI. For a good read about MacWire @see http://di-in-scala.github.io/
 */
trait StarshiptractorbeamWebModule
  extends StarshipWebModule {

  // Note: Alpha ordering
  // Available dependencies exposed from Web module.
  lazy val tractorbeamManagerController: tractorbeamController = wire[tractorbeamControllerImpl]
  lazy val tractorbeamConfig: tractorbeamConfig = wire[tractorbeamConfigImpl]
  //

  //Note: Alpha ordering
  //Dependencies of the Web Module.
  lazy val tractorbeamService: tractorbeamService = wire[tractorbeamServiceImpl]
  //End of to do block
}

