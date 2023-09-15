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
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EoriNumberFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{SupplyChainActorNavigatorProvider, UserAnswersNavigator}
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.supplyChainActors.index.IdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: SupplyChainActorNavigatorProvider,
  formProvider: EoriNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: IdentificationNumberView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("item.supplyChainActors.index.identificationNumber")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, actorIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(SupplyChainActorTypePage(itemIndex, actorIndex))) {
      implicit request =>
        val supplyChainActor = request.arg.toString.toLowerCase

        val preparedForm = request.userAnswers.get(IdentificationNumberPage(itemIndex, actorIndex)) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, lrn, mode, itemIndex, actorIndex, supplyChainActor))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, actorIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(SupplyChainActorTypePage(itemIndex, actorIndex)))
    .async {
      implicit request =>
        val supplyChainActor = request.arg.toString.toLowerCase
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, actorIndex, supplyChainActor))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, actorIndex)
              IdentificationNumberPage(itemIndex, actorIndex).writeToUserAnswers(value).updateTask().writeToSession().navigate()
            }
          )
    }
}
