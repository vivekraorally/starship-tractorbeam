package com.rallyhealth.starship.config

import com.rallyhealth.starship.module.StarshiptractorbeamApplication
import com.rallyhealth.starship.tractorbeam.config.tractorbeamConfig
import com.rallyhealth.starship.mongo.MongoExecutionContext
import com.softwaremill.macwire.{Macwire, Wired}
import play.filters.headers.SecurityHeadersFilter

/**
 * The global settings object for the play framework.
 * For more information @see https://www.playframework.com/documentation/2.3.x/ScalaGlobal
 */
trait StarshiptractorbeamGlobalSetting
  extends StarshipGlobalSetting
  with MongoExecutionContext
  with Macwire {

  override lazy val wired: Wired = wiredInModule(StarshiptractorbeamApplication)

  protected override def filters = super.filters ++ Seq(
    new SecurityHeadersFilter()
  )
}

object StarshiptractorbeamGlobalSetting extends StarshiptractorbeamGlobalSetting {
  override def serviceMongoDatabase: String = wired.lookupSingleOrThrow(classOf[tractorbeamConfig]).tractorbeamDatabase
}

