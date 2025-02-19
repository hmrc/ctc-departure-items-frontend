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

package models.journeyDomain.item.dangerousGoods

import models.journeyDomain.*
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.{Index, Mode, UserAnswers}
import pages.item.dangerousGoods.index.UNNumberPage
import play.api.mvc.Call

case class DangerousGoodsDomain(
  unNumber: String
)(itemIndex: Index, dangerousGoodsIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = unNumber

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = stage match {
    case AccessingJourney =>
      Some(controllers.item.dangerousGoods.index.routes.UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, dangerousGoodsIndex))

    case CompletingJourney =>
      Some(controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(userAnswers.lrn, mode, itemIndex))
  }
}

object DangerousGoodsDomain {

  implicit def userAnswersReader(itemIndex: Index, dangerousGoodsIndex: Index): Read[DangerousGoodsDomain] =
    UNNumberPage(itemIndex, dangerousGoodsIndex).reader.map(DangerousGoodsDomain(_)(itemIndex, dangerousGoodsIndex))
}
