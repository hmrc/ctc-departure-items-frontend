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

import config.{FrontendAppConfig, PhaseConfig}
import controllers.item.documents.routes
import models.{Document, Index, ItemLevelDocuments, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import services.DocumentsService
import utils.cyaHelpers.item.documents.DocumentAnswersHelper
import viewmodels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherDocumentViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call,
  documents: Seq[Document],
  consignmentLevelDocumentsListItems: Seq[ListItem],
  allowMoreDocuments: Boolean
) extends AddAnotherViewModel {

  override def count: Int = super.count + consignmentLevelDocumentsListItems.length

  override val nextIndex: Index = Index(super.count)

  override val prefix: String = "item.documents.addAnotherDocument"

  def canAttachMoreDocumentsToItem: Boolean                           = documents.nonEmpty
  override def allowMore(implicit config: FrontendAppConfig): Boolean = allowMoreDocuments
}

object AddAnotherDocumentViewModel {

  class AddAnotherDocumentViewModelProvider @Inject() (implicit documentsService: DocumentsService) {

    def apply(userAnswers: UserAnswers, mode: Mode, itemIndex: Index, documents: Seq[Document])(implicit
      messages: Messages,
      config: FrontendAppConfig,
      phaseConfig: PhaseConfig
    ): AddAnotherDocumentViewModel = {
      val helper = new DocumentAnswersHelper(userAnswers, mode, itemIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      val itemLevelDocuments = ItemLevelDocuments(userAnswers, itemIndex)

      new AddAnotherDocumentViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherDocumentController.onSubmit(userAnswers.lrn, mode, itemIndex),
        documents = documents,
        consignmentLevelDocumentsListItems = helper.consignmentLevelListItems,
        allowMoreDocuments = !itemLevelDocuments.cannotAddAnyMore
      )
    }
  }
}
