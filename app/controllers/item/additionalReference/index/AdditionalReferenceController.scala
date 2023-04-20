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

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.additionalReference.index.AdditionalReferencePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.AdditionalReferencesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.additionalReference.index.AdditionalReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdditionalReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: SelectableFormProvider,
  service: AdditionalReferencesService,
  val controllerComponents: MessagesControllerComponents,
  view: AdditionalReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "item.additionalReference.index.additionalReference"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        service.getAdditionalReferences.map {
          additionalReferences =>
            val form = formProvider(prefix, additionalReferences)
            val preparedForm = request.userAnswers.get(AdditionalReferencePage(itemIndex, additionalReferenceIndex)) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, lrn, additionalReferences.values, mode, itemIndex, additionalReferenceIndex))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, additionalReferenceIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getAdditionalReferences.flatMap {
        additionalReferences =>
          val form = formProvider(prefix, additionalReferences)
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(BadRequest(view(formWithErrors, lrn, additionalReferences.values, mode, itemIndex, additionalReferenceIndex))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                AdditionalReferencePage(itemIndex, additionalReferenceIndex).writeToUserAnswers(value).updateTask().writeToSession().navigate()
              }
            )
      }
  }
}
