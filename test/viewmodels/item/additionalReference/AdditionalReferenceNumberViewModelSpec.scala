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

import base.SpecBase
import generators.Generators
import models.Index
import models.reference.AdditionalReference
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.additionalReference.index._
import viewmodels.item.additionalReference.AdditionalReferenceNumberViewModel.AdditionalReferenceNumberViewModelProvider

class AdditionalReferenceNumberViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AdditionalReferenceNumberViewModel" - {

    "must return view model" - {

      "when no other additional reference numbers for the given type" in {
        forAll(arbitrary[AdditionalReference]) {
          additionalReference =>
            forAll(arbitrary[AdditionalReference].retryUntil(_ != additionalReference)) {
              otherAdditionalReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(AdditionalReferencePage(itemIndex, Index(0)), otherAdditionalReference)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(0)), true)
                  .setValue(AdditionalReferenceNumberPage(itemIndex, Index(0)), nonEmptyString.sample.value)
                  .setValue(AdditionalReferencePage(itemIndex, Index(1)), additionalReference)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(1)), true)

                val result = new AdditionalReferenceNumberViewModelProvider().apply(userAnswers, itemIndex, Index(1), additionalReference)

                result.otherAdditionalReferenceNumbers mustBe empty
            }
        }
      }

      "when there are additional reference numbers for the given type" in {
        forAll(arbitrary[AdditionalReference], nonEmptyString) {
          (additionalReference, additionalReferenceNumber) =>
            forAll(arbitrary[AdditionalReference].retryUntil(_ != additionalReference)) {
              otherAdditionalReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(AdditionalReferencePage(itemIndex, Index(0)), otherAdditionalReference)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(0)), true)
                  .setValue(AdditionalReferenceNumberPage(itemIndex, Index(0)), nonEmptyString.sample.value)
                  .setValue(AdditionalReferencePage(itemIndex, Index(1)), additionalReference)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(1)), true)
                  .setValue(AdditionalReferenceNumberPage(itemIndex, Index(1)), additionalReferenceNumber)
                  .setValue(AdditionalReferencePage(itemIndex, Index(2)), additionalReference)
                  .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(2)), true)

                val result = new AdditionalReferenceNumberViewModelProvider().apply(userAnswers, itemIndex, Index(2), additionalReference)

                result.otherAdditionalReferenceNumbers mustBe Seq(additionalReferenceNumber)
            }
        }
      }

      "when reference number is required" in {
        forAll(arbitrary[AdditionalReference]) {
          additionalReference =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalReferencePage(itemIndex, Index(0)), additionalReference)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(0)), false)
              .setValue(AdditionalReferencePage(itemIndex, Index(1)), additionalReference)

            val result = new AdditionalReferenceNumberViewModelProvider().apply(userAnswers, itemIndex, Index(1), additionalReference)

            result.isReferenceNumberRequired mustBe true
        }
      }

      "when reference number is not required" in {
        forAll(arbitrary[AdditionalReference]) {
          additionalReference =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalReferencePage(itemIndex, Index(0)), additionalReference)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(0)), true)
              .setValue(AdditionalReferenceNumberPage(itemIndex, Index(0)), nonEmptyString.sample.value)
              .setValue(AdditionalReferencePage(itemIndex, Index(1)), additionalReference)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(1)), true)

            val result = new AdditionalReferenceNumberViewModelProvider().apply(userAnswers, itemIndex, Index(1), additionalReference)

            result.isReferenceNumberRequired mustBe false
        }
      }
    }
  }
}
