package controllers.item.additionalReference.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider
import models.{NormalMode, SelectableList}
import navigation.PreTaskListDetailsNavigatorProvider
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.item.additionalReference.index.AdditionalReferencePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AdditionalReferencesService
import views.html.item.additionalReference.index.AdditionalReferenceView

import scala.concurrent.Future

class AdditionalReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val additionalReference1    = arbitraryAdditionalReference.arbitrary.sample.get
  private val additionalReference2    = arbitraryAdditionalReference.arbitrary.sample.get
  private val additionalReferenceList = SelectableList(Seq(additionalReference1, additionalReference2))

  private val formProvider = new SelectableFormProvider()
  private val form         = formProvider("item.additionalReference.index.additionalReference", additionalReferenceList)
  private val mode         = NormalMode

  private val mockAdditionalReferencesService: AdditionalReferencesService = mock[AdditionalReferencesService]
  private lazy val additionalReferenceRoute                                = routes.AdditionalReferenceController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListDetailsNavigatorProvider]).toInstance(fakePreTaskListDetailsNavigatorProvider))
      .overrides(bind(classOf[AdditionalReferencesService]).toInstance(mockAdditionalReferencesService))

  "AdditionalReference Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockAdditionalReferencesService.getAdditionalReferences(any())).thenReturn(Future.successful(additionalReferenceList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, additionalReferenceRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, additionalReferenceList.values, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockAdditionalReferencesService.getAdditionalReferences(any())).thenReturn(Future.successful(additionalReferenceList))
      val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage, additionalReference1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, additionalReferenceRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> additionalReference1.value))

      val view = injector.instanceOf[AdditionalReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, additionalReferenceList.values, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockAdditionalReferencesService.getAdditionalReferences(any())).thenReturn(Future.successful(additionalReferenceList))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, additionalReferenceRoute)
        .withFormUrlEncodedBody(("value", additionalReference1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockAdditionalReferencesService.getAdditionalReferences(any())).thenReturn(Future.successful(additionalReferenceList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, additionalReferenceRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalReferenceView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, additionalReferenceList.values, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, additionalReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, additionalReferenceRoute)
        .withFormUrlEncodedBody(("value", additionalReference1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
