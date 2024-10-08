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

package controllers.item.additionalInformation

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions._
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.ItemNavigatorProvider
import pages.sections.additionalInformation.AdditionalInformationListSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.item.additionalInformation.AddAnotherAdditionalInformationViewModel
import viewmodels.item.additionalInformation.AddAnotherAdditionalInformationViewModel.AddAnotherAdditionalInformationViewModelProvider
import views.html.item.additionalInformation.AddAnotherAdditionalInformationView

import javax.inject.Inject

class AddAnotherAdditionalInformationController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherAdditionalInformationView,
  viewModelProvider: AddAnotherAdditionalInformationViewModelProvider
)(implicit config: FrontendAppConfig, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherAdditionalInformationViewModel): Form[Boolean] = formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex)
      viewModel.count match {
        case 0 =>
          Redirect(controllers.item.routes.AddAdditionalInformationYesNoController.onPageLoad(lrn, mode, itemIndex))
        case _ =>
          Ok(view(form(viewModel), lrn, viewModel, itemIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, viewModel, itemIndex)),
          {
            case true =>
              Redirect(
                controllers.item.additionalInformation.index.routes.AdditionalInformationTypeController.onPageLoad(lrn, mode, itemIndex, viewModel.nextIndex)
              )
            case false =>
              Redirect(navigatorProvider(mode, itemIndex).nextPage(request.userAnswers, Some(AdditionalInformationListSection(itemIndex))))
          }
        )
  }
}
