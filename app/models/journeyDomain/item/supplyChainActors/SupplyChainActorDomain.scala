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

package models.journeyDomain.item.supplyChainActors

import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.{Index, Mode, SupplyChainActorType, UserAnswers}
import pages.item.supplyChainActors.index.SupplyChainActorTypePage
import play.api.i18n.Messages
import play.api.mvc.Call
import cats.implicits._

case class SupplyChainActorDomain(
                                   role: SupplyChainActorType)(itemIndex: Index, actorIndex: Index) extends JourneyDomainModel {

  def asString(implicit messages: Messages): String =
    SupplyChainActorDomain.asString(role)

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

  def asString(role: SupplyChainActorType)(implicit messages: Messages): String =
    role.asString

  def userAnswersReader(itemIndex: Index, actorIndex: Index): UserAnswersReader[SupplyChainActorDomain] =
    SupplyChainActorTypePage(itemIndex, actorIndex).reader.map(SupplyChainActorDomain(_)(itemIndex, actorIndex))


}