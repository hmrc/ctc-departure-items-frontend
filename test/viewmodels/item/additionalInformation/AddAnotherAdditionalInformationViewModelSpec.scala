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

package viewmodels.item.additionalInformation

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.item.additionalInformation.AddAnotherAdditionalInformationViewModel.AddAnotherAdditionalInformationViewModelProvider

class AddAnotherAdditionalInformationViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one additional information added" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryAdditionalInformationAnswers(emptyUserAnswers, itemIndex, additionalInformationIndex).sample.value

          val result = new AddAnotherAdditionalInformationViewModelProvider()(userAnswers, mode, itemIndex)

          result.listItems.length mustEqual 1
          result.title mustEqual "You have added 1 additional information"
          result.heading mustEqual "You have added 1 additional information"
          result.legend mustEqual "Do you want to add any more additional information?"
          result.maxLimitLabel mustEqual "You cannot add any more additional information. To add another, you need to remove one first."
      }
    }

    "when there are multiple additional information added" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxAdditionalInformation)) {
        (mode, additionalInformationList) =>
          val userAnswers = (0 until additionalInformationList).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryAdditionalInformationAnswers(acc, itemIndex, Index(i)).sample.value
          }

          val result = new AddAnotherAdditionalInformationViewModelProvider()(userAnswers, mode, itemIndex)
          result.listItems.length mustEqual additionalInformationList
          result.title mustEqual s"You have added ${formatter.format(additionalInformationList)} additional information"
          result.heading mustEqual s"You have added ${formatter.format(additionalInformationList)} additional information"
          result.legend mustEqual "Do you want to add any more additional information?"
          result.maxLimitLabel mustEqual "You cannot add any more additional information. To add another, you need to remove one first."
      }
    }
  }
}
