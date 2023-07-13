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
import forms.EnumerableFormProvider
import models.NormalMode
import models.item.TransportMethodOfPayment
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.item.TransportMethodOfPaymentPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.TransportMethodOfPaymentView

import scala.concurrent.Future

class TransportMethodOfPaymentControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                       = new EnumerableFormProvider()
  private val form                               = formProvider[TransportMethodOfPayment]("item.transportMethodOfPayment")
  private val mode                               = NormalMode
  private lazy val transportMethodOfPaymentRoute = routes.TransportMethodOfPaymentController.onPageLoad(lrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))

  "TransportMethodOfPayment Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportMethodOfPaymentRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, TransportMethodOfPayment.values, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(TransportMethodOfPaymentPage(index), TransportMethodOfPayment.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, transportMethodOfPaymentRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> TransportMethodOfPayment.values.head.toString))

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, TransportMethodOfPayment.values, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, transportMethodOfPaymentRoute)
        .withFormUrlEncodedBody(("value", TransportMethodOfPayment.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, transportMethodOfPaymentRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, TransportMethodOfPayment.values, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, transportMethodOfPaymentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, transportMethodOfPaymentRoute)
        .withFormUrlEncodedBody(("value", TransportMethodOfPayment.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
