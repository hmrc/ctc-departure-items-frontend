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
import models.journeyDomain.Stage.*
import models.{Index, Mode, UserAnswers}
import pages.item.documents.index.DocumentPage
import play.api.mvc.Call

import java.util.UUID

case class DocumentDomain(
  document: UUID
)(itemIndex: Index, documentIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = document.toString

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.documents.index.routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex)
      case CompletingJourney =>
        controllers.item.documents.routes.AddAnotherDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object DocumentDomain {

  implicit def userAnswersReader(itemIndex: Index, documentIndex: Index): Read[DocumentDomain] =
    DocumentPage(itemIndex, documentIndex).reader.map(DocumentDomain(_)(itemIndex, documentIndex))
}
