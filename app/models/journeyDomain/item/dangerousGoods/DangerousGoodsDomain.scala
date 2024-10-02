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

package models.journeyDomain.item.dangerousGoods

import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain._
import models.{Index, Mode, Phase, UserAnswers}
import pages.item.dangerousGoods.index.UNNumberPage
import play.api.mvc.Call

case class DangerousGoodsDomain(
  unNumber: String
)(itemIndex: Index, dangerousGoodsIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = unNumber

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage, phase: Phase): Option[Call] = stage match {
    case AccessingJourney =>
      Some(controllers.item.dangerousGoods.index.routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, dangerousGoodsIndex))

    case CompletingJourney if DangerousGoodsDomain.hasMultiplicity(phase) =>
      Some(controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(userAnswers.lrn, mode, itemIndex))

    case _ => Some(controllers.item.routes.GrossWeightController.onPageLoad(userAnswers.lrn, mode, itemIndex))
  }
}

object DangerousGoodsDomain {

  def hasMultiplicity(phase: Phase): Boolean = phase match {
    case Phase.PostTransition => true
    case Phase.Transition     => false
  }

  implicit def userAnswersReader(itemIndex: Index, dangerousGoodsIndex: Index): Read[DangerousGoodsDomain] =
    UNNumberPage(itemIndex, dangerousGoodsIndex).reader.map(DangerousGoodsDomain(_)(itemIndex, dangerousGoodsIndex))
}
