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

package models.journeyDomain.item.documents

import models.journeyDomain.*
import models.{Document, Index, RichJsArray, UserAnswers}
import pages.item.documents.AddAnotherDocumentPage
import pages.sections.documents.DocumentsSection
import pages.sections.{external, Section}

case class DocumentsDomain(
  value: Seq[DocumentDomain]
)(itemIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[?]] = Some(DocumentsSection(itemIndex))
}

object DocumentsDomain {

  def userAnswersReader(itemIndex: Index): Read[DocumentsDomain] = {
    lazy val anyConsignmentLevelDocumentsReader: Read[Boolean] =
      external.DocumentsSection.arrayReader.to {
        documents => Read.apply(documents.validateAsListOf[Document].exists(_.attachToAllItems))
      }

    lazy val documentsReader: Read[Seq[DocumentDomain]] = (
      DocumentsSection(itemIndex).arrayReader,
      anyConsignmentLevelDocumentsReader
    ).to {
      case (x, true) if x.isEmpty =>
        Read.apply(Nil)
      case (x, false) if x.isEmpty =>
        DocumentDomain.userAnswersReader(itemIndex, Index(0)).toSeq
      case (x, _) =>
        x.traverse[DocumentDomain](DocumentDomain.userAnswersReader(itemIndex, _).apply(_))
    }

    (
      documentsReader,
      AddAnotherDocumentPage(itemIndex).reader
    ).map {
      (documents, _) => DocumentsDomain.apply(documents)(itemIndex)
    }
  }
}
