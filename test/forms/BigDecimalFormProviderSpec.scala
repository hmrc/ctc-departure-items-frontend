/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.PhaseConfig
import forms.behaviours.BigDecimalFieldBehaviours
import models.Phase
import org.mockito.Mockito.when
import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import play.api.test.Helpers.running

class BigDecimalFormProviderSpec extends SpecBase with BigDecimalFieldBehaviours with AppWithDefaultMockFixtures {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"

  val generatedBigDecimal: Gen[BigDecimal] = Gen.choose(BigDecimal(1), maxValue)

  ".value" - {

    def runTests(form: Form[BigDecimal], decimalPlaceCount: Int, characterCount: Int, totalCount: Int, phaseConfig: PhaseConfig): Unit = {

      val fieldName = "value"

      val args = Seq(decimalPlaceCount, characterCount, totalCount)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        generatedBigDecimal.toString
      )

      behave like bigDecimalField(
        form,
        fieldName,
        invalidCharactersError = FormError(fieldName, s"$prefix.error.invalidCharacters", args),
        invalidFormatError = FormError(fieldName, s"$prefix.error.invalidFormat", args),
        invalidValueError = FormError(fieldName, s"$prefix.error.invalidValue", args)
      )(phaseConfig)

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )
    }

    "during transition" - {
      val app                          = transitionApplicationBuilder().build()
      val mockPhaseConfig: PhaseConfig = mock[PhaseConfig]
      when(mockPhaseConfig.phase).thenReturn(Phase.Transition)

      val decimalPlaceTransition: Int   = 3
      val characterCountTransition: Int = 11
      val totalCount: Int               = 15

      running(app) {
        val form = app.injector.instanceOf[BigDecimalFormProvider].apply(prefix, decimalPlaceTransition, characterCountTransition)
        runTests(form, decimalPlaceTransition, characterCountTransition, totalCount, mockPhaseConfig)
      }
    }

    "post transition" - {
      val app                          = postTransitionApplicationBuilder().build()
      val mockPhaseConfig: PhaseConfig = mock[PhaseConfig]
      when(mockPhaseConfig.phase).thenReturn(Phase.PostTransition)

      val decimalPlacePostTransition: Int   = 6
      val characterCountPostTransition: Int = 16
      val totalCount: Int                   = 23

      running(app) {
        val form = app.injector.instanceOf[BigDecimalFormProvider].apply(prefix, decimalPlacePostTransition, characterCountPostTransition)
        runTests(form, decimalPlacePostTransition, characterCountPostTransition, totalCount, mockPhaseConfig)
      }
    }
  }
}
