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
import models.Index
import org.scalacheck.Gen
import pages.item.dangerousGoods.index.{AddAnotherDangerousGoodsPage, UNNumberPage}

class DangerousGoodsListDomainSpec extends SpecBase {

  "Dangerous Goods List Domain" - {

    "can be read from user answers" - {
      "when there are dangerous goods added" in {
        val dangerousGoods1 = Gen.alphaNumStr.sample.value
        val dangerousGoods2 = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(UNNumberPage(itemIndex, Index(0)), dangerousGoods1)
          .setValue(UNNumberPage(itemIndex, Index(1)), dangerousGoods2)

        val expectedResult = DangerousGoodsListDomain(
          Seq(
            DangerousGoodsDomain(dangerousGoods1)(itemIndex, Index(0)),
            DangerousGoodsDomain(dangerousGoods2)(itemIndex, Index(1))
          )
        )(itemIndex)

        val result = DangerousGoodsListDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value mustEqual expectedResult
        result.value.pages.last mustEqual AddAnotherDangerousGoodsPage(itemIndex)
      }
    }

    "can not be read from user answers" - {
      "when there aren't any dangerous goods added" in {
        val result = DangerousGoodsListDomain.userAnswersReader(itemIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustEqual UNNumberPage(itemIndex, Index(0))
        result.left.value.pages mustEqual Seq(
          UNNumberPage(itemIndex, Index(0))
        )
      }
    }
  }
}
