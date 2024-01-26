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

package models.journeyDomain

import config.PhaseConfig
import models.journeyDomain.item.ItemDomain
import models.{Index, RichJsArray, UserAnswers}
import pages.sections.{ItemsSection, Section}

case class ItemsDomain(item: Seq[ItemDomain]) extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[_]] = Some(ItemsSection)
}

object ItemsDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): UserAnswersReader[ItemsDomain] = {

    val itemsReader: Read[Seq[ItemDomain]] =
      ItemsSection.arrayReader.to {
        case x if x.isEmpty =>
          ItemDomain.userAnswersReader(Index(0)).toSeq
        case x =>
          x.traverse[ItemDomain](ItemDomain.userAnswersReader(_).apply(_))
      }

    itemsReader.map(ItemsDomain.apply).apply(Nil)
  }
}
