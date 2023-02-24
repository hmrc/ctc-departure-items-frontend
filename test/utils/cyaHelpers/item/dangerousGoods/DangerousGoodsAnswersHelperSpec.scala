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

package utils.cyaHelpers.item.dangerousGoods

import base.SpecBase
import controllers.item.dangerousGoods.index.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.AddDangerousGoodsYesNoPage
import pages.item.dangerousGoods.index.UNNumberPage
import viewmodels.ListItem

class DangerousGoodsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "DangerousGoodsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new DangerousGoodsAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete dangerous goods" - {
        "and add dangerous goods yes/no page is defined" - {
          "must return list items with remove links" ignore {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, uNNumber) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddDangerousGoodsYesNoPage(itemIndex), true)
                  .setValue(UNNumberPage(itemIndex, Index(0)), uNNumber)
                  .setValue(UNNumberPage(itemIndex, Index(1)), uNNumber)

                val helper = new DangerousGoodsAnswersHelper(userAnswers, mode, itemIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = uNNumber,
                      changeUrl = routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                      removeUrl = ??? //TODO: Add remove route and un-ignore test
                    )
                  ),
                  Right(
                    ListItem(
                      name = uNNumber,
                      changeUrl = routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                      removeUrl = ??? //TODO: Add remove route and un-ignore test
                    )
                  )
                )
            }
          }
        }

        "and add dangerous goods yes/no page is undefined" - {
          "must return list items with no remove link a index 0" ignore {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, uNNumber) =>
                val userAnswers = emptyUserAnswers
                  .setValue(UNNumberPage(itemIndex, Index(0)), uNNumber)
                  .setValue(UNNumberPage(itemIndex, Index(1)), uNNumber)

                val helper = new DangerousGoodsAnswersHelper(userAnswers, mode, itemIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = uNNumber,
                      changeUrl = routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                      removeUrl = None
                    )
                  ),
                  Right(
                    ListItem(
                      name = uNNumber,
                      changeUrl = routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                      removeUrl = ??? //TODO: Add remove route and un-ignore test
                    )
                  )
                )
            }
          }
        }
      }
    }
  }

}
