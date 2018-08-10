package com.rallyhealth.starship.tractorbeam.client

import com.rallyhealth.config.RallyConfig

/**
 * Config for tractorbeam client.
 */
trait tractorbeamClientConfig {

  protected def rallyConfig: RallyConfig

  lazy val starshipBaseURL = rallyConfig.get(tractorbeamClientConfig.baseURL, "http://127.0.0.1:8008")
}

object tractorbeamClientConfig {
  val baseURL = "starship.web.baseURL"
}

/**
 * The tractorbeam client config that allows to read files backed by [[com.rallyhealth.config.RallyConfig]] that
 * gives preference to environment config over file config
 */
class tractorbeamClientConfigImpl(confFile: String)
  extends tractorbeamClientConfig {

  def this() = this(tractorbeamClientConfigImpl.defaultConfigFile)

  override protected lazy val rallyConfig = RallyConfig(confFile)
}

object tractorbeamClientConfigImpl {
  val defaultConfigFile = "starship.conf"
}
