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

package utils.cyaHelpers.item.documents

import base.SpecBase
import controllers.item.documents.index.routes
import generators.Generators
import models.{Document, Index, Mode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.DocumentPage
import pages.item.{AddDocumentsYesNoPage, InferredAddDocumentsYesNoPage}
import services.DocumentsService
import viewmodels.ListItem

class DocumentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  implicit private val mockDocumentsService: DocumentsService = mock[DocumentsService]

  private def buildHelper(userAnswers: UserAnswers, mode: Mode, index: Index): DocumentAnswersHelper =
    new DocumentAnswersHelper(mockDocumentsService)(userAnswers, mode, index)

  "DocumentAnswersHelper" - {

    "consignmentLevelListItems" - {
      "when no consignment level documents" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              when(mockDocumentsService.getConsignmentLevelDocuments(any()))
                .thenReturn(Nil)

              val helper = buildHelper(emptyUserAnswers, mode, itemIndex)
              helper.consignmentLevelListItems mustBe Nil
          }
        }
      }

      "when there are consignment level documents" in {
        forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
          (mode, document1, document2) =>
            when(mockDocumentsService.getConsignmentLevelDocuments(any()))
              .thenReturn(Seq(document1, document2))

            val helper = buildHelper(emptyUserAnswers, mode, itemIndex)
            helper.consignmentLevelListItems mustBe Seq(
              ListItem(
                name = document1.toString,
                changeUrl = None,
                removeUrl = None
              ),
              ListItem(
                name = document2.toString,
                changeUrl = None,
                removeUrl = None
              )
            )
        }
      }
    }

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete document" - {
        "and AddDocumentsYesNoPage is populated" in {
          forAll(arbitrary[Mode], arbitrary[Document]) {
            (mode, document) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(AddDocumentsYesNoPage(itemIndex), true)
                .setValue(DocumentPage(itemIndex, Index(0)), document.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                )
              )
          }
        }

        "and InferredAddDocumentsYesNoPage is populated" in {
          forAll(arbitrary[Mode], arbitrary[Document]) {
            (mode, document) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                .setValue(DocumentPage(itemIndex, Index(0)), document.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                )
              )
          }
        }

        "and neither are populated" in {
          forAll(arbitrary[Mode], arbitrary[Document]) {
            (mode, document) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(DocumentPage(itemIndex, Index(0)), document.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = None
                  )
                )
              )
          }
        }
      }

      "when user answers populated with complete documents" - {
        "and AddDocumentsYesNoPage is populated" in {
          forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
            (mode, document1, document2) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document1))
                .thenReturn(Some(document2))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(false)
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(AddDocumentsYesNoPage(itemIndex), true)
                .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
                .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document1.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = document2.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }

        "and InferredAddDocumentsYesNoPage is populated" in {
          forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
            (mode, document1, document2) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document1))
                .thenReturn(Some(document2))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(false)
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
                .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document1.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = document2.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }

        "and neither are populated" in {
          forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
            (mode, document1, document2) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document1))
                .thenReturn(Some(document2))

              val userAnswers = emptyUserAnswers
                .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
                .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document1.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = document2.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemoveDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }

        "and document is mandatory" in {
          forAll(arbitrary[Mode], arbitrary[Document], arbitrary[Document]) {
            (mode, document1, document2) =>
              when(mockDocumentsService.getDocument(any(), any(), any()))
                .thenReturn(Some(document1))
                .thenReturn(Some(document2))

              when(mockDocumentsService.isPreviousDocumentRequired(any(), any(), any()))
                .thenReturn(true)
                .thenReturn(false)

              val userAnswers = emptyUserAnswers
                .setValue(DocumentPage(itemIndex, Index(0)), document1.uuid)
                .setValue(DocumentPage(itemIndex, Index(1)), document2.uuid)

              val helper = buildHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = document1.toString,
                    changeUrl = routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = None
                  )
                ),
                Right(
                  ListItem(
                    name = document2.toString,
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

}
