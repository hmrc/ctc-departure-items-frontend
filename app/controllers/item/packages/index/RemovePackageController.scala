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
import controllers.item.packages.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, LocalReferenceNumber, Mode, Packaging, UserAnswers}
import pages.item.packages.index.PackageTypePage
import pages.sections.packages.PackageSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.packages.index.RemovePackageView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemovePackageController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemovePackageView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, PackageSection(itemIndex, packageIndex), addAnother(lrn, mode, itemIndex))
    .andThen(getMandatoryPage(PackageTypePage(itemIndex, packageIndex))) {
      implicit request =>
        Ok(
          view(form, lrn, mode, itemIndex, packageIndex, insetText(request.userAnswers, itemIndex, packageIndex))
        )
    }

  private def form: Form[Boolean] = formProvider("item.packages.index.removePackage")

  private def addAnother(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Call =
    routes.AddAnotherPackageController.onPageLoad(lrn, mode, itemIndex)

  private def insetText(userAnswers: UserAnswers, itemIndex: Index, packageIndex: Index): Option[String] =
    Packaging(userAnswers, itemIndex, packageIndex).map(_.forRemoveDisplay)

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions
    .requireIndex(lrn, PackageSection(itemIndex, packageIndex), addAnother(lrn, mode, itemIndex))
    .andThen(getMandatoryPage(PackageTypePage(itemIndex, packageIndex)))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(
                BadRequest(view(formWithErrors, lrn, mode, itemIndex, packageIndex, insetText(request.userAnswers, itemIndex, packageIndex)))
              ),
            {
              case true =>
                PackageSection(itemIndex, packageIndex)
                  .removeFromUserAnswers()
                  .updateTask()
                  .writeToSession(sessionRepository)
                  .navigateTo(addAnother(lrn, mode, itemIndex))
              case false =>
                Future.successful(Redirect(addAnother(lrn, mode, itemIndex)))
            }
          )
    }
}
