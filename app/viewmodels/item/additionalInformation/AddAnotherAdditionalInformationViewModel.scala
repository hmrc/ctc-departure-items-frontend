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

package viewmodels.item.additionalInformation

import config.FrontendAppConfig
import controllers.item.additionalInformation.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.item.additionalInformation.AdditionalInformationAnswersHelper
import viewmodels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherAdditionalInformationViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call
) extends AddAnotherViewModel {

  override val prefix: String = "item.additionalInformation.addAnotherAdditionalInformation"

  override def maxCount(implicit config: FrontendAppConfig): Int = config.maxAdditionalInformation
}

object AddAnotherAdditionalInformationViewModel {

  class AddAnotherAdditionalInformationViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit
      messages: Messages,
      config: FrontendAppConfig
    ): AddAnotherAdditionalInformationViewModel = {
      val helper = new AdditionalInformationAnswersHelper(userAnswers, mode, itemIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherAdditionalInformationViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherAdditionalInformationController.onSubmit(userAnswers.lrn, mode, itemIndex)
      )
    }
  }
}