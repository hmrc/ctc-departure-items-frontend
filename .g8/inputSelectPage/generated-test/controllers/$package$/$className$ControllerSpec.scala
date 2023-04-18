package controllers.$package$

import base.{SpecBase, AppWithDefaultMockFixtures}
import forms.SelectableFormProvider
import models.{NormalMode, SelectableList}
import navigation.$navRoute$NavigatorProvider
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.$package$.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.$serviceName$
import views.html.$package$.$className$View

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val $referenceClass;format="decap"$1 = arbitrary$referenceClass$.arbitrary.sample.get
  private val $referenceClass;format="decap"$2 = arbitrary$referenceClass$.arbitrary.sample.get
  private val $referenceClass;format="decap"$List = SelectableList(Seq($referenceClass;format="decap"$1, $referenceClass;format="decap"$2))

  private val formProvider = new SelectableFormProvider()
  private val form         = formProvider("$package$.$className;format="decap"$", $referenceClass;format="decap"$List)
  private val mode         = NormalMode

  private val mock$serviceName$: $serviceName$ = mock[$serviceName$]
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[$navRoute$NavigatorProvider]).toInstance(fake$navRoute$NavigatorProvider))
      .overrides(bind(classOf[$serviceName$]).toInstance(mock$serviceName$))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceClass;format="decap"$List))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, $referenceClass;format="decap"$List.values, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceClass;format="decap"$List))
      val userAnswers = emptyUserAnswers.setValue($className$Page, $referenceClass;format="decap"$1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> $referenceClass;format="decap"$1.value))

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, $referenceClass;format="decap"$List.values, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceClass;format="decap"$List))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(("value", $referenceClass;format="decap"$1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceClass;format="decap"$List))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, $referenceClass;format="decap"$List.values, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(("value", $referenceClass;format="decap"$1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
