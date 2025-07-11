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

package viewmodels

import base.SpecBase
import generators.Generators
import models.Index
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.AddAnotherItemViewModel.AddAnotherItemViewModelProvider

class AddAnotherItemViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one item" in {
      val userAnswers = arbitraryItemAnswers(emptyUserAnswers, index).sample.value

      val result = new AddAnotherItemViewModelProvider()(userAnswers)
      result.listItems.length mustEqual 1
      result.title mustEqual "You have added 1 item"
      result.heading mustEqual "You have added 1 item"
      result.legend mustEqual "Do you want to add another item?"
      result.maxLimitLabel mustEqual "You cannot add any more items. To add another, you need to remove one first."

    }

    "when there are multiple items" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(Gen.choose(2, frontendAppConfig.maxItems)) {
        numberOfItems =>
          val userAnswers = (0 until numberOfItems).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryItemAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherItemViewModelProvider()(userAnswers)
          result.listItems.length mustEqual numberOfItems
          result.title mustEqual s"You have added ${formatter.format(numberOfItems)} items"
          result.heading mustEqual s"You have added ${formatter.format(numberOfItems)} items"
          result.legend mustEqual "Do you want to add another item?"
          result.maxLimitLabel mustEqual "You cannot add any more items. To add another, you need to remove one first."
      }
    }
  }
}
