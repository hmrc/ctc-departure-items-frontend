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
import forms.EnumerableFormProvider
import models.reference.TransportChargesMethodOfPayment
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.TransportChargesMethodOfPaymentPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.TransportChargesMethodOfPaymentService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.TransportMethodOfPaymentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TransportChargesMethodOfPaymentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TransportMethodOfPaymentView,
  methodOfPaymentService: TransportChargesMethodOfPaymentService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(methodOfPayment: Seq[TransportChargesMethodOfPayment]): Form[TransportChargesMethodOfPayment] =
    formProvider("item.transportMethodOfPayment", methodOfPayment)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      methodOfPaymentService.getTransportChargesMethodOfPaymentTypes().map {
        methodOfPayment =>
          val preparedForm = request.userAnswers.get(TransportChargesMethodOfPaymentPage(itemIndex)) match {
            case None        => form(methodOfPayment)
            case Some(value) => form(methodOfPayment).fill(value)
          }

          Ok(view(preparedForm, lrn, methodOfPayment, mode, itemIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      methodOfPaymentService.getTransportChargesMethodOfPaymentTypes().flatMap {
        methodOfPayment =>
          form(methodOfPayment)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, methodOfPayment, mode, itemIndex))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                TransportChargesMethodOfPaymentPage(itemIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
              }
            )
      }
  }
}
