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
import models.{Index, RichJsArray, UserAnswers}
import pages.item.InferredAddDocumentsYesNoPage
import pages.item.documents.AddAnotherDocumentPage
import pages.sections.Section
import pages.sections.documents.DocumentsSection

case class DocumentsDomain(
  value: Seq[DocumentDomain]
)(itemIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[?]] = Some(DocumentsSection(itemIndex))
}

object DocumentsDomain {

  def userAnswersReader(itemIndex: Index): Read[DocumentsDomain] = {
    lazy val documentsReader: Read[Seq[DocumentDomain]] =
      (
        InferredAddDocumentsYesNoPage(itemIndex).optionalReader,
        DocumentsSection(itemIndex).arrayReader
      ).to {
        case (Some(true), x) if x.isEmpty =>
          Read.apply(Nil)
        case (_, x) if x.isEmpty =>
          DocumentDomain.userAnswersReader(itemIndex, Index(0)).toSeq
        case (_, x) =>
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
