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

package controllers.item.supplyChainActors.index

import config.PhaseConfig
import controllers.actions._
import controllers.item.supplyChainActors.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import pages.sections.supplyChainActors.SupplyChainActorSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.supplyChainActors.index.RemoveSupplyChainActorView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveSupplyChainActorController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveSupplyChainActorView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("item.supplyChainActors.index.removeSupplyChainActor")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, actorIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Ok(view(form, lrn, mode, itemIndex, actorIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, actorIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      lazy val redirect = routes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode, itemIndex)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, actorIndex))),
          {
            case true =>
              SupplyChainActorSection(itemIndex, actorIndex)
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
