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
import models.Mode
import models.journeyDomain.{ItemsDomain, UserAnswersReader}

import javax.inject.{Inject, Singleton}

@Singleton
class ItemsNavigatorProviderImpl @Inject() (implicit config: FrontendAppConfig, phaseConfig: PhaseConfig) extends ItemsNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    new ItemsNavigator(mode)
}

trait ItemsNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class ItemsNavigator(override val mode: Mode)(implicit override val config: FrontendAppConfig, override val phaseConfig: PhaseConfig)
    extends UserAnswersNavigator {

  override type T = ItemsDomain

  implicit override val reader: UserAnswersReader[ItemsDomain] =
    ItemsDomain.userAnswersReader
}
