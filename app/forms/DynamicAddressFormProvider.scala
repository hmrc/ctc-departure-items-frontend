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
import models.AddressLine._
import models.DynamicAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import play.api.i18n.Messages

import javax.inject.Inject

class DynamicAddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, isPostalCodeRequired: Boolean, args: Any*)(implicit messages: Messages): Form[DynamicAddress] =
    Form(
      mapping(
        NumberAndStreet.field -> {
          text(s"$prefix.error.required", NumberAndStreet.arg +: args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(NumberAndStreet.length, s"$prefix.error.length", Seq(NumberAndStreet.arg) ++ args ++ Seq(NumberAndStreet.length)),
                regexp(NumberAndStreet.regex, s"$prefix.error.invalid", NumberAndStreet.arg +: args)
              )
            )
        },
        City.field -> {
          text(s"$prefix.error.required", City.arg +: args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(City.length, s"$prefix.error.length", Seq(City.arg) ++ args ++ Seq(City.length)),
                regexp(City.regex, s"$prefix.error.invalid", City.arg +: args)
              )
            )
        },
        PostalCode.field -> {
          val constraint = StopOnFirstFail[String](
            maxLength(PostalCode.length, s"$prefix.error.length", Seq(PostalCode.arg) ++ args ++ Seq(PostalCode.length)),
            regexp(PostalCode.regex, s"$prefix.error.postalCode.invalid", args)
          )
          if (isPostalCodeRequired) {
            text(s"$prefix.error.required", PostalCode.arg +: args)
              .verifying(constraint)
              .transform[Option[String]](Some(_), _.getOrElse(""))
          } else {
            optional(
              text()
                .verifying(constraint)
            )
          }
        }
      )(DynamicAddress.apply)(DynamicAddress.unapply)
    )
}

object DynamicAddressFormProvider {

  def apply(prefix: String, isPostalCodeRequired: Boolean, args: Any*)(implicit messages: Messages): Form[DynamicAddress] =
    new DynamicAddressFormProvider()(prefix, isPostalCodeRequired, args: _*)
}
