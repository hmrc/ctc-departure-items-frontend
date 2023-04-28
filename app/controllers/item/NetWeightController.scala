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

package controllers.item

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.NetWeightFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.{GrossWeightPage, NetWeightPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.NetWeightView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NetWeightController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: NetWeightFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: NetWeightView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[BigDecimal]#SpecificDataRequest[_]

  private def grossWeight(implicit request: Request): BigDecimal = request.arg

  private def form(grossWeight: BigDecimal): Form[BigDecimal] =
    formProvider("item.netWeight", grossWeight)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(GrossWeightPage(itemIndex))) {
      implicit request =>
        val preparedForm = request.userAnswers.get(NetWeightPage(itemIndex)) match {
          case None        => form(grossWeight)
          case Some(value) => form(grossWeight).fill(value)
        }
        Ok(view(preparedForm, lrn, mode, itemIndex))
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(GrossWeightPage(itemIndex)))
    .async {
      implicit request =>
        form(grossWeight)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, itemIndex))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
              NetWeightPage(itemIndex).writeToUserAnswers(value).updateTask().writeToSession().navigate()
            }
          )
    }
}
