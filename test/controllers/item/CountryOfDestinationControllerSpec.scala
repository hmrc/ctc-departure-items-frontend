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

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider
import generators.Generators
import models.{Index, NormalMode, SelectableList, UserAnswers}
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import pages.external.ConsignmentAdditionalInformationTypePage
import pages.item.{CountryOfDestinationInCL009Page, CountryOfDestinationPage}
import pages.sections.external.{ConsignmentAdditionalInformationListSection, ConsignmentAdditionalInformationSection}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.item.CountryOfDestinationView

import scala.concurrent.Future

class CountryOfDestinationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = SelectableList(Seq(country1, country2))

  private val formProvider = new SelectableFormProvider()
  private val form         = formProvider("item.countryOfDestination", countryList)
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService = mock[CountriesService]
  private lazy val itemCountryOfDestinationRoute     = routes.CountryOfDestinationController.onPageLoad(lrn, mode, itemIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "ItemCountryOfDestination Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, countryList.values, mode, itemIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers.setValue(CountryOfDestinationPage(itemIndex), country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> country1.code.code))

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countryList.values, mode, itemIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" - {
      "and value is in CL009" in {
        val userAnswers = emptyUserAnswers
          .setValue(ConsignmentAdditionalInformationTypePage(Index(0)), "30600")

        userAnswers.get(ConsignmentAdditionalInformationListSection) must be(defined)

        when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
        when(mockCountriesService.isCountryInCL009(any())(any())).thenReturn(Future.successful(true))
        when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, itemCountryOfDestinationRoute)
          .withFormUrlEncodedBody(("value", country1.code.code))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          s"http://localhost:10131/manage-transit-movements/departures/transport-details/$lrn/update-task?" +
          s"continue=http://localhost:10127${onwardRoute.url}"

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(ConsignmentAdditionalInformationSection(Index(0))) must not be defined
        userAnswersCaptor.getValue.get(CountryOfDestinationInCL009Page(Index(0))).value mustBe true
      }

      "and value is not in CL009" in {
        val userAnswers = emptyUserAnswers
          .setValue(ConsignmentAdditionalInformationTypePage(Index(0)), "30600")

        userAnswers.get(ConsignmentAdditionalInformationListSection) must be(defined)

        when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
        when(mockCountriesService.isCountryInCL009(any())(any())).thenReturn(Future.successful(false))
        when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, itemCountryOfDestinationRoute)
          .withFormUrlEncodedBody(("value", country1.code.code))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          s"http://localhost:10131/manage-transit-movements/departures/transport-details/$lrn/update-task?" +
          s"continue=http://localhost:10127${onwardRoute.url}"

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(ConsignmentAdditionalInformationSection(Index(0))) must be(defined)
        userAnswersCaptor.getValue.get(CountryOfDestinationInCL009Page(Index(0))).value mustBe false
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, itemCountryOfDestinationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countryList.values, mode, itemIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, itemCountryOfDestinationRoute)
        .withFormUrlEncodedBody(("value", country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
