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

package viewmodels.item.additionalReference

import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.reference.AdditionalReference
import models.{Index, UserAnswers}

import javax.inject.Inject

case class AdditionalReferenceNumberViewModel(
  otherAdditionalReferenceNumbers: Seq[String],
  isReferenceNumberRequired: Boolean
)

object AdditionalReferenceNumberViewModel {

  class AdditionalReferenceNumberViewModelProvider @Inject() () {

    def apply(
      userAnswers: UserAnswers,
      itemIndex: Index,
      additionalReferenceIndex: Index,
      additionalReference: AdditionalReference
    ): AdditionalReferenceNumberViewModel = {
      val otherAdditionalReferenceNumbers = AdditionalReferenceDomain
        .otherAdditionalReferenceNumbers(itemIndex, additionalReferenceIndex, additionalReference)
        .run(userAnswers)
        .getOrElse(Nil)

      AdditionalReferenceNumberViewModel(
        otherAdditionalReferenceNumbers = otherAdditionalReferenceNumbers.flatten,
        isReferenceNumberRequired = AdditionalReferenceDomain.isReferenceNumberRequired(otherAdditionalReferenceNumbers)
      )
    }
  }
}
