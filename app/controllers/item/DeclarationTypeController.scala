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

import config.PhaseConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.{DeclarationTypeItemLevel, Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.DeclarationTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DeclarationTypeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.DeclarationTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  declarationTypeService: DeclarationTypeService,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationTypeView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(declarationTypes: Seq[DeclarationTypeItemLevel]): Form[DeclarationTypeItemLevel] =
    formProvider[DeclarationTypeItemLevel]("item.declarationType", declarationTypes)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      declarationTypeService.getDeclarationTypeItemLevel().map {
        declarationTypes =>
          val preparedForm = request.userAnswers.get(DeclarationTypePage(itemIndex)) match {
            case None        => form(declarationTypes)
            case Some(value) => form(declarationTypes).fill(value)
          }

          Ok(view(preparedForm, lrn, declarationTypes, mode, itemIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      declarationTypeService.getDeclarationTypeItemLevel().flatMap {
        declarationTypes =>
          form(declarationTypes)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, declarationTypes, mode, itemIndex))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                DeclarationTypePage(itemIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
              }
            )
      }
  }
}
