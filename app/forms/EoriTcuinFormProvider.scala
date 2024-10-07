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

import forms.Constants.{maxEoriNumberLength, minEoriTcuinLength}
import forms.mappings.Mappings
import models.RichString
import models.domain.StringFieldRegex._
import play.api.data.Form
import play.api.data.validation.Constraints.minLength

import javax.inject.Inject

class EoriTcuinFormProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> adaptedText(s"$prefix.error.required")(_.removeSpaces().capitalise(2))
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(alphaNumericRegex, s"$prefix.error.invalidCharacters"),
            maxLength(maxEoriNumberLength, s"$prefix.error.maxLength"),
            minLength(minEoriTcuinLength, s"$prefix.error.minLength"),
            regexp(eoriTcuinRegex, s"$prefix.error.invalidFormat")
          )
        )
    )
}
