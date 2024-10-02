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

package controllers.item.consignee

import config.PhaseConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DynamicAddressFormProvider
import models.reference.Country
import models.requests.SpecificDataRequestProvider2
import models.{DynamicAddress, Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.consignee._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.consignee.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: DynamicAddressFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: AddressView
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider2[String, Country]#SpecificDataRequest[?]

  private def name(implicit request: Request): String = request.arg._1

  private def country(implicit request: Request): Country = request.arg._2

  private def form(isPostalCodeRequired: Boolean)(implicit request: Request): Form[DynamicAddress] =
    formProvider("item.consignee.address", isPostalCodeRequired, name)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(NamePage(itemIndex)))
    .andThen(getMandatoryPage.getSecond(CountryPage(itemIndex)))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).map {
          isPostalCodeRequired =>
            val preparedForm = request.userAnswers.get(AddressPage(itemIndex)) match {
              case None        => form(isPostalCodeRequired)
              case Some(value) => form(isPostalCodeRequired).fill(value)
            }

            Ok(view(preparedForm, lrn, mode, name, isPostalCodeRequired, itemIndex))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(NamePage(itemIndex)))
    .andThen(getMandatoryPage.getSecond(CountryPage(itemIndex)))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).flatMap {
          isPostalCodeRequired =>
            form(isPostalCodeRequired)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, name, isPostalCodeRequired, itemIndex))),
                value => {
                  val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                  AddressPage(itemIndex).writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
                }
              )
        }
    }
}
