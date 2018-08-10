package com.rallyhealth.starship.tractorbeam.config

import com.rallyhealth.config.RallyConfig
import com.rallyhealth.starship.config.StarshipConfig

/**
 * Config for tractorbeam.
 */
trait tractorbeamConfig {
  import tractorbeamConfig._

  protected def rallyConfig: RallyConfig
  protected def starshipConfig: StarshipConfig
  
  /**
   * Database for tractorbeam related collections
   */
  lazy val tractorbeamDatabase: String = rallyConfig.get(tractorbeamDatabaseKey, "starship-tractorbeam")
}

object tractorbeamConfig {
  val tractorbeamDatabaseKey = "starship.tractorbeam.database"
}

/**
 * The tractorbeam config that allows to read files backed by [[com.rallyhealth.config.RallyConfig]] that
 * gives preference to environment config over file config
 */
class tractorbeamConfigImpl(confFile: String, val starshipConfig: StarshipConfig)
  extends tractorbeamConfig {

  def this(starshipConfig: StarshipConfig) = this(tractorbeamConfigImpl.defaultConfigFile, starshipConfig)

  override protected lazy val rallyConfig = RallyConfig(confFile)
}

object tractorbeamConfigImpl {
  val defaultConfigFile = "starship.conf"
}
