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

package services

import models.{Document, Index, RichOptionalJsArray, SelectableList, UserAnswers}
import pages.item.documents.index.DocumentPage
import pages.sections.documents.{DocumentsSection => ItemDocumentsSection}
import pages.sections.external.DocumentsSection
import play.api.libs.json.{JsArray, JsError, JsSuccess, Reads}
import services.DocumentsService._

import java.util.UUID
import javax.inject.Inject

class DocumentsService @Inject() () {

  def getDocuments(userAnswers: UserAnswers, itemIndex: Index): Option[SelectableList[Document]] =
    for {
      documents <- userAnswers.get(DocumentsSection).validate[Seq[Document]]
      itemDocumentUuids = userAnswers.get(ItemDocumentsSection(itemIndex)).validate[Seq[UUID]].getOrElse(Nil)
      filteredDocuments = documents.filterNot {
        document => itemDocumentUuids.contains(document.uuid)
      }
    } yield SelectableList(filteredDocuments)

  def getDocument(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Index): Option[Document] =
    for {
      uuid      <- userAnswers.get(DocumentPage(itemIndex, documentIndex))
      documents <- userAnswers.get(DocumentsSection).validate[Seq[Document]]
      document  <- documents.find(_.uuid == uuid)
    } yield document
}

object DocumentsService {

  implicit val documentsReads: Reads[Seq[Document]] = Reads[Seq[Document]] {
    case JsArray(values) =>
      JsSuccess(
        values.flatMap(_.validate[Document](Document.reads).asOpt).toSeq
      )
    case _ => JsError("DocumentsService::documentsReads: Failed to read documents from cache")
  }

  implicit val itemDocumentUuidsReads: Reads[Seq[UUID]] = Reads[Seq[UUID]] {
    case JsArray(values) =>
      JsSuccess(
        values.flatMap {
          value => (value \ "document").validate[UUID].asOpt
        }.toSeq
      )
    case _ => JsError("DocumentsService::itemDocumentUuidsReads: Failed to read document UUIDs from cache")
  }
}
