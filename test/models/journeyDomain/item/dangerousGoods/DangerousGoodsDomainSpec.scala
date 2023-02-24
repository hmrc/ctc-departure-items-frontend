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

package models.journeyDomain.item.dangerousGoods

import base.SpecBase
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.item.dangerousGoods.index.UNNumberPage

class DangerousGoodsDomainSpec extends SpecBase {

  "Dangerous Goods Domain" - {

    "can be read from user answers" - {
      "when UNNumber page is answered" in {
        val uNNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), uNNumber)

        val expectedResult = DangerousGoodsDomain(uNNumber)(itemIndex, dangerousGoodsIndex)

        val result: EitherType[DangerousGoodsDomain] =
          UserAnswersReader[DangerousGoodsDomain](DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex)).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be read from user answers" - {
      "when UNNumber page is unanswered" in {
        val result: EitherType[DangerousGoodsDomain] =
          UserAnswersReader[DangerousGoodsDomain](DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe UNNumberPage(itemIndex, dangerousGoodsIndex)
      }
    }
  }

}
