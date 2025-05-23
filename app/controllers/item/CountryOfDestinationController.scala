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

import config.FrontendAppConfig
import controllers.actions.*
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner, UpdateOps}
import forms.SelectableFormProvider.CountryFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.{CountryOfDestinationInCL009Page, CountryOfDestinationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.CountryOfDestinationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfDestinationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: CountryFormProvider,
  service: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: CountryOfDestinationView
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "item.countryOfDestination"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCountries().map {
        countryList =>
          val form = formProvider(prefix, countryList)
          val preparedForm = request.userAnswers.get(CountryOfDestinationPage(itemIndex)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, countryList.values, mode, itemIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, itemIndex: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCountries().flatMap {
        countryList =>
          val form = formProvider(prefix, countryList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryList.values, mode, itemIndex))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, itemIndex)
                service.isCountryInCL009(value).flatMap {
                  isCountryInCL009 =>
                    CountryOfDestinationPage(itemIndex)
                      .writeToUserAnswers(value)
                      .appendValue(CountryOfDestinationInCL009Page(itemIndex), isCountryInCL009)
                      .amendUserAnswers(_.removeConsignmentAdditionalInformation(isCountryInCL009))
                      .updateTask()
                      .writeToSession(sessionRepository)
                      .getNextPage(navigator)
                      .updateTransportDetails(lrn)
                      .navigate()
                }
              }
            )
      }
  }
}
