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

package controllers.item.packages.index

import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider.PackageFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{PackageNavigatorProvider, UserAnswersNavigator}
import pages.item.packages.index
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.PackagesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.packages.index.PackageTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PackageTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: PackageNavigatorProvider,
  actions: Actions,
  formProvider: PackageFormProvider,
  service: PackagesService,
  val controllerComponents: MessagesControllerComponents,
  view: PackageTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "item.packages.index.packageType"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getPackageTypes().map {
        packageTypeList =>
          val form = formProvider(prefix, packageTypeList)
          val preparedForm = request.userAnswers.get(index.PackageTypePage(itemIndex, packageIndex)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, packageTypeList.values, mode, itemIndex, packageIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getPackageTypes().flatMap {
        packageTypeList =>
          val form = formProvider(prefix, packageTypeList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, packageTypeList.values, mode, itemIndex, packageIndex))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, packageIndex)
                index.PackageTypePage(itemIndex, packageIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
              }
            )
      }
  }
}
