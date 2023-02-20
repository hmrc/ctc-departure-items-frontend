package controllers.items

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import models.NormalMode
import models.items.DeclarationType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.items.DeclarationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.items.DeclarationTypeView

import scala.concurrent.Future

class DeclarationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider[DeclarationType]("items.declarationType")
  private val mode         = NormalMode
  private lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, DeclarationType.radioItems, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, DeclarationType.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> DeclarationType.values.head.toString))

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, DeclarationType.radioItems, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", DeclarationType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, DeclarationType.radioItems, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", DeclarationType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
