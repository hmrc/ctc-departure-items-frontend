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

package viewmodels.item.documents

import config.FrontendAppConfig
import controllers.item.documents.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import services.DocumentsService
import utils.cyaHelpers.item.documents.DocumentAnswersHelper
import viewmodels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherDocumentViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call,
  consignmentLevelDocumentsListItems: Seq[ListItem],
  allowMoreDocuments: Boolean
) extends AddAnotherViewModel {

  override def count: Int = super.count + consignmentLevelDocumentsListItems.length

  override val nextIndex: Index = Index(super.count)

  override val prefix: String = "item.documents.addAnotherDocument"

  override def allowMore(implicit config: FrontendAppConfig): Boolean = allowMoreDocuments
}

object AddAnotherDocumentViewModel {

  class AddAnotherDocumentViewModelProvider @Inject() (documentsService: DocumentsService) {

    def apply(
      userAnswers: UserAnswers,
      mode: Mode,
      itemIndex: Index
    )(implicit messages: Messages, config: FrontendAppConfig): AddAnotherDocumentViewModel = {
      val helper = new DocumentAnswersHelper(documentsService)(userAnswers, mode, itemIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      val itemLevelDocuments = documentsService.getItemLevelDocuments(userAnswers, itemIndex, None)

      new AddAnotherDocumentViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherDocumentController.onSubmit(userAnswers.lrn, mode, itemIndex),
        consignmentLevelDocumentsListItems = helper.consignmentLevelListItems,
        allowMoreDocuments = !itemLevelDocuments.cannotAddAnyMore
      )
    }
  }
}
