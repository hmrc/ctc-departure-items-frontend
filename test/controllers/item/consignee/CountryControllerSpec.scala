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

package controllers.item.consignee

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider.CountryFormProvider
import models.{NormalMode, SelectableList}
import navigation.ItemNavigatorProvider
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Gen
import pages.item.consignee.{CountryPage, NamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import services.CountriesService
import views.html.item.consignee.CountryView

import scala.concurrent.Future

class CountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = SelectableList(Seq(country1, country2))
  private val name        = Gen.alphaNumStr.sample.value

  private val formProvider = new CountryFormProvider()
  private val field        = formProvider.field
  private val form         = formProvider("item.consignee.country", countryList, name)
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService = mock[CountriesService]
  private lazy val countryRoute                      = routes.CountryController.onPageLoad(lrn, mode, itemIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  private val updatedUserAnswers = emptyUserAnswers.setValue(NamePage(itemIndex), name)

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  "Country Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountryCodesForAddress()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, countryList.values, mode, itemIndex, name)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountryCodesForAddress()(any())).thenReturn(Future.successful(countryList))
      val userAnswers = updatedUserAnswers.setValue(CountryPage(itemIndex), country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map(field -> country1.value))

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countryList.values, mode, itemIndex, name)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountryCodesForAddress()(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(POST, countryRoute)
        .withFormUrlEncodedBody((field, country1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }
    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountryCodesForAddress()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(updatedUserAnswers)

      val request   = FakeRequest(POST, countryRoute).withFormUrlEncodedBody((field, "invalid value"))
      val boundForm = form.bind(Map(field -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countryList.values, mode, itemIndex, name)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, countryRoute)
        .withFormUrlEncodedBody((field, country1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
