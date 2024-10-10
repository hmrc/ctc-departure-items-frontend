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

package models.journeyDomain.item.additionalInformation

import controllers.item.additionalInformation.index.routes._
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain._
import models.reference.AdditionalInformation
import models.{Index, Mode, Phase, UserAnswers}
import pages.item.additionalInformation.index._
import play.api.mvc.Call

case class AdditionalInformationDomain(
  `type`: AdditionalInformation,
  value: String
)(itemIndex: Index, additionalInformationIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = s"${`type`} - $value"

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage, phase: Phase): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalInformationIndex)
      case CompletingJourney =>
        controllers.item.additionalInformation.routes.AddAnotherAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object AdditionalInformationDomain {

  def userAnswersReader(itemIndex: Index, additionalInformationIndex: Index): Read[AdditionalInformationDomain] = RichTuple2(
    (AdditionalInformationTypePage(itemIndex, additionalInformationIndex).reader, AdditionalInformationPage(itemIndex, additionalInformationIndex).reader)
  ).map(AdditionalInformationDomain.apply(_, _)(itemIndex, additionalInformationIndex))
}
