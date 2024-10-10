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

package viewmodels.item.dangerousGoods

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewmodels.item.dangerousGoods.AddAnotherDangerousGoodsViewModel.AddAnotherDangerousGoodsViewModelProvider

class AddAnotherDangerousGoodsViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one dangerous goods added" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryDangerousGoodsAnswers(emptyUserAnswers, itemIndex, dangerousGoodsIndex).sample.value

          val result = new AddAnotherDangerousGoodsViewModelProvider()(userAnswers, mode, itemIndex)

          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 UN number for dangerous goods"
          result.heading mustBe "You have added 1 UN number for dangerous goods"
          result.legend mustBe "Do you want to add another UN number?"
          result.maxLimitLabel mustBe "You cannot add any more UN numbers for dangerous goods. To add another, you need to remove one first."
      }
    }

    "when there are multiple dangerous goods added" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxDangerousGoods)) {
        (mode, dangerousGoods) =>
          val userAnswers = (0 until dangerousGoods).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryDangerousGoodsAnswers(acc, itemIndex, Index(i)).sample.value
          }

          val result = new AddAnotherDangerousGoodsViewModelProvider()(userAnswers, mode, itemIndex)
          result.listItems.length mustBe dangerousGoods
          result.title mustBe s"You have added ${formatter.format(dangerousGoods)} UN numbers for dangerous goods"
          result.heading mustBe s"You have added ${formatter.format(dangerousGoods)} UN numbers for dangerous goods"
          result.legend mustBe "Do you want to add another UN number?"
          result.maxLimitLabel mustBe "You cannot add any more UN numbers for dangerous goods. To add another, you need to remove one first."
      }
    }
  }
}
