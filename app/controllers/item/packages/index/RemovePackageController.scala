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
import forms.YesNoFormProvider
import models.reference.PackageType
import models.requests.SpecificDataRequestProvider1
import models.{Index, LocalReferenceNumber, Mode}
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
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemovePackageView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(packageType: PackageType): Form[Boolean] = formProvider("item.packages.index.removePackage", packageType)

  private type Request = SpecificDataRequestProvider1[PackageType]#SpecificDataRequest[_]

  private def packageType(implicit request: Request): PackageType = request.arg

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(PackageTypePage(itemIndex, packageIndex))) {
      implicit request =>
        Ok(view(form(packageType), lrn, mode, itemIndex, packageIndex, packageType))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, packageIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(PackageTypePage(itemIndex, packageIndex)))
    .async {
      implicit request =>
        lazy val redirect = Call("GET", "#")
        form(packageType)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex, packageIndex, packageType))),
            {
              case true =>
                PackageSection(itemIndex, packageIndex)
                  .removeFromUserAnswers()
                  .updateTask()
                  .writeToSession()
                  .navigateTo(redirect)
              case false =>
                Future.successful(Redirect(redirect))
            }
          )
    }
}
