package com.rallyhealth.starship.tractorbeam.model.generators

import com.rallyhealth.starship.tractorbeam.model.tractorbeamName
import com.rallyhealth.testutil.generators.DateTimeGenerators
import org.scalacheck.Gen

trait tractorbeamNameGenerators extends DateTimeGenerators {
  def gentractorbeamName: Gen[tractorbeamName] = Gen.alphaStr map tractorbeamName

  def genListOftractorbeamName(n: Int): Gen[List[tractorbeamName]] = Gen.listOfN(n, gentractorbeamName)
}
