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

package models.journeyDomain.item.additionalReferences

import config.Constants._
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.reference.AdditionalReference
import models.{Index, Mode, UserAnswers}
import pages.item.additionalReference.index._
import play.api.mvc.Call

case class AdditionalReferenceDomain(
  `type`: AdditionalReference,
  number: Option[String]
)(itemIndex: Index, additionalReferenceIndex: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    super.routeIfCompleted(userAnswers, mode, stage) // TODO
}

object AdditionalReferenceDomain {

  def userAnswersReader(itemIndex: Index, additionalReferenceIndex: Index): UserAnswersReader[AdditionalReferenceDomain] =
    for {
      additionalReference <- AdditionalReferencePage(itemIndex, additionalReferenceIndex).reader
      additionalReferenceNumber <- additionalReference.value match {
        case C651 | C658 =>
          AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex).reader.map(Some(_))
        case _ =>
          AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex).filterOptionalDependent(identity) {
            AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex).reader
          }
      }
    } yield AdditionalReferenceDomain(additionalReference, additionalReferenceNumber)(itemIndex, additionalReferenceIndex)
}
