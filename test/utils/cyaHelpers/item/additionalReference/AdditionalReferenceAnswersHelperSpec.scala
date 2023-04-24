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

package utils.cyaHelpers.item.additionalReference

import base.SpecBase
import controllers.item.additionalReference.index.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.AddAdditionalReferenceYesNoPage
import pages.item.additionalReference.index.{AddAdditionalReferenceNumberYesNoPage, AdditionalReferenceNumberPage, AdditionalReferencePage}
import viewmodels.ListItem

class AdditionalReferenceAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "AdditionalReferenceAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new AdditionalReferenceAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete additional references" in {
        forAll(arbitrary[Mode], Gen.alphaNumStr) {
          (mode, additionalReferenceNumber) =>
            val c651OrC658Document    = arbitraryC651OrC658AdditionalReference.arbitrary.sample.value
            val nonC658OrC658Document = arbitraryNonC651OrC658AdditionalReference.arbitrary.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)
              .setValue(AdditionalReferencePage(itemIndex, Index(0)), c651OrC658Document)
              .setValue(AdditionalReferenceNumberPage(itemIndex, Index(0)), additionalReferenceNumber)
              .setValue(AdditionalReferencePage(itemIndex, Index(1)), nonC658OrC658Document)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, Index(1)), true)
              .setValue(AdditionalReferenceNumberPage(itemIndex, Index(1)), additionalReferenceNumber)

            val helper = new AdditionalReferenceAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = s"Additional reference 1 - ${c651OrC658Document.toString}",
                  changeUrl = routes.AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemoveAdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              ),
              Right(
                ListItem(
                  name = s"Additional reference 2 - ${nonC658OrC658Document.toString}",
                  changeUrl = routes.AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                  removeUrl = Some(routes.RemoveAdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                )
              )
            )
        }
      }
    }
  }
}
