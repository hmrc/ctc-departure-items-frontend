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

class WeightFormProviderSpec extends BigDecimalFieldBehaviours with SpecBase with AppWithDefaultMockFixtures {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val grossWeight = BigDecimal("5000")
  val requiredKey         = s"$prefix.error.required"
  val maxErrorKey         = s"$prefix.error.maximum"

  val generatedBigDecimal: Gen[BigDecimal] = Gen.choose(BigDecimal(1), maxValue)

  private val fieldName = "value"

  ".value" - {

    def runTests(form: Form[BigDecimal], decimalPlaceCount: Int, characterCount: Int, totalCount: Int, phaseConfig: PhaseConfig): Unit = {

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

      val decimalPlaces: Int  = 3
      val characterCount: Int = 11
      val totalCount: Int     = 15

      running(app) {
        val form = app.injector.instanceOf[WeightFormProvider].apply(prefix, isZeroAllowed = true, grossWeight)

        runTests(form, decimalPlaces, characterCount, totalCount, mockPhaseConfig)

        "must bind a value greater than the gross weight" in {
          val value  = grossWeight + 1
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }

        "must bind a value equal to the gross weight" in {
          val value  = grossWeight
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }

        "must bind a value less than the gross weight" in {
          val value  = grossWeight - 1
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }
      }
    }

    "post transition" - {
      val app                          = postTransitionApplicationBuilder().build()
      val mockPhaseConfig: PhaseConfig = mock[PhaseConfig]
      when(mockPhaseConfig.phase).thenReturn(Phase.PostTransition)

      val decimalPlaces: Int  = 6
      val characterCount: Int = 16
      val totalCount: Int     = 23

      running(app) {
        val form = app.injector.instanceOf[WeightFormProvider].apply(prefix, isZeroAllowed = true, grossWeight)

        runTests(form, decimalPlaces, characterCount, totalCount, mockPhaseConfig)

        "must not bind a value greater than the gross weight" in {
          val value  = grossWeight + 1
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.errors must contain only FormError(fieldName, maxErrorKey, Seq(grossWeight))
        }

        "must bind a value equal to the gross weight" in {
          val value  = grossWeight
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }

        "must bind a value less than the gross weight" in {
          val value  = grossWeight - 1
          val result = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }

        "must bind value greater than 0 when gross weight is 0" in {
          val grossWeight = 0
          val value       = grossWeight + 1
          val form        = app.injector.instanceOf[WeightFormProvider].apply(prefix, isZeroAllowed = true, grossWeight)
          val result      = form.bind(Map(fieldName -> value.toString)).apply(fieldName)
          result.value.value mustBe value.toString
        }
      }
    }

  }
}
