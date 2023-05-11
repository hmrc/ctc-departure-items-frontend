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

package viewmodels.item.documents

import base.SpecBase
import generators.Generators
import helper.WritesHelper
import models.{Document, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.DocumentPage
import pages.sections.external.DocumentsSection
import play.api.libs.json.{JsArray, Json}
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider

class AddAnotherDocumentViewModelSpec extends SpecBase with WritesHelper with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one document added" in {
      forAll(arbitrary[Mode], arbitrary[Document]) {
        (mode, document) =>
          val userAnswers = emptyUserAnswers
            .setValue(DocumentsSection, JsArray(Seq(Json.toJson(document))))
            .setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

          val result = new AddAnotherDocumentViewModelProvider()(userAnswers, mode, itemIndex, Nil)

          result.listItems.length mustBe 1
          result.title mustBe "You have attached 1 document to this item"
          result.heading mustBe "You have attached 1 document to this item"
          result.legend mustBe "Do you want to attach another document?"
          result.maxLimitLabel mustBe "You cannot attach any more documents. To attach another, you need to remove one first."
      }
    }

    "when there are multiple documents added" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxDocuments)) {
        (mode, numberOfDocuments) =>
          val documents = (0 until numberOfDocuments).map {
            _ => arbitrary[Document].sample.value
          }
          val initialAnswers = emptyUserAnswers.setValue(DocumentsSection, JsArray(documents.map(Json.toJson(_))))
          val userAnswers = documents.zipWithIndex.foldLeft(initialAnswers) {
            case (acc, (document, i)) =>
              acc.setValue(DocumentPage(itemIndex, Index(i)), document.uuid)
          }

          val result = new AddAnotherDocumentViewModelProvider()(userAnswers, mode, itemIndex, Nil)
          result.listItems.length mustBe numberOfDocuments
          result.title mustBe s"You have attached ${formatter.format(numberOfDocuments)} documents to this item"
          result.heading mustBe s"You have attached ${formatter.format(numberOfDocuments)} documents to this item"
          result.legend mustBe "Do you want to attach another document?"
          result.maxLimitLabel mustBe "You cannot attach any more documents. To attach another, you need to remove one first."
      }
    }

    "when no documents available to attach to item" - {
      "flag must be false" in {
        forAll(arbitrary[AddAnotherDocumentViewModel].map(_.copy(documents = Nil))) {
          viewModel =>
            viewModel.canAttachMoreDocumentsToItem mustBe false
        }
      }
    }

    "when there are documents available to attach to item" - {
      "flag must be true" in {
        forAll(arbitrary[AddAnotherDocumentViewModel]) {
          viewModel =>
            viewModel.canAttachMoreDocumentsToItem mustBe true
        }
      }
    }
  }
}
