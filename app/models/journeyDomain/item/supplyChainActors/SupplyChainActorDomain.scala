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

package models.journeyDomain.item.supplyChainActors

import models.journeyDomain.*
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.reference.SupplyChainActorType
import models.{Index, Mode, UserAnswers}
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import play.api.mvc.Call

case class SupplyChainActorDomain(role: SupplyChainActorType, identification: String)(itemIndex: Index, actorIndex: Index) extends JourneyDomainModel {

  def asString: String = s"${role.toString} - $identification"

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.supplyChainActors.index.routes.SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, actorIndex)
      case CompletingJourney =>
        controllers.item.supplyChainActors.routes.AddAnotherSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object SupplyChainActorDomain {

  def userAnswersReader(itemIndex: Index, actorIndex: Index): Read[SupplyChainActorDomain] =
    RichTuple2(
      (SupplyChainActorTypePage(itemIndex, actorIndex).reader, IdentificationNumberPage(itemIndex, actorIndex).reader)
    ).map(SupplyChainActorDomain.apply(_, _)(itemIndex, actorIndex))

}
