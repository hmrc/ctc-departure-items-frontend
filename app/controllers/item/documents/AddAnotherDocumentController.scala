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

package controllers.item.documents

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.documents.AddAnotherDocumentPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.item.documents.AddAnotherDocumentViewModel
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider
import views.html.item.documents.AddAnotherDocumentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherDocumentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  actions: Actions,
  navigatorProvider: ItemNavigatorProvider,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherDocumentView,
  viewModelProvider: AddAnotherDocumentViewModelProvider
)(implicit ec: ExecutionContext, config: FrontendAppConfig, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherDocumentViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex)
      viewModel.count match {
        case 0 =>
          Redirect(controllers.item.routes.AddDocumentsYesNoController.onPageLoad(lrn, mode, itemIndex))
        case _ =>
          Ok(view(form(viewModel), lrn, viewModel, itemIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, viewModel, itemIndex))),
          {
            value =>
              val write = AddAnotherDocumentPage(itemIndex)
                .writeToUserAnswers(value)
                .updateTask()
                .writeToSession(sessionRepository)

              if (value) {
                write.navigateTo(controllers.item.documents.index.routes.DocumentController.onPageLoad(lrn, mode, itemIndex, viewModel.nextIndex))
              } else {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                write.navigateWith(navigator)
              }
          }
        )
  }
}
