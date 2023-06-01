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
import models.{NormalMode, SelectableList, TransportEquipment}
import navigation.ItemNavigatorProvider
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.item.TransportEquipmentPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TransportEquipmentService
import views.html.item.TransportEquipmentView

import scala.concurrent.Future

class TransportEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val transportEquipment1    = arbitrary[TransportEquipment].sample.value
  private val transportEquipment2    = arbitrary[TransportEquipment].sample.value
  private val transportEquipmentList = SelectableList(Seq(transportEquipment1, transportEquipment2))

  private val formProvider = new SelectableFormProvider()
  private val form         = formProvider("item.transportEquipment", transportEquipmentList)
  private val mode         = NormalMode

  private val mockTransportEquipmentService: TransportEquipmentService = mock[TransportEquipmentService]
  private lazy val transportEquipmentRoute                             = routes.TransportEquipmentController.onPageLoad(lrn, mode, itemIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[TransportEquipmentService]).toInstance(mockTransportEquipmentService))

  "TransportEquipment Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockTransportEquipmentService.getTransportEquipments(any())).thenReturn(Some(transportEquipmentList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportEquipmentRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportEquipmentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, transportEquipmentList.values, mode, itemIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockTransportEquipmentService.getTransportEquipments(any())).thenReturn(Some(transportEquipmentList))
      val userAnswers = emptyUserAnswers.setValue(TransportEquipmentPage(itemIndex), transportEquipment1.number)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, transportEquipmentRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> transportEquipment1.value))

      val view = injector.instanceOf[TransportEquipmentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, transportEquipmentList.values, mode, itemIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockTransportEquipmentService.getTransportEquipments(any())).thenReturn(Some(transportEquipmentList))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, transportEquipmentRoute)
        .withFormUrlEncodedBody(("value", transportEquipment1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockTransportEquipmentService.getTransportEquipments(any())).thenReturn(Some(transportEquipmentList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, transportEquipmentRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[TransportEquipmentView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, transportEquipmentList.values, mode, itemIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, transportEquipmentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, transportEquipmentRoute)
        .withFormUrlEncodedBody(("value", transportEquipment1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
