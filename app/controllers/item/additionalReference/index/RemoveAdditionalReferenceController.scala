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

import config.FrontendAppConfig
import controllers.actions._
import controllers.item.additionalReference.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.additionalReference.index.RemoveAdditionalReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveAdditionalReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveAdditionalReferenceView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private def form(additionalReferenceIndex: Index): Form[Boolean] =
    formProvider("item.additionalReference.index.removeAdditionalReference", additionalReferenceIndex.display)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn) {
        implicit request =>
          UserAnswersReader[AdditionalReferenceDomain](
            AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
          ).run(request.userAnswers).toOption match {
            case Some(value) =>
              Ok(view(form(additionalReferenceIndex), lrn, mode, itemIndex, additionalReferenceIndex, value.toString))
            case None =>
              logger.warn(s"Additional reference not found at index $additionalReferenceIndex in item $itemIndex")
              Redirect(config.sessionExpiredUrl)
          }
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .async {
        implicit request =>
          lazy val redirect = routes.AddAnotherAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex)
          form(additionalReferenceIndex)
            .bindFromRequest()
            .fold(
              formWithErrors =>
                UserAnswersReader[AdditionalReferenceDomain](
                  AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
                ).run(request.userAnswers).toOption match {
                  case Some(value) =>
                    Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, additionalReferenceIndex, value.toString)))
                  case None =>
                    logger.warn(s"Additional reference not found at index $additionalReferenceIndex in item $itemIndex")
                    Future.successful(Redirect(config.sessionExpiredUrl))
                },
              {
                case true =>
                  AdditionalReferenceSection(itemIndex, additionalReferenceIndex)
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
