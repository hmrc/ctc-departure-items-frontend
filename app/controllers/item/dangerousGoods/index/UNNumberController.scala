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

package controllers.item.dangerousGoods.index

import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.item.dangerousGoods.UNNumberFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{DangerousGoodsNavigatorProvider, UserAnswersNavigator}
import pages.item.dangerousGoods.index.UNNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.dangerousGoods.index.UNNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UNNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: DangerousGoodsNavigatorProvider,
  formProvider: UNNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: UNNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("item.dangerousGoods.index.unNumber")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, dangerousGoodsIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(UNNumberPage(itemIndex, dangerousGoodsIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode, itemIndex, dangerousGoodsIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, dangerousGoodsIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, dangerousGoodsIndex))),
          value => {
            val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, dangerousGoodsIndex)
            UNNumberPage(itemIndex, dangerousGoodsIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
          }
        )
  }
}
