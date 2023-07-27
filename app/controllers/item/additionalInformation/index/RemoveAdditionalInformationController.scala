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

package controllers.item.additionalInformation.index

import config.PhaseConfig
import controllers.actions._
import controllers.item.additionalInformation.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import pages.item.additionalInformation.index.AdditionalInformationTypePage
import pages.sections.additionalInformation.AdditionalInformationSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.additionalInformation.index.RemoveAdditionalInformationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveAdditionalInformationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveAdditionalInformationView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalInformationIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(AdditionalInformationTypePage(itemIndex, additionalInformationIndex))) {
        implicit request =>
          val additionalInformationType = request.arg.toString
          val form                      = formProvider("item.additionalInformation.index.removeAdditionalInformation")
          Ok(view(form, lrn, mode, itemIndex, additionalInformationIndex, additionalInformationType))
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalInformationIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(AdditionalInformationTypePage(itemIndex, additionalInformationIndex)))
      .async {
        implicit request =>
          lazy val redirect             = routes.AddAnotherAdditionalInformationController.onPageLoad(lrn, mode, itemIndex)
          val additionalInformationType = request.arg.toString
          val form                      = formProvider("item.additionalInformation.index.removeAdditionalInformation")
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, additionalInformationIndex, additionalInformationType))),
              {
                case true =>
                  AdditionalInformationSection(itemIndex, additionalInformationIndex)
                    .removeFromUserAnswers()
                    .updateTask()
                    .writeToSession()
                    .navigateTo(redirect)
                case false =>
                  Future.successful(Redirect(redirect))
              }
            )
      }
}
