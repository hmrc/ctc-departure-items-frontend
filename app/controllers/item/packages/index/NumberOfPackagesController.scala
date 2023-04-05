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

package controllers.item.packages.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.Constants.maxNumberOfPackages
import forms.IntFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{PackageNavigatorProvider, UserAnswersNavigator}
import pages.item.packages.index.NumberOfPackagesPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.packages.index.NumberOfPackagesView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NumberOfPackagesController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: PackageNavigatorProvider,
  formProvider: IntFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: NumberOfPackagesView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("item.packages.index.numberOfPackages", maxNumberOfPackages)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(NumberOfPackagesPage(itemIndex, packageIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode, itemIndex, packageIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, packageIndex))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, packageIndex)
            NumberOfPackagesPage(itemIndex, packageIndex).writeToUserAnswers(value).updateTask().writeToSession().navigate()
          }
        )
  }
}
