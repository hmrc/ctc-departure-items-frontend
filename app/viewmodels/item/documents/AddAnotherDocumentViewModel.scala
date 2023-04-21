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

package viewmodels.item.documents

import config.FrontendAppConfig
import controllers.item.documents.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.item.documents.DocumentAnswersHelper
import viewmodels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherDocumentViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call
) extends AddAnotherViewModel {

  override val prefix: String = "item.documents.addAnotherDocument"

  override def maxCount(implicit config: FrontendAppConfig): Int = config.maxDocuments
}

object AddAnotherDocumentViewModel {

  class AddAnotherDocumentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit
      messages: Messages,
      config: FrontendAppConfig
    ): AddAnotherDocumentViewModel = {
      val helper = new DocumentAnswersHelper(userAnswers, mode, itemIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherDocumentViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherDocumentController.onSubmit(userAnswers.lrn, mode, itemIndex)
      )
    }
  }
}