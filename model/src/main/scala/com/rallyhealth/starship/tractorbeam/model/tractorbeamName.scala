package com.rallyhealth.starship.tractorbeam.model

import com.rallyhealth.util.serialization.PlayJsonHelpers._
import play.api.libs.json.Format

/**
 * Generated 
 *
 * @param neutronName is the Name of the Starship neutron
 */
case class tractorbeamName(neutronName: String) extends AnyVal

object tractorbeamName extends ((String) => tractorbeamName) with Ordering[tractorbeamName] {

  implicit val neutronNameFormat: Format[tractorbeamName] = Format.asString(tractorbeamName, _.neutronName)
  implicit def ordering: Ordering[tractorbeamName] = Ordering.by(_.neutronName)

  override def compare(x: tractorbeamName, y: tractorbeamName): Int = x.neutronName.compare(y.neutronName)
}

