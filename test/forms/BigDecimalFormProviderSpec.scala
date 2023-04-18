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

import forms.behaviours.BigDecimalFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class BigDecimalFormProviderSpec extends BigDecimalFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"

  val generatedBigDecimal: Gen[BigDecimal] = Gen.choose(BigDecimal(1), maxValue)

  val form = new BigDecimalFormProvider()(prefix)

  ".value" - {

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
  }
}
