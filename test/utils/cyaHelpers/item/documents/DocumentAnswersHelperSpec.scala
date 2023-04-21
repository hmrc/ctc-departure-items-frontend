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

package utils.cyaHelpers.item.documents

import base.SpecBase
import controllers.item.documents.index.routes
import generators.Generators
import models.{Document, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.DocumentPage
import viewmodels.ListItem

class DocumentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "DocumentAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new DocumentAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete document" in {
        forAll(arbitrary[Mode], arbitrary[Document]) {
          (mode, document) =>
            val userAnswers = emptyUserAnswers
              .setValue(DocumentPage(itemIndex, Index(0)), document)
              .setValue(DocumentPage(itemIndex, Index(1)), document)

            val helper = new DocumentAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = document.toString,
                  changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              ),
              Right(
                ListItem(
                  name = document.toString,
                  changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                  removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                )
              )
            )
        }
      }
    }
  }

}