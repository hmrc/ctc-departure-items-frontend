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

package controllers.item.documents

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Document, Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.AddDocumentsYesNoPage
import pages.item.documents.DocumentsInProgressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DocumentsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.item.documents.AddAnotherDocumentViewModel
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider
import views.html.item.documents.AddAnotherDocumentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherDocumentController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  navigatorProvider: ItemNavigatorProvider,
  formProvider: AddAnotherFormProvider,
  service: DocumentsService,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherDocumentView,
  viewModelProvider: AddAnotherDocumentViewModelProvider
)(implicit ec: ExecutionContext, config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherDocumentViewModel): Form[Boolean] =
    formProvider(viewModel.prefix)

  private def documents(itemIndex: Index)(implicit request: DataRequest[_]): Seq[Document] =
    service.getDocuments(request.userAnswers, itemIndex, None).values

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex, documents(itemIndex))
      viewModel.count match {
        case 0 =>
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
          AddDocumentsYesNoPage(itemIndex)
            .removeFromUserAnswers()
            .removeValue(DocumentsInProgressPage(itemIndex))
            .updateTask()
            .writeToSession()
            .navigate()
        case _ =>
          Future.successful(Ok(view(form(viewModel), lrn, viewModel, itemIndex)))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex, documents(itemIndex))
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, viewModel, itemIndex))),
          {
            case true =>
              Future.successful(Redirect(controllers.item.documents.index.routes.DocumentController.onPageLoad(lrn, mode, itemIndex, viewModel.nextIndex)))
            case false =>
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
              DocumentsInProgressPage(itemIndex).writeToUserAnswers(false).updateTask().writeToSession().navigate()
          }
        )
  }

  def redirectToDocuments(lrn: LocalReferenceNumber, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      DocumentsInProgressPage(itemIndex).writeToUserAnswers(true).updateTask().writeToSession().navigateTo(config.documentsFrontendUrl(lrn))
  }
}
