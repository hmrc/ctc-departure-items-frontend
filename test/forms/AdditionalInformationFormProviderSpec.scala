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

import forms.Constants.maxAdditionalInformationLength
import forms.behaviours.StringFieldBehaviours
import forms.item.additionalInformation.AdditionalInformationFormProvider
import models.domain.StringFieldRegex.stringFieldRegexComma
import org.scalacheck.Gen
import play.api.data.FormError

class AdditionalInformationFormProviderSpec extends StringFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"
  val invalidKey     = s"$prefix.error.invalidCharacters"
  val lengthKey      = s"$prefix.error.length"

  val form = new AdditionalInformationFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxAdditionalInformationLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxAdditionalInformationLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxAdditionalInformationLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(stringFieldRegexComma.regex)),
      maxAdditionalInformationLength
    )
  }
}
