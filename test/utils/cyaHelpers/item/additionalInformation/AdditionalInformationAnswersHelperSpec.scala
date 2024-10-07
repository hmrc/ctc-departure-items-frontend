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

package utils.cyaHelpers.item.additionalInformation

import base.SpecBase
import controllers.item.additionalInformation.index.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.AddAdditionalInformationYesNoPage
import pages.item.additionalInformation.index.{AdditionalInformationPage, AdditionalInformationTypePage}
import viewmodels.ListItem

class AdditionalInformationAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AdditionalInformationAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new AdditionalInformationAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete additional information entries" in {
        forAll(arbitrary[Mode], Gen.alphaNumStr) {
          (mode, additionalInformation) =>
            val additionalInformationType30600    = arbitraryAdditionalInformation30600.arbitrary.sample.value
            val additionalInformationTypeNon30600 = arbitraryAdditionalInformationNon30600.arbitrary.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(AddAdditionalInformationYesNoPage(itemIndex), true)
              .setValue(AdditionalInformationTypePage(itemIndex, Index(0)), additionalInformationType30600)
              .setValue(AdditionalInformationPage(itemIndex, Index(0)), additionalInformation)
              .setValue(AdditionalInformationTypePage(itemIndex, Index(1)), additionalInformationTypeNon30600)
              .setValue(AdditionalInformationPage(itemIndex, Index(1)), additionalInformation)

            val helper = new AdditionalInformationAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"${additionalInformationType30600.toString} - $additionalInformation",
                  changeUrl = routes.AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemoveAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              ),
              Right(
                ListItem(
                  name = s"${additionalInformationTypeNon30600.toString} - $additionalInformation",
                  changeUrl = routes.AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                  removeUrl = Some(routes.RemoveAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                )
              )
            )
        }
      }

      "when user answers populated with in progress additional information entries" in {
        forAll(arbitrary[Mode]) {
          mode =>
            val additionalInformationType30600    = arbitraryAdditionalInformation30600.arbitrary.sample.value
            val additionalInformationTypeNon30600 = arbitraryAdditionalInformationNon30600.arbitrary.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(AddAdditionalInformationYesNoPage(itemIndex), true)
              .setValue(AdditionalInformationTypePage(itemIndex, Index(0)), additionalInformationType30600)
              .setValue(AdditionalInformationTypePage(itemIndex, Index(1)), additionalInformationTypeNon30600)

            val helper = new AdditionalInformationAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Left(
                ListItem(
                  name = s"${additionalInformationType30600.toString}",
                  changeUrl = routes.AdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemoveAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              ),
              Left(
                ListItem(
                  name = s"${additionalInformationTypeNon30600.toString}",
                  changeUrl = routes.AdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                  removeUrl = Some(routes.RemoveAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                )
              )
            )
        }
      }
    }
  }
}
