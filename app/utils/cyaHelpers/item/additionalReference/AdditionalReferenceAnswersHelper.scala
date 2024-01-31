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

package utils.cyaHelpers.item.additionalReference

import config.{FrontendAppConfig, PhaseConfig}
import controllers.item.additionalReference.index.routes
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.{Index, Mode, UserAnswers}
import pages.item.additionalReference.index.AdditionalReferencePage
import pages.sections.additionalReference.AdditionalReferencesSection
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class AdditionalReferenceAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit
  messages: Messages,
  config: FrontendAppConfig,
  phaseConfig: PhaseConfig
) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(AdditionalReferencesSection(itemIndex)) {
      additionalReferenceIndex =>
        buildListItem[AdditionalReferenceDomain](
          nameWhenComplete = _.toString,
          nameWhenInProgress = userAnswers.get(AdditionalReferencePage(itemIndex, additionalReferenceIndex)).map(_.toString),
          removeRoute = Some(routes.RemoveAdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex))
        )(AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex).apply(Nil))
    }

}
