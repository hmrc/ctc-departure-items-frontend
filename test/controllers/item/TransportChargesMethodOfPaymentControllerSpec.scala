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
import generators.Generators
import models.NormalMode
import models.reference.TransportChargesMethodOfPayment
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.item.TransportChargesMethodOfPaymentPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TransportChargesMethodOfPaymentService
import views.html.item.TransportMethodOfPaymentView

import scala.concurrent.Future

class TransportChargesMethodOfPaymentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mops                               = arbitrary[Seq[TransportChargesMethodOfPayment]].sample.value
  private val mop1                               = mops.head
  private val formProvider                       = new EnumerableFormProvider()
  private val form                               = formProvider("item.transportMethodOfPayment", mops)
  private val mode                               = NormalMode
  private lazy val transportMethodOfPaymentRoute = routes.TransportChargesMethodOfPaymentController.onPageLoad(lrn, mode, index).url

  private val mockMethodOfPaymentService: TransportChargesMethodOfPaymentService = mock[TransportChargesMethodOfPaymentService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[TransportChargesMethodOfPaymentService]).toInstance(mockMethodOfPaymentService))

  "TransportMethodOfPayment Controller" - {

    "must return OK and the correct view for a GET" in {
      when(mockMethodOfPaymentService.getTransportChargesMethodOfPaymentTypes()(any())).thenReturn(Future.successful(mops))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportMethodOfPaymentRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mops, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockMethodOfPaymentService.getTransportChargesMethodOfPaymentTypes()(any())).thenReturn(Future.successful(mops))

      val userAnswers = emptyUserAnswers.setValue(TransportChargesMethodOfPaymentPage(index), mop1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, transportMethodOfPaymentRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> mop1.code))

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mops, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockMethodOfPaymentService.getTransportChargesMethodOfPaymentTypes()(any())).thenReturn(Future.successful(mops))

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, transportMethodOfPaymentRoute)
        .withFormUrlEncodedBody(("value", mop1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)
      when(mockMethodOfPaymentService.getTransportChargesMethodOfPaymentTypes()(any())).thenReturn(Future.successful(mops))

      val request   = FakeRequest(POST, transportMethodOfPaymentRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[TransportMethodOfPaymentView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, mops, mode, index)(request, messages).toString
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
        .withFormUrlEncodedBody(("value", mop1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
