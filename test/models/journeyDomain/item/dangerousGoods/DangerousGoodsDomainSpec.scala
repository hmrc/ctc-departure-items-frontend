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

package models.journeyDomain.item.dangerousGoods

import base.SpecBase
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

        val result = DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex).apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages mustEqual Seq(
          UNNumberPage(itemIndex, dangerousGoodsIndex)
        )
      }
    }

    "can not be read from user answers" - {
      "when UNNumber page is unanswered" in {
        val result = DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustEqual UNNumberPage(itemIndex, dangerousGoodsIndex)
        result.left.value.pages mustEqual Seq(
          UNNumberPage(itemIndex, dangerousGoodsIndex)
        )
      }
    }
  }
}
