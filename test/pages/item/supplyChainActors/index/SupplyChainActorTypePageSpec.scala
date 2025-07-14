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

package pages.item.supplyChainActors.index

import models.reference.SupplyChainActorType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class SupplyChainActorTypePageSpec extends PageBehaviours {

  "SupplyChainActorTypePage" - {

    beRetrievable[SupplyChainActorType](SupplyChainActorTypePage(itemIndex, actorIndex))

    beSettable[SupplyChainActorType](SupplyChainActorTypePage(itemIndex, actorIndex))

    beRemovable[SupplyChainActorType](SupplyChainActorTypePage(itemIndex, actorIndex))

    "cleanup" - {
      val identificationNumber = Gen.alphaNumStr.sample.value

      "when value changes" - {
        "must clean up identification number page" in {
          forAll(arbitrary[SupplyChainActorType]) {
            value =>
              forAll(arbitrary[SupplyChainActorType].retryUntil(_ != value)) {
                differentValue =>
                  val userAnswers = emptyUserAnswers
                    .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), value)
                    .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)

                  val result = userAnswers.setValue(SupplyChainActorTypePage(itemIndex, actorIndex), differentValue)

                  result.get(IdentificationNumberPage(itemIndex, actorIndex)) mustNot be(defined)
              }
          }
        }
      }

      "when value has not changed" - {
        "must not clean up identification number page" in {
          forAll(arbitrary[SupplyChainActorType]) {
            value =>
              val userAnswers = emptyUserAnswers
                .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), value)
                .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)

              val result = userAnswers.setValue(SupplyChainActorTypePage(itemIndex, actorIndex), value)

              result.get(IdentificationNumberPage(itemIndex, actorIndex)) mustBe defined
          }
        }
      }
    }
  }
}
