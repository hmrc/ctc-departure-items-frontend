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

package services

import config.Constants.DeclarationType.*
import models.*
import models.DocumentType.Previous
import pages.external.CustomsOfficeOfDepartureInCL112Page
import pages.item.DeclarationTypePage
import pages.item.documents.index.DocumentPage
import pages.sections.documents.DocumentsSection as ItemDocumentsSection
import pages.sections.external.DocumentsSection

import java.util.UUID
import javax.inject.Inject

class DocumentsService @Inject() {

  // these are the documents that were added in the documents section
  private def getDocuments(userAnswers: UserAnswers): Seq[Document] = {
    import models.Document.documentsReads
    userAnswers.get(DocumentsSection).validate[Seq[Document]].getOrElse(Nil)
  }

  // these are the documents that are available to add to this item
  def getDocuments(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Option[Index]): SelectableList[Document] = {
    import models.Document.itemDocumentsReads
    val documents         = getDocuments(userAnswers)
    val document          = documentIndex.flatMap(getDocument(userAnswers, itemIndex, _))
    val itemDocumentUuids = userAnswers.get(ItemDocumentsSection(itemIndex)).validate[Seq[UUID]].getOrElse(Nil)
    val filteredDocuments = documents
      .filter {
        x => !itemDocumentUuids.contains(x.uuid) || document.map(_.uuid).contains(x.uuid)
      }
      .filter {
        !_.attachToAllItems
      }
    SelectableList(filteredDocuments)
  }

  // these are the documents that have been added to this item
  // we ignore the current document index, if provided
  def getItemLevelDocuments(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Option[Index]): ItemLevelDocuments = {
    val numberOfDocuments = userAnswers.getArraySize(ItemDocumentsSection(itemIndex))
    val itemLevelDocuments = (0 until numberOfDocuments).map(Index(_)).foldLeft[Seq[Document]](Nil) {
      case (documents, index) if !documentIndex.contains(index) =>
        getDocument(userAnswers, itemIndex, index) match {
          case Some(document) => documents :+ document
          case None           => documents
        }
      case (documents, _) =>
        documents
    }
    ItemLevelDocuments(itemLevelDocuments)
  }

  // these are the documents that were added to all items in the documents section
  def getConsignmentLevelDocuments(userAnswers: UserAnswers): Seq[Document] =
    getDocuments(userAnswers).filter(_.attachToAllItems)

  def getDocument(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Index): Option[Document] =
    for {
      uuid <- userAnswers.get(DocumentPage(itemIndex, documentIndex))
      documents = getDocuments(userAnswers)
      document <- documents.find(_.uuid == uuid)
    } yield document

  def isConsignmentPreviousDocumentRequired(userAnswers: UserAnswers, itemIndex: Index): Boolean =
    (
      userAnswers.get(DeclarationTypePage(itemIndex)).map(_.code),
      userAnswers.get(CustomsOfficeOfDepartureInCL112Page),
      getDocuments(userAnswers),
      getItemLevelDocuments(userAnswers, itemIndex, None),
      getDocuments(userAnswers, itemIndex, None)
    ) match {
      case (Some(T2 | T2F), Some(true), documents, itemDocuments, availableDocuments) =>
        documents.noConsignmentPreviousDocumentPresent &&
        itemDocuments.noPreviousDocuments &&
        !availableDocuments.values.exists(_.`type` == Previous)
      case _ =>
        false
    }
}
