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

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.TransportEquipmentPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.TransportEquipmentService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.TransportEquipmentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TransportEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: SelectableFormProvider,
  service: TransportEquipmentService,
  val controllerComponents: MessagesControllerComponents,
  view: TransportEquipmentView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val prefix: String = "item.transportEquipment"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      service.getTransportEquipments(request.userAnswers) match {
        case Some(transportEquipmentList) =>
          val form = formProvider(prefix, transportEquipmentList)
          val preparedForm = request.userAnswers.get(TransportEquipmentPage(itemIndex)) match {
            case None => form
            case Some(number) =>
              transportEquipmentList.values.find(_.number == number) match {
                case None        => form
                case Some(value) => form.fill(value)
              }
          }
          Ok(view(preparedForm, lrn, transportEquipmentList.values, mode, itemIndex))
        case None =>
          handleError
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getTransportEquipments(request.userAnswers) match {
        case Some(transportEquipmentList) =>
          val form = formProvider(prefix, transportEquipmentList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, transportEquipmentList.values, mode, itemIndex))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                TransportEquipmentPage(itemIndex).writeToUserAnswers(value.number).updateTask().writeToSession().navigate()
              }
            )
        case None =>
          Future.successful(handleError)
      }
  }

  private def handleError: Result = {
    logger.error("Failed to read transport equipments from user answers")
    Redirect(config.technicalDifficultiesUrl)
  }
}
