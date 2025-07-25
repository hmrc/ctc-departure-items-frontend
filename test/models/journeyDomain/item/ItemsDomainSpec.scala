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

package models.journeyDomain.item

import base.SpecBase
import generators.Generators
import models.Index
import models.journeyDomain.ItemsDomain
import org.scalacheck.Gen
import pages.item.AddAnotherItemPage

class ItemsDomainSpec extends SpecBase with Generators {

  "Items" - {

    "can be parsed from UserAnswers" in {

      val numberOfItems = Gen.choose(1, frontendAppConfig.maxItems).sample.value

      val userAnswers = (0 until numberOfItems).foldLeft(emptyUserAnswers) {
        case (updatedUserAnswers, index) =>
          arbitraryItemAnswers(updatedUserAnswers, Index(index)).sample.value
      }

      val result = ItemsDomain.userAnswersReader.run(userAnswers)

      result.value.value.item.length mustEqual numberOfItems
      result.value.pages.last mustEqual AddAnotherItemPage
    }
  }
}
