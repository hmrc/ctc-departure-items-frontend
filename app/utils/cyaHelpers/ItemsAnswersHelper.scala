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

package utils.cyaHelpers

import config.FrontendAppConfig
import models.journeyDomain.item.ItemDomain
import models.{Mode, UserAnswers}
import pages.item.DescriptionPage
import pages.sections.ItemsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import viewmodels.ListItem

class ItemsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(ItemsSection) {
      itemIndex =>
        buildListItem[ItemDomain](
          nameWhenComplete = _.label,
          nameWhenInProgress = userAnswers.get(DescriptionPage(itemIndex)),
          removeRoute = Some(Call("GET", "#")) // TODO: replace with item remove route
        )(ItemDomain.userAnswersReader(itemIndex))
    }
}
