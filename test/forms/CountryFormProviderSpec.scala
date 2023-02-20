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

import forms.behaviours.StringFieldBehaviours
import generators.Generators
import models.CountryList
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError

class CountryFormProviderSpec extends StringFieldBehaviours with Generators {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"

  private val country1    = arbitrary[Country].sample.value
  private val country2    = arbitrary[Country].sample.value
  private val countryList = CountryList(Seq(country1, country2))
  private val arg         = Gen.alphaNumStr.sample.value

  private val form = new CountryFormProvider()(prefix, countryList, arg)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(arg))
    )

    "not bind if country does not exist in the countryList" in {
      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a country which is in the list" in {
      val boundForm = form.bind(Map("value" -> country1.code.code))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
