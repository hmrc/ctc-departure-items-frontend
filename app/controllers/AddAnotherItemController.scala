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

package controllers

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions.*
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, NormalMode}
import pages.item.AddAnotherItemPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.http.HttpVerbs.GET
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.AddAnotherItemViewModel
import viewmodels.AddAnotherItemViewModel.AddAnotherItemViewModelProvider
import views.html.AddAnotherItemView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherItemController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherItemView,
  viewModelProvider: AddAnotherItemViewModelProvider
)(implicit ec: ExecutionContext, config: FrontendAppConfig, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private val mode = NormalMode

  private def form(viewModel: AddAnotherItemViewModel): Form[Boolean] = formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers)
      viewModel.count match {
        case 0 =>
          Redirect(controllers.item.routes.DescriptionController.onPageLoad(lrn, mode, Index(0)))
        case _ =>
          val preparedForm = request.userAnswers.get(AddAnotherItemPage) match {
            case None        => form(viewModel)
            case Some(value) => form(viewModel).fill(value)
          }
          Ok(view(preparedForm, lrn, viewModel))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, viewModel))),
          value =>
            AddAnotherItemPage
              .writeToUserAnswers(value)
              .updateTask()
              .writeToSession(sessionRepository)
              .navigateTo {
                if value then controllers.item.routes.DescriptionController.onPageLoad(lrn, mode, viewModel.nextIndex)
                else Call(GET, config.taskListUrl(lrn))
              }
        )
  }

}
