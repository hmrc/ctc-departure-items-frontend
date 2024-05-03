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

package controllers.item.supplyChainActors.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EoriNumberFormProvider
import generators.Generators
import models.NormalMode
import models.reference.SupplyChainActorType
import navigation.SupplyChainActorNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.supplyChainActors.index.IdentificationNumberView

import scala.concurrent.Future

class IdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                   = new EoriNumberFormProvider()
  private val validAnswer                    = "testString"
  private val form                           = formProvider("item.supplyChainActors.index.identificationNumber")
  private val mode                           = NormalMode
  private lazy val identificationNumberRoute = routes.IdentificationNumberController.onPageLoad(lrn, mode, itemIndex, actorIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind(classOf[SupplyChainActorNavigatorProvider]).toInstance(fakeSupplyChainActorNavigatorProvider)
      )

  "IdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val supplyChainActor = arbitrary[SupplyChainActorType].sample.value

      val updatedUserAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActor)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, identificationNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, actorIndex, supplyChainActor.toString.toLowerCase)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val supplyChainActor = arbitrary[SupplyChainActorType].sample.value

      val updatedUserAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActor)
        .setValue(IdentificationNumberPage(itemIndex, actorIndex), validAnswer)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, identificationNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAnswer))

      val view = injector.instanceOf[IdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, actorIndex, supplyChainActor.toString.toLowerCase)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val updatedUserAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), arbitrary[SupplyChainActorType].sample.value)

      setExistingUserAnswers(updatedUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, identificationNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val supplyChainActor = arbitrary[SupplyChainActorType].sample.value

      val updatedUserAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActor)

      setExistingUserAnswers(updatedUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, identificationNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[IdentificationNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, actorIndex, supplyChainActor.toString.toLowerCase)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, identificationNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, identificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
