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

package viewmodels

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.AddAnotherItemViewModel.AddAnotherItemViewModelProvider

class AddAnotherItemViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one item" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryItemAnswers(emptyUserAnswers, index).sample.value

          val result = new AddAnotherItemViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 item"
          result.heading mustBe "You have added 1 item"
          result.legend mustBe "Do you want to add another item?"
          result.maxLimitLabel mustBe "You cannot add any more items. To add another, you need to remove one first."
      }
    }

    "when there are multiple items" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxItems)) {
        (mode, numberOfItems) =>
          val userAnswers = (0 until numberOfItems).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryItemAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherItemViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe numberOfItems
          result.title mustBe s"You have added ${formatter.format(numberOfItems)} items"
          result.heading mustBe s"You have added ${formatter.format(numberOfItems)} items"
          result.legend mustBe "Do you want to add another item?"
          result.maxLimitLabel mustBe "You cannot add any more items. To add another, you need to remove one first."
      }
    }
  }
}
