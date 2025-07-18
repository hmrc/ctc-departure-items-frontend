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

package models.journeyDomain.item.supplyChainActors

import base.SpecBase
import generators.Generators
import models.Index
import models.reference.SupplyChainActorType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}

class SupplyChainActorDomainSpec extends SpecBase with Generators {

  "SupplyChainActorDomain" - {

    val role                 = arbitrary[SupplyChainActorType].sample.value
    val identificationNumber = Gen.alphaNumStr.sample.value

    "can be parsed from UserAnswers" - {

      "when at least one supply chain actor" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), role)
          .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)

        val expectedResult = SupplyChainActorDomain(
          role = role,
          identification = identificationNumber
        )(itemIndex, actorIndex)

        val result = SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex).apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          SupplyChainActorTypePage(itemIndex, actorIndex),
          IdentificationNumberPage(itemIndex, actorIndex)
        )
      }
    }

    "cannot be parsed from user answers" - {
      "when no supply chain actor type" in {
        val result = SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustEqual SupplyChainActorTypePage(itemIndex, Index(0))
        result.left.value.pages mustEqual Seq(
          SupplyChainActorTypePage(itemIndex, actorIndex)
        )
      }

      "when no supply chain actor id number" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), role)

        val result = SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustEqual IdentificationNumberPage(itemIndex, Index(0))
        result.left.value.pages mustEqual Seq(
          SupplyChainActorTypePage(itemIndex, actorIndex),
          IdentificationNumberPage(itemIndex, actorIndex)
        )
      }
    }
  }
}
