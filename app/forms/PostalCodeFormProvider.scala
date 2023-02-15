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

import forms.mappings.Mappings
import models.AddressLine.{Country, PostalCode, StreetNumber}
import models.{CountryList, PostalCodeAddress}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

class PostalCodeFormProvider @Inject() extends Mappings {

  def apply(prefix: String, countryList: CountryList)(implicit messages: Messages): Form[PostalCodeAddress] =
    Form(
      mapping(
        StreetNumber.field -> {
          trimmedText(s"$prefix.error.required", Seq(StreetNumber.arg))
            .verifying(
              StopOnFirstFail[String](
                maxLength(StreetNumber.length, s"$prefix.error.length", Seq(StreetNumber.arg, StreetNumber.length)),
                regexp(StreetNumber.regex, s"$prefix.error.streetNumber.invalid")
              )
            )
        },
        PostalCode.field -> {
          trimmedText(s"$prefix.error.required", Seq(PostalCode.arg))
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostalCode.length, s"$prefix.error.length", Seq(PostalCode.arg, PostalCode.length)),
                regexp(PostalCode.regex, s"$prefix.error.postalCode.invalid")
              )
            )
        },
        Country.field -> {
          country(countryList, s"$prefix.error.required", Seq(Country.arg))
        }
      )(PostalCodeAddress.apply)(PostalCodeAddress.unapply)
    )
}
