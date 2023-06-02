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

package models

import base.SpecBase
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import services.DocumentsService

class ItemLevelDocumentsSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with BeforeAndAfterEach {

  implicit private val mockDocumentsService: DocumentsService = mock[DocumentsService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDocumentsService)
  }

  "Item Level Documents" - {

    "must return counts of each document type at item level" - {

      "when there is a previous document" - {

        "and not providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitraryPreviousDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex)
              result.previous mustBe 1
              result.supporting mustBe 0
              result.transport mustBe 0
          }
        }

        "and providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitraryPreviousDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex, Some(documentIndex))
              result.previous mustBe 0
              result.supporting mustBe 0
              result.transport mustBe 0
          }
        }
      }

      "when there is a transport document" - {

        "and not providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitraryTransportDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex)
              result.previous mustBe 0
              result.supporting mustBe 0
              result.transport mustBe 1
          }
        }

        "and providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitraryTransportDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex, Some(documentIndex))
              result.previous mustBe 0
              result.supporting mustBe 0
              result.transport mustBe 0
          }
        }
      }

      "when there is a supporting document" - {

        "and not providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitrarySupportingDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex)
              result.previous mustBe 0
              result.supporting mustBe 1
              result.transport mustBe 0
          }
        }

        "and providing a document index" in {
          forAll(arbitrary[UserAnswers], arbitrary[Document](arbitrarySupportingDocument)) {
            (userAnswers, document) =>
              when(mockDocumentsService.numberOfDocuments(any(), any())).thenReturn(1)
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))

              val result = ItemLevelDocuments.apply(userAnswers, itemIndex, Some(documentIndex))
              result.previous mustBe 0
              result.supporting mustBe 0
              result.transport mustBe 0
          }
        }
      }
    }
  }

}
