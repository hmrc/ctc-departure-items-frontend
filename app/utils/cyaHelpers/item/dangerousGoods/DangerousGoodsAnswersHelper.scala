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

package utils.cyaHelpers.item.dangerousGoods

import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.{Index, Mode, UserAnswers}
import pages.item.dangerousGoods.index.UNNumberPage
import pages.sections.dangerousGoods.DangerousGoodsListSection
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class DangerousGoodsAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(DangerousGoodsListSection(itemIndex)) {
      index =>
        val removeRoute: Option[Call] = if (userAnswers.get(UNNumberPage(itemIndex, index)).isEmpty && index.isFirst) {
          None
        } else {
          Some(Call("GET", "#")) //TODO: Replace with remove controller
        }

        buildListItem[DangerousGoodsDomain](
          nameWhenComplete = _.unNumber,
          nameWhenInProgress = userAnswers.get(UNNumberPage(itemIndex, index)),
          removeRoute = removeRoute
        )(DangerousGoodsDomain.userAnswersReader(itemIndex, index))
    }

}
