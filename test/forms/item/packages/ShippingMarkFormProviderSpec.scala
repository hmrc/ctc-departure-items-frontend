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

package forms.item.packages

import forms.Constants.maxShippingMarkLength
import forms.behaviours.StringFieldBehaviours
import forms.item.ShippingMarkFormProvider
import models.domain.StringFieldRegex.stringFieldRegex
import org.scalacheck.Gen
import play.api.data.FormError

class ShippingMarkFormProviderSpec extends StringFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"
  val invalidKey     = s"$prefix.error.invalidCharacters"
  val lengthKey      = s"$prefix.error.maxLength"

  val form = new ShippingMarkFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxShippingMarkLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxShippingMarkLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxShippingMarkLength))
    )

    behave like fieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(stringFieldRegex.regex)),
      maxShippingMarkLength
    )
  }
}
