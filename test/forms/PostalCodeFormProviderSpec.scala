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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.AddressLine._
import models.reference.{Country, CountryCode}
import models.{AddressLine, CountryList}
import org.scalacheck.Gen
import play.api.data.FormError

class PostalCodeFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"

  private val form = new PostalCodeFormProvider()(prefix, countries)

  ".streetNumber" - {

    val fieldName = StreetNumber.field

    val invalidKey = s"$prefix.error.streetNumber.invalid"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(StreetNumber.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = StreetNumber.length,
      lengthError = FormError(fieldName, lengthKey, Seq(StreetNumber.arg, StreetNumber.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(StreetNumber.arg))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(StreetNumber.regex.regex)),
      length = StreetNumber.length
    )
  }

  ".postalCode" - {

    val fieldName = PostalCode.field

    val invalidKey = s"$prefix.error.postalCode.invalid"

    val invalidPostalOverLength: Gen[String] = stringsLongerThan(PostalCode.length + 1)

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(PostalCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = PostalCode.length,
      lengthError = FormError(fieldName, lengthKey, Seq(PostalCode.arg, PostalCode.length)),
      gen = invalidPostalOverLength
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(PostalCode.arg))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(PostalCode.regex.regex)),
      length = PostalCode.length
    )
  }

  ".country" - {

    import AddressLine.Country

    val fieldName = Country.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = nonEmptyString
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(Country.arg))
    )

    "not bind if country code does not exist in the country list" in {
      val result        = form.bind(Map(fieldName -> "foobar")).apply(fieldName)
      val expectedError = FormError(fieldName, requiredKey, Seq(Country.arg))
      result.errors must contain(expectedError)
    }

    "bind a country code which is in the list" in {
      val result = form.bind(Map(fieldName -> country.code.code)).apply(fieldName)
      result.value.value mustBe country.code.code
    }
  }
}
