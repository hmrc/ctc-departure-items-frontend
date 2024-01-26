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

package models.journeyDomain.item.documents

import models.journeyDomain._
import models.{Index, RichJsArray, UserAnswers}
import pages.item.documents.DocumentsInProgressPage
import pages.sections.Section
import pages.sections.documents.DocumentsSection

case class DocumentsDomain(
  value: Seq[DocumentDomain]
)(itemIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[_]] = Some(DocumentsSection(itemIndex))
}

object DocumentsDomain {

  def userAnswersReader(itemIndex: Index): Read[DocumentsDomain] = {
    lazy val documentsReader: Read[Seq[DocumentDomain]] =
      DocumentsSection(itemIndex).arrayReader.to {
        case x if x.isEmpty =>
          DocumentDomain.userAnswersReader(itemIndex, Index(0)).toSeq
        case x =>
          x.traverse[DocumentDomain](DocumentDomain.userAnswersReader(itemIndex, _).apply(_))
      }

    DocumentsInProgressPage(itemIndex).reader.to {
      _ =>
        documentsReader.map(DocumentsDomain.apply(_)(itemIndex))
    }
  }
}
