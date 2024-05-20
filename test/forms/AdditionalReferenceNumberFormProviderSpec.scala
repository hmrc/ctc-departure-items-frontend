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
import forms.behaviours.StringFieldBehaviours
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import models.domain.StringFieldRegex.stringFieldRegexComma
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.{Form, FormError}
import play.api.test.Helpers.running

class AdditionalReferenceNumberFormProviderSpec extends SpecBase with AppWithDefaultMockFixtures with StringFieldBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val invalidKey  = s"$prefix.error.invalidCharacters"
  private val lengthKey   = s"$prefix.error.length"
  private val uniqueKey   = s"$prefix.error.unique"
  private val cl234Key    = s"$prefix.error.cl234Constraint"

  private val values = listWithMaxLength[String]()(Arbitrary(nonEmptyString)).sample.value

  private val maxAdditionalReferenceNumTransitionLength     = 35
  private val maxAdditionalReferenceNumPostTransitionLength = 70

  ".value" - {

    def runTests(form: Form[String], maxAdditionalReferenceNumLength: Int): Unit = {
      val fieldName = "value"

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxAdditionalReferenceNumLength)
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = maxAdditionalReferenceNumLength,
        lengthError = FormError(fieldName, lengthKey, Seq(maxAdditionalReferenceNumLength))
      )

      behave like fieldWithInvalidCharacters(
        form,
        fieldName,
        error = FormError(fieldName, invalidKey, Seq(stringFieldRegexComma.regex)),
        maxAdditionalReferenceNumLength
      )

      behave like fieldThatBindsUniqueData(
        form = form,
        fieldName = fieldName,
        uniqueError = FormError(fieldName, uniqueKey),
        values = values
      )
    }

    "during transition" - {
      val app = transitionApplicationBuilder().build()
      running(app) {
        val form = app.injector.instanceOf[AdditionalReferenceNumberFormProvider].apply(prefix, values, Some(false))
        runTests(form, maxAdditionalReferenceNumTransitionLength)
      }
    }

    "post transition" - {

      val fieldName = "value"

      val app = postTransitionApplicationBuilder().build()
      running(app) {
        val form = app.injector.instanceOf[AdditionalReferenceNumberFormProvider].apply(prefix, values, Some(true))
        runTests(form, maxAdditionalReferenceNumPostTransitionLength)

        behave like fieldWithInvalidInputCL234(
          form,
          fieldName,
          error = FormError(fieldName, cl234Key)
        )

      }
    }
  }
}
