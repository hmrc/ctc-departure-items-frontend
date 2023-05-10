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

package controllers.item.additionalReference.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{AdditionalReferenceNavigatorProvider, UserAnswersNavigator}
import pages.item.additionalReference.index.{AddAdditionalReferenceNumberYesNoPage, AdditionalReferenceNumberPage, AdditionalReferencePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.item.additionalReference.AdditionalReferenceNumberViewModel.AdditionalReferenceNumberViewModelProvider
import views.html.item.additionalReference.index.AdditionalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdditionalReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: AdditionalReferenceNavigatorProvider,
  formProvider: AdditionalReferenceNumberFormProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AdditionalReferenceNumberView,
  viewModelProvider: AdditionalReferenceNumberViewModelProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(otherAdditionalReferenceNumbers: Seq[String]): Form[String] =
    formProvider("item.additionalReference.index.additionalReferenceNumber", otherAdditionalReferenceNumbers)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(AdditionalReferencePage(itemIndex, additionalReferenceIndex))) {
      implicit request =>
        val viewModel = viewModelProvider.apply(request.userAnswers, itemIndex, additionalReferenceIndex, request.arg)
        val preparedForm = request.userAnswers.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) match {
          case None        => form(viewModel.otherAdditionalReferenceNumbers)
          case Some(value) => form(viewModel.otherAdditionalReferenceNumbers).fill(value)
        }
        Ok(view(preparedForm, lrn, mode, itemIndex, additionalReferenceIndex, viewModel.isReferenceNumberRequired))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(AdditionalReferencePage(itemIndex, additionalReferenceIndex)))
    .async {
      implicit request =>
        val viewModel = viewModelProvider.apply(request.userAnswers, itemIndex, additionalReferenceIndex, request.arg)
        form(viewModel.otherAdditionalReferenceNumbers)
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, additionalReferenceIndex, viewModel.isReferenceNumberRequired))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, additionalReferenceIndex)
              AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)
                .writeToUserAnswers(value)
                .appendValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
                .updateTask()
                .writeToSession()
                .navigate()
            }
          )
    }
}
