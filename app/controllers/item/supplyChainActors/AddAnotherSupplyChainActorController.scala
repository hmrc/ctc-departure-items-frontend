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

package controllers.item.supplyChainActors

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherFormProvider
import controllers.item.supplyChainActors.index.{routes => supplyChainActorRoutes}
import models.{Index, LocalReferenceNumber, Mode}
import navigation.ItemNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.item.supplyChainActors.AddAnotherSupplyChainActorViewModel
import viewmodels.item.supplyChainActors.AddAnotherSupplyChainActorViewModel.AddAnotherSupplyChainActorViewModelProvider
import views.html.item.supplyChainActors.AddAnotherSupplyChainActorView

import javax.inject.Inject

class AddAnotherSupplyChainActorController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  navigatorProvider: ItemNavigatorProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherSupplyChainActorView,
  viewModelProvider: AddAnotherSupplyChainActorViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherSupplyChainActorViewModel): Form[Boolean] = formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, itemIndex)
      viewModel.count match {
        case 0 =>
          Redirect(controllers.item.routes.AddSupplyChainActorYesNoController.onPageLoad(lrn, mode, itemIndex))
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
              Redirect(supplyChainActorRoutes.SupplyChainActorTypeController.onPageLoad(lrn, mode, itemIndex, viewModel.nextIndex))
            case false =>
              Redirect(navigatorProvider(mode, itemIndex).nextPage(request.userAnswers))
          }
        )
  }

}
