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

package utils.cyaHelpers.item.supplyChainActors

import config.{FrontendAppConfig, PhaseConfig}
import controllers.item.supplyChainActors.index.routes
import models.journeyDomain.item.supplyChainActors.SupplyChainActorDomain
import models.{Index, Mode, UserAnswers}
import pages.item.supplyChainActors.index.SupplyChainActorTypePage
import pages.sections.supplyChainActors.SupplyChainActorsSection
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class SupplyChainActorsAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit
  messages: Messages,
  config: FrontendAppConfig,
  phaseConfig: PhaseConfig
) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(SupplyChainActorsSection(itemIndex)) {
      actorIndex =>
        buildListItem[SupplyChainActorDomain](
          nameWhenComplete = _.asString,
          nameWhenInProgress = userAnswers.get(SupplyChainActorTypePage(itemIndex, actorIndex)).map(_.toString),
          removeRoute = Some(routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex, actorIndex))
        )(SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex))
    }

}
