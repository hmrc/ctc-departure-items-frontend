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

import models.journeyDomain.Stage._
import models.journeyDomain._
import models.{Index, Mode, Phase, UserAnswers}
import pages.item.documents.index.DocumentPage
import play.api.mvc.Call

import java.util.UUID

case class DocumentDomain(
  document: UUID
)(itemIndex: Index, documentIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = document.toString

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage, phase: Phase): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.documents.index.routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex)
      case CompletingJourney =>
        controllers.item.documents.routes.AddAnotherDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object DocumentDomain {

  implicit def userAnswersReader(itemIndex: Index, documentIndex: Index): UserAnswersReader[DocumentDomain] =
    DocumentPage(itemIndex, documentIndex).reader.map(DocumentDomain(_)(itemIndex, documentIndex))
}
