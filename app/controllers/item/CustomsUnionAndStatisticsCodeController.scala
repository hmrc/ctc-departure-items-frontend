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
import forms.item.CUSCodeFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.CustomsUnionAndStatisticsCodePage
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CUSCodeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.CustomsUnionAndStatisticsCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsUnionAndStatisticsCodeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  formProvider: CUSCodeFormProvider,
  service: CUSCodeService,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CustomsUnionAndStatisticsCodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("item.customsUnionAndStatisticsCode")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(CustomsUnionAndStatisticsCodePage(itemIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode, itemIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val boundForm = form.bindFromRequest()
      boundForm
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex))),
          value =>
            service.doesCUSCodeExist(value).flatMap {
              case true =>
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                CustomsUnionAndStatisticsCodePage(itemIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
              case false =>
                val formWithErrors = boundForm.withError(FormError("value", "item.customsUnionAndStatisticsCode.error.not.exists"))
                Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex)))
            }
        )
  }
}
