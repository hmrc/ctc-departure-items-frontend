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

package forms.item.additionalReference

import config.PhaseConfig
import forms.mappings.Mappings
import models.Phase
import models.domain.StringFieldRegex.stringFieldRegexComma
import play.api.data.Form

import javax.inject.Inject

class AdditionalReferenceNumberFormProvider @Inject() (implicit phaseConfig: PhaseConfig) extends Mappings {

  def apply(prefix: String, otherAdditionalReferenceNumbers: Seq[String], isDocumentInCL234: Boolean, phase: Phase): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(stringFieldRegexComma, s"$prefix.error.invalidCharacters"),
            maxLength(phaseConfig.values.maxAdditionalReferenceNumLength, s"$prefix.error.length"),
            notInList(otherAdditionalReferenceNumbers, s"$prefix.error.unique"),
            cl234Constraint(isDocumentInCL234, phase, s"$prefix.error.cl234Constraint")
          )
        )
    )
}
