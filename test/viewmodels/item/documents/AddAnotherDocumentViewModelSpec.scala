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

package viewmodels.item.documents

import base.SpecBase
import generators.Generators
import models.{Document, Index, ItemLevelDocuments, Mode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.DocumentPage
import services.DocumentsService
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider

class AddAnotherDocumentViewModelSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  implicit private val mockDocumentsService: DocumentsService = mock[DocumentsService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDocumentsService)
  }

  "must get list items" - {

    "when there is one document added" - {
      "at item level" in {
        forAll(arbitrary[Mode], arbitrary[Document]) {
          (mode, document) =>
            when(mockDocumentsService.getDocument(any(), any(), any()))
              .thenReturn(Some(document))

            when(mockDocumentsService.getConsignmentLevelDocuments(any()))
              .thenReturn(Nil)

            when(mockDocumentsService.getItemLevelDocuments(any(), any(), any()))
              .thenReturn(ItemLevelDocuments(Seq(document)))

            val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

            val viewModelProvider = new AddAnotherDocumentViewModelProvider(mockDocumentsService)
            val result            = viewModelProvider.apply(userAnswers, mode, itemIndex)

            result.listItems.length mustEqual 1
            result.consignmentLevelDocumentsListItems.length mustEqual 0
            result.title mustEqual "You have attached 1 document to this item"
            result.heading mustEqual "You have attached 1 document to this item"
            result.legend mustEqual "Do you want to attach another document?"
            result.maxLimitLabel mustEqual "You cannot attach any more documents. To attach another, you need to remove one first."
        }
      }

      "at consignment level" in {
        forAll(arbitrary[Mode], arbitrary[Document]) {
          (mode, document) =>
            when(mockDocumentsService.getDocument(any(), any(), any()))
              .thenReturn(None)

            when(mockDocumentsService.getConsignmentLevelDocuments(any()))
              .thenReturn(Seq(document))

            when(mockDocumentsService.getItemLevelDocuments(any(), any(), any()))
              .thenReturn(ItemLevelDocuments(Nil))

            val viewModelProvider = new AddAnotherDocumentViewModelProvider(mockDocumentsService)
            val result            = viewModelProvider.apply(emptyUserAnswers, mode, itemIndex)

            result.consignmentLevelDocumentsListItems.length mustEqual 1
            result.listItems.length mustEqual 0
            result.title mustEqual "You have attached 1 document to this item"
            result.heading mustEqual "You have attached 1 document to this item"
            result.legend mustEqual "Do you want to attach another document?"
            result.maxLimitLabel mustEqual "You cannot attach any more documents. To attach another, you need to remove one first."
        }
      }

    }

    "when there are multiple documents added" in {
      forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document], arbitrary[Document]) {
        (mode, document1, document2, document3) =>
          when(mockDocumentsService.getDocument(any(), any(), any()))
            .thenReturn(Some(document1))
            .thenReturn(Some(document2))

          when(mockDocumentsService.getConsignmentLevelDocuments(any()))
            .thenReturn(Seq(document3))

          when(mockDocumentsService.getItemLevelDocuments(any(), any(), any()))
            .thenReturn(ItemLevelDocuments(Seq(document1, document2)))

          val userAnswers = emptyUserAnswers
            .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
            .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

          val viewModelProvider = new AddAnotherDocumentViewModelProvider(mockDocumentsService)
          val result            = viewModelProvider.apply(userAnswers, mode, itemIndex)

          result.listItems.length mustEqual 2
          result.consignmentLevelDocumentsListItems.length mustEqual 1
          result.title mustEqual s"You have attached 3 documents to this item"
          result.heading mustEqual s"You have attached 3 documents to this item"
          result.legend mustEqual "Do you want to attach another document?"
          result.maxLimitLabel mustEqual "You cannot attach any more documents. To attach another, you need to remove one first."
      }
    }

    "when consignment level document present and new documents to attach have been added" - {
      "nextIndex value must be read from item level documents" in {
        forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document], arbitrary[Document]) {
          (mode, document1, document2, document3) =>
            when(mockDocumentsService.getDocument(any(), any(), any()))
              .thenReturn(Some(document1))
              .thenReturn(Some(document2))

            when(mockDocumentsService.getConsignmentLevelDocuments(any()))
              .thenReturn(Seq(document3))

            when(mockDocumentsService.getItemLevelDocuments(any(), any(), any()))
              .thenReturn(ItemLevelDocuments(Seq(document1, document2)))

            val userAnswers = emptyUserAnswers
              .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
              .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

            val viewModelProvider = new AddAnotherDocumentViewModelProvider(mockDocumentsService)
            val result            = viewModelProvider.apply(userAnswers, mode, itemIndex)

            result.listItems.length mustEqual 2
            result.consignmentLevelDocumentsListItems.length mustEqual 1
            result.nextIndex mustEqual Index(2)
        }
      }
    }
  }
}
