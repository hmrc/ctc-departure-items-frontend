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

package forms.items

import forms.Constants.maxItemDescriptionLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form

import javax.inject.Inject

class ItemDescriptionFormProvider @Inject() extends Mappings {

  def apply(prefix: String, args: Any*): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required", args = args.map(_.toString))
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(stringFieldRegex, s"$prefix.error.invalidCharacters"),
            maxLength(maxItemDescriptionLength, s"$prefix.error.length")
          )
        )
    )
}
