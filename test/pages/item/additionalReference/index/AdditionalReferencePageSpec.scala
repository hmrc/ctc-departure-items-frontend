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

import models.reference.AdditionalReference
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AdditionalReferencePageSpec extends PageBehaviours {

  "AdditionalReferencePage" - {

    beRetrievable[AdditionalReference](AdditionalReferencePage(itemIndex, additionalReferenceIndex))

    beSettable[AdditionalReference](AdditionalReferencePage(itemIndex, additionalReferenceIndex))

    beRemovable[AdditionalReference](AdditionalReferencePage(itemIndex, additionalReferenceIndex))

    "when value changes" - {
      "must clean up subsequent pages" in {
        forAll(arbitrary[AdditionalReference]) {
          value =>
            forAll(arbitrary[AdditionalReference].retryUntil(_ != value), nonEmptyString) {
              (differentValue, referenceNumber) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), value)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
                  .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), referenceNumber)

                val result = userAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), differentValue)

                result.get(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex)) mustNot be(defined)
                result.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) mustNot be(defined)
            }
        }
      }
    }

    "when value has not changed" - {
      "must not clean up subsequent pages" in {
        forAll(arbitrary[AdditionalReference], nonEmptyString) {
          (value, referenceNumber) =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), value)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
              .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), referenceNumber)

            val result = userAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), value)

            result.get(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex)) mustBe defined
            result.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) mustBe defined
        }
      }
    }
  }
}
