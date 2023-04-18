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

package controllers.item.documents.index

import config.FrontendAppConfig
import controllers.actions._
import forms.DocumentFormProvider
import models.{DocumentList, Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.ItemNavigatorProvider
import pages.item.documents.index.DocumentPage
import pages.sections.external.DocumentsSection
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.documents.index.DocumentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DocumentController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: DocumentFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DocumentView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val prefix: String = "item.countryOfDispatch"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      request.userAnswers.get(DocumentsSection).validate(DocumentList.reads) match {
        case Some(documentList) =>
          val form = formProvider(prefix, documentList)
          val preparedForm = request.userAnswers.get(DocumentPage(itemIndex, documentIndex)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          Ok(view(preparedForm, lrn, documentList.documents, mode, itemIndex, documentIndex))
        case None =>
          handleError
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      request.userAnswers.get(DocumentsSection).validate(DocumentList.reads) match {
        case Some(documentList) =>
          ??? // Pass docs to form and view, then navigate
        case None =>
          Future.successful(handleError)
      }
  }

  private def handleError: Result = {
    logger.error("Failed to read documents from user answers")
    Redirect(config.technicalDifficultiesUrl)
  }
}
