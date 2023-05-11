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
import pages.sections.documents.{DocumentsSection => ItemDocumentsSection}
import pages.sections.external.DocumentsSection

import java.util.UUID
import javax.inject.Inject

class DocumentsService @Inject() () {

  def getDocuments(userAnswers: UserAnswers, itemIndex: Index): Option[SelectableList[Document]] =
    for {
      documents <- userAnswers.get(DocumentsSection).validate(SelectableList.documentsReads)
      itemDocumentUuids = userAnswers.get(ItemDocumentsSection(itemIndex)).validate(SelectableList.itemDocumentUuidsReads).getOrElse(Nil)
      filteredDocuments = documents.values.filterNot {
        x => itemDocumentUuids.contains(x.uuid)
      }
    } yield SelectableList(filteredDocuments)

  def getDocument(userAnswers: UserAnswers, uuid: UUID): Option[Document] =
    DocumentsService.getDocument(userAnswers, uuid)
}

object DocumentsService {

  def getDocument(userAnswers: UserAnswers, uuid: UUID): Option[Document] =
    userAnswers.get(DocumentsSection).validate(SelectableList.documentsReads).map(_.values).flatMap {
      _.find(_.uuid == uuid)
    }
}
