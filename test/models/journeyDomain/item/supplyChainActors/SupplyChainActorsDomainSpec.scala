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

package models.journeyDomain.item.supplyChainActors

import base.SpecBase
import generators.Generators
import models.Index
import org.scalacheck.Gen
import pages.sections.supplyChainActors.SupplyChainActorsSection

class SupplyChainActorsDomainSpec extends SpecBase with Generators {

  "SupplyChainActorsDomain" - {

    "can be parsed from UserAnswers" in {

      val numberOfSupplyChainActors = Gen.choose(1, frontendAppConfig.maxSupplyChainActors).sample.value

      val userAnswers = (0 until numberOfSupplyChainActors).foldLeft(emptyUserAnswers) {
        case (updatedUserAnswers, index) =>
          arbitrarySupplyChainActorAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
      }

      val result = SupplyChainActorsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

      result.value.value.SupplyChainActorsDomain.length mustBe numberOfSupplyChainActors
      result.value.pages.last mustBe SupplyChainActorsSection(itemIndex)
    }
  }
}
