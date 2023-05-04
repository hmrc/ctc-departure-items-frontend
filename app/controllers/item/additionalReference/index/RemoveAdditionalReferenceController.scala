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

package controllers.item.additionalReference.index

import config.FrontendAppConfig
import controllers.actions._
import controllers.item.additionalReference.routes
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.reference.AdditionalReference
import models.requests.SpecificDataRequestProvider1
import models.{Index, LocalReferenceNumber, Mode}
import pages.item.additionalReference.index.{AdditionalReferenceNumberPage, AdditionalReferencePage}
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.additionalReference.index.RemoveAdditionalReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveAdditionalReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveAdditionalReferenceView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private type Request = SpecificDataRequestProvider1[AdditionalReference]#SpecificDataRequest[_]

  private def additionalReference(itemIndex: Index, additionalReferenceIndex: Index)(implicit request: Request): String =
    AdditionalReferenceDomain.asString(
      `type` = request.arg,
      number = request.userAnswers.get(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex))
    )

  private val form: Form[Boolean] = formProvider("item.additionalReference.index.removeAdditionalReference")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(AdditionalReferencePage(itemIndex, additionalReferenceIndex))) {
        implicit request =>
          Ok(view(form, lrn, mode, itemIndex, additionalReferenceIndex, additionalReference(itemIndex, additionalReferenceIndex)))
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(AdditionalReferencePage(itemIndex, additionalReferenceIndex)))
      .async {
        implicit request =>
          lazy val redirect = routes.AddAnotherAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex)
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(
                  BadRequest(view(formWithErrors, lrn, mode, itemIndex, additionalReferenceIndex, additionalReference(itemIndex, additionalReferenceIndex)))
                ),
              {
                case true =>
                  AdditionalReferenceSection(itemIndex, additionalReferenceIndex)
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
