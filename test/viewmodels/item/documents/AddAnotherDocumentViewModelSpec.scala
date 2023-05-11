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
import models.{Document, Index, Mode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.DocumentPage
import services.DocumentsService
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider

class AddAnotherDocumentViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  implicit private val mockDocumentsService: DocumentsService = mock[DocumentsService]

  "must get list items" - {

    "when there is one document added" in {
      forAll(arbitrary[Mode], arbitrary[Document]) {
        (mode, document) =>
          when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

          val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

          val result = new AddAnotherDocumentViewModelProvider().apply(userAnswers, mode, itemIndex, Nil)

          result.listItems.length mustBe 1
          result.title mustBe "You have attached 1 document to this item"
          result.heading mustBe "You have attached 1 document to this item"
          result.legend mustBe "Do you want to attach another document?"
          result.maxLimitLabel mustBe "You cannot attach any more documents. To attach another, you need to remove one first."
      }
    }

    "when there are multiple documents added" in {
      forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
        (mode, document1, document2) =>
          when(mockDocumentsService.getDocument(any(), any(), any()))
            .thenReturn(Some(document1))
            .thenReturn(Some(document2))

          val userAnswers = emptyUserAnswers
            .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
            .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

          val result = new AddAnotherDocumentViewModelProvider().apply(userAnswers, mode, itemIndex, Nil)
          result.listItems.length mustBe 2
          result.title mustBe s"You have attached 2 documents to this item"
          result.heading mustBe s"You have attached 2 documents to this item"
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
