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
import forms.item.CombinedNomenclatureCode
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.item.CombinedNomenclatureCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.CombinedNomenclatureCodeView

import scala.concurrent.Future

class CombinedNomenclatureCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                       = new CombinedNomenclatureCode()
  private val form                               = formProvider("item.combinedNomenclatureCode")
  private val mode                               = NormalMode
  private lazy val combinedNomenclatureCodeRoute = routes.CombinedNomenclatureCodeController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))

  "CombinedNomenclatureCode Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, combinedNomenclatureCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CombinedNomenclatureCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(CombinedNomenclatureCodePage, "test string")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, combinedNomenclatureCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "test string"))

      val view = injector.instanceOf[CombinedNomenclatureCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, combinedNomenclatureCodeRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, combinedNomenclatureCodeRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[CombinedNomenclatureCodeView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, combinedNomenclatureCodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, combinedNomenclatureCodeRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}