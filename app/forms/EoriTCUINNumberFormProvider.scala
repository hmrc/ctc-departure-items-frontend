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

import forms.Constants.maxEoriNumberLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex._
import play.api.data.Form
import javax.inject.Inject

class EoriTCUINNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> eoriFormat(s"$prefix.error.required")
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(alphaNumericRegex, s"$prefix.error.invalidCharacters"),
            maxLength(maxEoriNumberLength, s"$prefix.error.maxLength")
            // minLength(minLengthCarrierEori, s"$prefix.error.minLength"), // TODO: Add back minlength and regexp once CTCP-5502 is in play
            // regexp(eoriTCUINRegex, s"$prefix.error.invalidFormat")
          )
        )
    )
}
