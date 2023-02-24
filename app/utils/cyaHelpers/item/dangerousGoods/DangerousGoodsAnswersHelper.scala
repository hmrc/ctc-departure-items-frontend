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

import controllers.item.dangerousGoods.index.routes
import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.{Index, Mode, UserAnswers}
import pages.item.dangerousGoods.index.UNNumberPage
import pages.sections.dangerousGoods.DangerousGoodsListSection
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class DangerousGoodsAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(DangerousGoodsListSection(itemIndex)) {
      dangerousGoodsIndex =>
        buildListItem[DangerousGoodsDomain](
          nameWhenComplete = _.toString,
          nameWhenInProgress = userAnswers.get(UNNumberPage(itemIndex, dangerousGoodsIndex)),
          removeRoute = Option(routes.RemoveUNNumberController.onPageLoad(lrn, mode, itemIndex, dangerousGoodsIndex))
        )(DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex))
    }

}
