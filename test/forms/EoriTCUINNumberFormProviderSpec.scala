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

import forms.Constants._
import forms.behaviours.{FieldBehaviours, StringFieldBehaviours}
import models.domain.StringFieldRegex._
import org.scalacheck.Gen
import play.api.data.FormError

class EoriTCUINNumberFormProviderSpec extends StringFieldBehaviours with FieldBehaviours { // TODO: Add back minlength and regexp once CTCP-5502 is in play

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey  = s"$prefix.error.required"
  private val maxLengthKey = s"$prefix.error.maxLength"
  // private val minLengthKey         = s"$prefix.error.minLength"
  private val invalidCharactersKey = s"$prefix.error.invalidCharacters"
  // private val invalidFormatKey     = s"$prefix.error.invalidFormat"

  private val form = new EoriTCUINNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxEoriNumberLength)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidCharactersKey, Seq(alphaNumericRegex.regex)),
      length = maxEoriNumberLength
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = maxEoriNumberLength,
      lengthError = FormError(fieldName, maxLengthKey, Seq(maxEoriNumberLength))
    )

    //    behave like fieldWithMinLength(
    //      form = form,
    //      fieldName = fieldName,
    //      minLength = minLengthCarrierEori,
    //      lengthError = FormError(fieldName, minLengthKey, Seq(minLengthCarrierEori))
    //    )

    "must remove spaces on bound strings" in {
      val result = form.bind(Map(fieldName -> " GB 123 456 789 123 "))
      result.errors mustEqual Nil
      result.get mustEqual "GB123456789123"
    }

    //    "must not bind strings that do not match format regex" in {
    //      val expectedError = FormError("value", invalidFormatKey, Seq(eoriTCUINRegex.toString))
    //      val result: Field = form.bind(Map(fieldName -> "123456")).apply(fieldName)
    //      result.errors must contain(expectedError)
    //    }

    "must capitalise first 2 letters on bound strings" in {
      val result = form.bind(Map(fieldName -> "gb12345"))
      result.errors mustEqual Nil
      result.get mustEqual "GB12345"
    }
  }
}
