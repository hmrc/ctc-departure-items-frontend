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
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.item.dangerousGoods.index.UNNumberPage

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
        )

        val result: EitherType[DangerousGoodsListDomain] = UserAnswersReader[DangerousGoodsListDomain](
          DangerousGoodsListDomain.userAnswersReader(itemIndex)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be read from user answers" - {
      "when there aren't any dangerous goods added" in {
        val result: EitherType[DangerousGoodsListDomain] = UserAnswersReader[DangerousGoodsListDomain](
          DangerousGoodsListDomain.userAnswersReader(itemIndex)
        ).run(emptyUserAnswers)

        result.left.value.page mustBe UNNumberPage(itemIndex, Index(0))
      }
    }
  }

}
