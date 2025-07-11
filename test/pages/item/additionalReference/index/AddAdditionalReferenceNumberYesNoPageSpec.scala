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

package pages.item.additionalReference.index

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddAdditionalReferenceNumberYesNoPageSpec extends PageBehaviours {

  "AddAdditionalReferenceNumberYesNoPage" - {

    beRetrievable[Boolean](AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex))

    beSettable[Boolean](AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex))

    beRemovable[Boolean](AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove reference number" in {
          forAll(arbitrary[String]) {
            referenceNumber =>
              val userAnswers = emptyUserAnswers
                .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
                .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), referenceNumber)

              val result = userAnswers.setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), false)

              result.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            code =>
              val userAnswers = emptyUserAnswers
                .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
                .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), code)

              val result = userAnswers.setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)

              result.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) mustBe defined
          }
        }
      }
    }
  }
}
