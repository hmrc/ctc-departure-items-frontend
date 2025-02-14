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

package controllers.item.additionalInformation.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider
import generators.Generators
import models.{NormalMode, SelectableList}
import navigation.AdditionalInformationNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.item.additionalInformation.index.AdditionalInformationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.AdditionalInformationService
import views.html.item.additionalInformation.index.AdditionalInformationTypeView

import scala.concurrent.Future

class AdditionalInformationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val additionalInformation1    = arbitraryAdditionalInformation.arbitrary.sample.get
  private val additionalInformation2    = arbitraryAdditionalInformation.arbitrary.sample.get
  private val additionalInformationList = SelectableList(Seq(additionalInformation1, additionalInformation2))

  private val formProvider = new SelectableFormProvider()
  private val form         = formProvider("item.additionalInformation.index.additionalInformationType", additionalInformationList)
  private val mode         = NormalMode

  private val mockAdditionalInformationService: AdditionalInformationService = mock[AdditionalInformationService]

  private lazy val additionalInformationTypeRoute = routes.AdditionalInformationTypeController.onPageLoad(lrn, mode, itemIndex, additionalInformationIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AdditionalInformationNavigatorProvider]).toInstance(fakeAdditionalInformationNavigatorProvider))
      .overrides(bind(classOf[AdditionalInformationService]).toInstance(mockAdditionalInformationService))

  "AdditionalInformationType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockAdditionalInformationService.getAdditionalInformationTypes()(any())).thenReturn(Future.successful(additionalInformationList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, additionalInformationTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalInformationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, additionalInformationList.values, mode, itemIndex, additionalInformationIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockAdditionalInformationService.getAdditionalInformationTypes()(any())).thenReturn(Future.successful(additionalInformationList))
      val userAnswers = emptyUserAnswers.setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), additionalInformation1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, additionalInformationTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> additionalInformation1.value))

      val view = injector.instanceOf[AdditionalInformationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, additionalInformationList.values, mode, itemIndex, additionalInformationIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockAdditionalInformationService.getAdditionalInformationTypes()(any())).thenReturn(Future.successful(additionalInformationList))
      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, additionalInformationTypeRoute)
        .withFormUrlEncodedBody(("value", additionalInformation1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockAdditionalInformationService.getAdditionalInformationTypes()(any())).thenReturn(Future.successful(additionalInformationList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, additionalInformationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalInformationTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, additionalInformationList.values, mode, itemIndex, additionalInformationIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, additionalInformationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, additionalInformationTypeRoute)
        .withFormUrlEncodedBody(("value", additionalInformation1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
