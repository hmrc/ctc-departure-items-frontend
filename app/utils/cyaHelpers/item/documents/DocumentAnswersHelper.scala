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

package utils.cyaHelpers.item.documents

import config.FrontendAppConfig
import controllers.item.documents.index.routes
import models.{Index, Mode, UserAnswers}
import pages.item.documents.index.DocumentPage
import pages.sections.documents.DocumentsSection
import play.api.i18n.Messages
import services.DocumentsService
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class DocumentAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def consignmentLevelListItems(implicit documentsService: DocumentsService): Seq[ListItem] =
    documentsService.getConsignmentLevelDocuments(userAnswers).map {
      document =>
        ListItem(
          name = document.toString,
          changeUrl = None,
          removeUrl = None
        )
    }

  def listItems(implicit documentsService: DocumentsService): Seq[Either[ListItem, ListItem]] =
    buildListItems(DocumentsSection(itemIndex)) {
      documentIndex =>
        val page = DocumentPage(itemIndex, documentIndex)
        for {
          changeUrl <- page.route(userAnswers, mode).map(_.url)
          document  <- documentsService.getDocument(userAnswers, itemIndex, documentIndex)
        } yield Right(
          ListItem(
            name = document.toString,
            changeUrl = changeUrl,
            removeUrl = Option(routes.RemoveDocumentController.onPageLoad(lrn, mode, itemIndex, documentIndex).url)
          )
        )
    }

}
