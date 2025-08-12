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

package viewmodels.item.additionalReference

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.item.additionalReference.AddAnotherAdditionalReferenceViewModel.AddAnotherAdditionalReferenceViewModelProvider

class AddAnotherAdditionalReferenceViewModelSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one additional reference added" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryAdditionalReferenceAnswers(emptyUserAnswers, itemIndex, additionalReferenceIndex).sample.value

          val result = new AddAnotherAdditionalReferenceViewModelProvider()(userAnswers, mode, itemIndex)

          result.listItems.length mustEqual 1
          result.title mustEqual "You have added 1 additional reference"
          result.heading mustEqual "You have added 1 additional reference"
          result.legend mustEqual "Do you want to add another additional reference?"
          result.maxLimitLabel mustEqual "You cannot add any more additional references. To add another, you need to remove one first."
      }
    }

    "when there are multiple additional references added" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxAdditionalReferences)) {
        (mode, additionalReferences) =>
          val userAnswers = (0 until additionalReferences).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryAdditionalReferenceAnswers(acc, itemIndex, Index(i)).sample.value
          }

          val result = new AddAnotherAdditionalReferenceViewModelProvider()(userAnswers, mode, itemIndex)
          result.listItems.length mustEqual additionalReferences
          result.title mustEqual s"You have added ${formatter.format(additionalReferences)} additional references"
          result.heading mustEqual s"You have added ${formatter.format(additionalReferences)} additional references"
          result.legend mustEqual "Do you want to add another additional reference?"
          result.maxLimitLabel mustEqual "You cannot add any more additional references. To add another, you need to remove one first."
      }
    }
  }
}
