package controllers.items

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CountryFormProvider
import models.{CountryList, NormalMode}
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.items.ItemCountryOfDestinationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.items.ItemCountryOfDestinationView

import scala.concurrent.Future

class ItemCountryOfDestinationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = CountryList(Seq(country1, country2))

  private val formProvider = new CountryFormProvider()
  private val form         = formProvider("items.itemCountryOfDestination", countryList)
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService = mock[CountriesService]
  private lazy val itemCountryOfDestinationRoute     = routes.ItemCountryOfDestinationController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "ItemCountryOfDestination Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountries(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ItemCountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountries(any())).thenReturn(Future.successful(countryList))
      val userAnswers = emptyUserAnswers.setValue(ItemCountryOfDestinationPage, country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> country1.id))

      val view = injector.instanceOf[ItemCountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountries(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, itemCountryOfDestinationRoute)
        .withFormUrlEncodedBody(("value", country1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, itemCountryOfDestinationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[ItemCountryOfDestinationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, itemCountryOfDestinationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, itemCountryOfDestinationRoute)
        .withFormUrlEncodedBody(("value", country1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
