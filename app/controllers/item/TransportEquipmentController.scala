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

package controllers.item

import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider.EquipmentFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.TransportEquipmentPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.TransportEquipmentService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.TransportEquipmentView

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TransportEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: EquipmentFormProvider,
  service: TransportEquipmentService,
  val controllerComponents: MessagesControllerComponents,
  view: TransportEquipmentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val prefix: String = "item.transportEquipment"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val transportEquipmentList = service.getTransportEquipments(request.userAnswers)
      val form                   = formProvider(prefix, transportEquipmentList)
      val preparedForm = request.userAnswers.get(TransportEquipmentPage(itemIndex)) match {
        case None => form
        case Some(uuid) =>
          transportEquipmentList.values.find(_.uuid == uuid) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
      }
      Ok(view(preparedForm, lrn, transportEquipmentList.values, mode, itemIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val transportEquipmentList = service.getTransportEquipments(request.userAnswers)
      val form                   = formProvider(prefix, transportEquipmentList)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, transportEquipmentList.values, mode, itemIndex))),
          value =>
            val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
            TransportEquipmentPage(itemIndex).writeToUserAnswers(value.uuid).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
        )
  }
}
