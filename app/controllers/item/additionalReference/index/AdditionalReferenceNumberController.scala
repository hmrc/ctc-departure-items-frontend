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

import config.PhaseConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import models.{Index, LocalReferenceNumber, Mode, Phase}
import navigation.{AdditionalReferenceNavigatorProvider, UserAnswersNavigator}
import pages.item.additionalReference.index.{
  AddAdditionalReferenceNumberYesNoPage,
  AdditionalReferenceInCL234Page,
  AdditionalReferenceNumberPage,
  AdditionalReferencePage
}
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
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(otherAdditionalReferenceNumbers: Seq[String], isDocumentInCL234: Boolean, phase: Phase): Form[String] =
    formProvider("item.additionalReference.index.additionalReferenceNumber", otherAdditionalReferenceNumbers, isDocumentInCL234, phase)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(AdditionalReferencePage(itemIndex, additionalReferenceIndex)))
    .andThen(getMandatoryPage.getSecond(AdditionalReferenceInCL234Page(itemIndex, additionalReferenceIndex))) {
      implicit request =>
        val viewModel = viewModelProvider.apply(request.userAnswers, itemIndex, additionalReferenceIndex, request.arg._1)
        val preparedForm = request.userAnswers.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)) match {
          case None        => form(viewModel.otherAdditionalReferenceNumbers, request.arg._2, phaseConfig.phase)
          case Some(value) => form(viewModel.otherAdditionalReferenceNumbers, request.arg._2, phaseConfig.phase).fill(value)
        }
        Ok(view(preparedForm, lrn, mode, itemIndex, additionalReferenceIndex, viewModel.isReferenceNumberRequired))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(AdditionalReferencePage(itemIndex, additionalReferenceIndex)))
    .andThen(getMandatoryPage.getSecond(AdditionalReferenceInCL234Page(itemIndex, additionalReferenceIndex)))
    .async {
      implicit request =>
        val viewModel = viewModelProvider.apply(request.userAnswers, itemIndex, additionalReferenceIndex, request.arg._1)

        form(viewModel.otherAdditionalReferenceNumbers, request.arg._2, phaseConfig.phase)
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
