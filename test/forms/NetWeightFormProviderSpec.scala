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
import forms.behaviours.BigDecimalFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class NetWeightFormProviderSpec extends BigDecimalFieldBehaviours with SpecBase with AppWithDefaultMockFixtures {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val grossWeight = BigDecimal("5000")
  val requiredKey         = s"$prefix.error.required"
  val maxErrorKey         = s"$prefix.error.maximum"

  val generatedBigDecimal: Gen[BigDecimal] = Gen.choose(BigDecimal(1), maxValue)

  ".value" - {
    val app  = transitionApplicationBuilder().build()
    val form = app.injector.instanceOf[NetWeightFormProvider].apply(prefix, grossWeight)

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      generatedBigDecimal.toString
    )

    behave like bigDecimalField(
      form,
      fieldName,
      invalidCharactersError = FormError(fieldName, s"$prefix.error.invalidCharacters"),
      invalidFormatError = FormError(fieldName, s"$prefix.error.invalidFormat"),
      invalidValueError = FormError(fieldName, s"$prefix.error.invalidValue")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

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
  }
}
