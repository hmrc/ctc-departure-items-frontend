/*
 * Copyright 2025 HM Revenue & Customs
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
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.GrossWeightBeforeYouContinuePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.GrossWeightBeforeYouContinueView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GrossWeightBeforeYouContinueController @Inject (override val messagesApi: MessagesApi,
                                                      sessionRepository: SessionRepository,
                                                      navigatorProvider: ItemNavigatorProvider,
                                                      actions: Actions,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: GrossWeightBeforeYouContinueView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Ok(view(lrn, mode, itemIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
      GrossWeightBeforeYouContinuePage(itemIndex)
        .writeToUserAnswers(true)
        .updateTask()
        .writeToSession(sessionRepository)
        .navigateWith(navigator)
  }
}
