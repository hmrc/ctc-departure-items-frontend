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

package viewmodels.item.supplyChainActors

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.item.supplyChainActors.AddAnotherSupplyChainActorViewModel.AddAnotherSupplyChainActorViewModelProvider

class AddAnotherSupplyChainActorViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one supply chain actor" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitrarySupplyChainActorAnswers(emptyUserAnswers, itemIndex, actorIndex).sample.value

          val result = new AddAnotherSupplyChainActorViewModelProvider()(userAnswers, mode, itemIndex)

          result.listItems.length mustEqual 1
          result.title mustEqual "You have added 1 supply chain actor for this item"
          result.heading mustEqual "You have added 1 supply chain actor for this item"
          result.legend mustEqual "Do you want to add another supply chain actor for this item?"
          result.maxLimitLabel mustEqual "You cannot add any more supply chain actors. To add another, you need to remove one first."
      }
    }

    "when there are multiple supply chain actors" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxSupplyChainActors)) {
        (mode, supplyChainActors) =>
          val userAnswers = (0 until supplyChainActors).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitrarySupplyChainActorAnswers(acc, itemIndex, Index(i)).sample.value
          }

          val result = new AddAnotherSupplyChainActorViewModelProvider()(userAnswers, mode, itemIndex)
          result.listItems.length mustEqual supplyChainActors
          result.title mustEqual s"You have added ${formatter.format(supplyChainActors)} supply chain actors for this item"
          result.heading mustEqual s"You have added ${formatter.format(supplyChainActors)} supply chain actors for this item"
          result.legend mustEqual "Do you want to add another supply chain actor for this item?"
          result.maxLimitLabel mustEqual "You cannot add any more supply chain actors. To add another, you need to remove one first."
      }
    }
  }
}
