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

import forms.behaviours.StringFieldBehaviours
import models.SelectableList
import play.api.data.FormError
import generators.Generators
import org.scalacheck.Gen

class SelectableFormProviderSpec extends StringFieldBehaviours with Generators {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = SelectableList(Seq(country1, country2))

  private class FakeFormProvider extends SelectableFormProvider {
    override val field: String = "value"
  }

  private val formProvider = new FakeFormProvider()
  private val form         = formProvider.apply(prefix, countryList)

  ".value" - {

    val fieldName = formProvider.field

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "not bind if value does not exist in the list" in {
      val boundForm = form.bind(Map(fieldName -> "foobar"))
      val field     = boundForm(fieldName)
      field.errors mustNot be(empty)
    }

    "bind a value which is in the list" in {
      val boundForm = form.bind(Map(fieldName -> country1.value))
      val field     = boundForm(fieldName)
      field.errors must be(empty)
    }
  }
}
