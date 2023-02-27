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

package models.journeyDomain.item

import base.SpecBase
import models.DeclarationType
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.external.TransitOperationDeclarationTypePage
import pages.item.{DeclarationTypePage, DescriptionPage}

class ItemDomainSpec extends SpecBase {

  "Item Domain" - {

    "can be read from user answers" - {}

    "can not be read from user answers" - {
      "when item description page is unanswered" in {
        val result: EitherType[ItemDomain] =
          UserAnswersReader[ItemDomain](ItemDomain.userAnswersReader(itemIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe DescriptionPage(itemIndex)
      }

      "when transit operation declaration type is T" - {
        "and declaration type is unanswered" in {
          val itemDescription = Gen.alphaNumStr.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(TransitOperationDeclarationTypePage, DeclarationType.T)
            .setValue(DescriptionPage(itemIndex), itemDescription)

          val result: EitherType[ItemDomain] =
            UserAnswersReader[ItemDomain](ItemDomain.userAnswersReader(itemIndex)).run(userAnswers)

          result.left.value.page mustBe DeclarationTypePage(itemIndex)

        }
      }
    }
  }

}
