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

import models.journeyDomain.{JourneyDomainModel, Stage}
import models.{Index, Mode, UserAnswers}
import pages.sections.supplyChainActors.SupplyChainActorsSection
import play.api.mvc.Call

case class SupplyChainActorsDomain(
  SupplyChainActorsDomain: Seq[SupplyChainActorDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.supplyChainActors.routes.AddAnotherSupplyChainActorController.onPageLoad(userAnswers.lrn, mode))
}

object SupplyChainActorsDomain {

  implicit val userAnswersReader: UserAnswersReader[SupplyChainActorsDomain] = {

    val actorsReader: UserAnswersReader[Seq[SupplyChainActorDomain]] =
      SupplyChainActorsSection.arrayReader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[SupplyChainActorDomain](
            SupplyChainActorDomain.userAnswersReader(Index(0))
          ).map(Seq(_))

        case x =>
          x.traverse[SupplyChainActorDomain](
            SupplyChainActorDomain.userAnswersReader
          ).map(_.toSeq)
      }

    UserAnswersReader[Seq[SupplyChainActorDomain]](actorsReader).map(SupplyChainActorsDomain(_))

  }
}
