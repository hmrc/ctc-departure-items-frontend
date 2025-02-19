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

import forms.Constants.*
import forms.mappings.Mappings
import play.api.data.Form

import javax.inject.Inject

class BigDecimalFormProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[BigDecimal] =
    apply(prefix, decimalPlaces, characterCount)

  def apply(prefix: String, decimalPlaceCount: Int, characterCount: Int): Form[BigDecimal] =
    Form(
      "value" -> bigDecimal(
        decimalPlaceCount,
        characterCount,
        isZeroAllowed = true,
        s"$prefix.error",
        Seq(decimalPlaceCount, characterCount, characterCount + decimalPlaceCount + 1)
      )
    )
}
