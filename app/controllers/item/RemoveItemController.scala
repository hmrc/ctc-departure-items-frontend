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

package controllers.item

import config.PhaseConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber}
import pages.item.DescriptionPage
import pages.sections.ItemSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.RemoveItemView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveItemController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveItemView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, itemIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, ItemSection(itemIndex), addAnother(lrn))
    .andThen(getMandatoryPage(DescriptionPage(itemIndex))) {
      implicit request =>
        Ok(view(form(itemIndex), lrn, itemIndex, request.arg))
    }

  private def form(itemIndex: Index): Form[Boolean] = formProvider("item.removeItem", itemIndex.display)

  private def addAnother(lrn: LocalReferenceNumber): Call =
    controllers.routes.AddAnotherItemController.onPageLoad(lrn)

  def onSubmit(lrn: LocalReferenceNumber, itemIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, ItemSection(itemIndex), addAnother(lrn))
    .andThen(getMandatoryPage(DescriptionPage(itemIndex)))
    .async {
      implicit request =>
        form(itemIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, itemIndex, request.arg))),
            {
              case true =>
                ItemSection(itemIndex)
                  .removeFromUserAnswers()
                  .updateTask()
                  .writeToSession(sessionRepository)
                  .navigateTo(addAnother(lrn))
              case false =>
                Future.successful(Redirect(addAnother(lrn)))
            }
          )
    }
}
