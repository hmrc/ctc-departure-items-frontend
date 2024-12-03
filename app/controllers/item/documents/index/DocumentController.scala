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

package controllers.item.documents.index

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DocumentFormProvider
import models.{Index, ItemLevelDocuments, LocalReferenceNumber, Mode}
import navigation.{DocumentNavigatorProvider, UserAnswersNavigator}
import pages.item.documents.index.DocumentPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DocumentsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.documents.index.{DocumentView, NoDocumentsToAttachView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DocumentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: DocumentNavigatorProvider,
  actions: Actions,
  formProvider: DocumentFormProvider,
  service: DocumentsService,
  val controllerComponents: MessagesControllerComponents,
  documentsAvailableView: DocumentView,
  noDocumentsAvailableView: NoDocumentsToAttachView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val prefix: String = "item.documents.index.document"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val documentList = service.getDocuments(request.userAnswers, itemIndex, Some(documentIndex))
      documentList.values match {
        case Nil =>
          Ok(noDocumentsAvailableView(lrn, itemIndex, documentIndex))
        case values =>
          val itemLevelDocuments = ItemLevelDocuments(request.userAnswers, itemIndex, documentIndex)(service)
          val form               = formProvider(prefix, documentList, itemLevelDocuments)(config)
          val preparedForm = request.userAnswers.get(DocumentPage(itemIndex, documentIndex)) match {
            case None => form
            case Some(uuid) =>
              values.find(_.uuid == uuid) match {
                case None        => form
                case Some(value) => form.fill(value)
              }
          }
          Ok(documentsAvailableView(preparedForm, lrn, values, mode, itemIndex, documentIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val documentList       = service.getDocuments(request.userAnswers, itemIndex, Some(documentIndex))
      val itemLevelDocuments = ItemLevelDocuments(request.userAnswers, itemIndex, documentIndex)(service)
      val form               = formProvider(prefix, documentList, itemLevelDocuments)(config)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(documentsAvailableView(formWithErrors, lrn, documentList.values, mode, itemIndex, documentIndex))),
          value => {
            val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex, documentIndex)
            DocumentPage(itemIndex, documentIndex)
              .writeToUserAnswers(value.uuid)
              .updateTask()
              .writeToSession(sessionRepository)
              .navigateWith(navigator)
          }
        )
  }

  def redirectToDocuments(lrn: LocalReferenceNumber, itemIndex: Index, documentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Redirect(config.documentsFrontendUrl(lrn))
  }
}
