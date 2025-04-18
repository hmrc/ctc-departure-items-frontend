/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.Constants.maxAdditionalReferenceNumLength
import forms.behaviours.StringFieldBehaviours
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import models.domain.StringFieldRegex.stringFieldRegexComma
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.FormError

class AdditionalReferenceNumberFormProviderSpec extends SpecBase with AppWithDefaultMockFixtures with StringFieldBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val invalidKey  = s"$prefix.error.invalidCharacters"
  private val lengthKey   = s"$prefix.error.length"
  private val uniqueKey   = s"$prefix.error.unique"
  private val cl234Key    = s"$prefix.error.cl234Constraint"

  private val values = listWithMaxLength[String]()(Arbitrary(nonEmptyString)).sample.value
  private val form   = new AdditionalReferenceNumberFormProvider().apply(prefix, values, isDocumentInCL234 = true)

  ".value" - {

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

    behave like fieldWithInvalidInputCL234(
      form,
      fieldName,
      error = FormError(fieldName, cl234Key)
    )
  }
}
