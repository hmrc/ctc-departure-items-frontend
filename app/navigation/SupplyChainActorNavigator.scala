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

package navigation

import config.{FrontendAppConfig, PhaseConfig}
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.item.supplyChainActors.SupplyChainActorDomain
import models.{CheckMode, Index, Mode, NormalMode}

import javax.inject.{Inject, Singleton}

@Singleton
class SupplyChainActorNavigatorProviderImpl @Inject() (implicit config: FrontendAppConfig, phaseConfig: PhaseConfig) extends SupplyChainActorNavigatorProvider {

  override def apply(mode: Mode, itemIndex: Index, actorIndex: Index): UserAnswersNavigator =
    mode match {
      case NormalMode => new SupplyChainActorNavigator(mode, itemIndex, actorIndex)
      case CheckMode  => new ItemNavigator(mode, itemIndex)
    }
}

trait SupplyChainActorNavigatorProvider {
  def apply(mode: Mode, itemIndex: Index, actorIndex: Index): UserAnswersNavigator
}

class SupplyChainActorNavigator(override val mode: Mode, itemIndex: Index, actorIndex: Index)(implicit
  override val config: FrontendAppConfig,
  override val phaseConfig: PhaseConfig
) extends UserAnswersNavigator {

  override type T = SupplyChainActorDomain

  implicit override val reader: UserAnswersReader[SupplyChainActorDomain] =
    SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex).apply(Nil)
}
