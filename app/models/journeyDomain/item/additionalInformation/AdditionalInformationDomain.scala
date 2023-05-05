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

package models.journeyDomain.item.additionalInformation

import cats.implicits._
import controllers.item.additionalInformation.index.routes._
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.reference.AdditionalInformation
import models.{Index, Mode, UserAnswers}
import pages.item.additionalInformation.index._
import play.api.mvc.Call

case class AdditionalInformationDomain(
  `type`: AdditionalInformation,
  value: String
)(itemIndex: Index, additionalInformationIndex: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalInformationIndex)
      case CompletingJourney =>
        Call("GET", "#")
    }
  }
}

object AdditionalInformationDomain {

  def userAnswersReader(itemIndex: Index, additionalInformationIndex: Index): UserAnswersReader[AdditionalInformationDomain] = (
    AdditionalInformationTypePage(itemIndex, additionalInformationIndex).reader,
    AdditionalInformationPage(itemIndex, additionalInformationIndex).reader
  ).tupled.map((AdditionalInformationDomain.apply _).tupled).map(_(itemIndex, additionalInformationIndex))
}