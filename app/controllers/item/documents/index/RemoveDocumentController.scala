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

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions._
import controllers.item.documents.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.documents.DocumentSection
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.DocumentsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.documents.index.RemoveDocumentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveDocumentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveDocumentView,
  service: DocumentsService,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val form: Form[Boolean] = formProvider("item.documents.index.removeDocument")

  private def addAnother(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Call =
    routes.AddAnotherDocumentController.onPageLoad(lrn, mode, itemIndex)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, DocumentSection(itemIndex, documentIndex), addAnother(lrn, mode, itemIndex)) {
      implicit request =>
        service.getDocument(request.userAnswers, itemIndex, documentIndex) match {
          case Some(document) => Ok(view(form, lrn, mode, itemIndex, documentIndex, document))
          case None           => Redirect(addAnother(lrn, mode, itemIndex))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, DocumentSection(itemIndex, documentIndex), addAnother(lrn, mode, itemIndex))
    .async {
      implicit request =>
        service.getDocument(request.userAnswers, itemIndex, documentIndex) match {
          case Some(document) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, documentIndex, document))),
                {
                  case true =>
                    DocumentSection(itemIndex, documentIndex)
                      .removeFromUserAnswers()
                      .updateTask()
                      .writeToSession(sessionRepository)
                      .navigateTo(addAnother(lrn, mode, itemIndex))
                  case false =>
                    Future.successful(Redirect(addAnother(lrn, mode, itemIndex)))
                }
              )
          case None =>
            Future.successful(Redirect(addAnother(lrn, mode, itemIndex)))
        }
    }
}
