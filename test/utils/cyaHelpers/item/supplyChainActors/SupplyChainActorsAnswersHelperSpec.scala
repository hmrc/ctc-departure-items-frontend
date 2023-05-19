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

package utils.cyaHelpers.item.supplyChainActors

import base.SpecBase
import generators.Generators
import controllers.item.supplyChainActors.index.routes
import models.{Index, Mode, SupplyChainActorType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.AddSupplyChainActorYesNoPage
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import viewmodels.ListItem

class SupplyChainActorsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "SupplyChainActorsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete supply chain actors" - {
        "must return list items with remove links" in {
          forAll(arbitrary[Mode], arbitrary[SupplyChainActorType], Gen.alphaNumStr) {
            (mode, actorRole, actorId) =>
              val userAnswers = emptyUserAnswers
                .setValue(AddSupplyChainActorYesNoPage(itemIndex), true)
                .setValue(SupplyChainActorTypePage(itemIndex, Index(0)), actorRole)
                .setValue(IdentificationNumberPage(itemIndex, Index(0)), actorId)
                .setValue(SupplyChainActorTypePage(itemIndex, Index(1)), actorRole)
                .setValue(IdentificationNumberPage(itemIndex, Index(1)), actorId)

              val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"${actorRole.asString} - $actorId",
                    changeUrl = routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = s"${actorRole.asString} - $actorId",
                    changeUrl = routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }
      }

      "when user answers populated with an in progress supply chain actor" in {

        forAll(arbitrary[Mode], arbitrary[SupplyChainActorType]) {
          (mode, actorRole) =>
            val userAnswers = emptyUserAnswers
              .setValue(AddSupplyChainActorYesNoPage(itemIndex), true)
              .setValue(SupplyChainActorTypePage(itemIndex, Index(0)), actorRole)

            val helper = new SupplyChainActorsAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Left(
                ListItem(
                  name = s"${actorRole.asString}",
                  changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              )
            )
        }
      }
    }
  }

}
