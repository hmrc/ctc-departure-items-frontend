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
import controllers.item.supplyChainActors.{routes => supplyChainActorRoutes}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import models.reference.SupplyChainActorType
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import pages.sections.supplyChainActors.SupplyChainActorSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.supplyChainActors.index.RemoveSupplyChainActorView

import scala.concurrent.Future

class RemoveSupplyChainActorControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val supplyChainActorTypes = arbitrary[Seq[SupplyChainActorType]].sample.value

  private val form = new YesNoFormProvider()("item.supplyChainActors.index.removeSupplyChainActor")

  private val mode = NormalMode

  private val identificationNumber = "12345"

  private lazy val removeSupplyChainActorRoute = routes.RemoveSupplyChainActorController.onPageLoad(lrn, mode, itemIndex, actorIndex).url

  "RemoveSupplyChainActor" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActorTypes.head)
        .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeSupplyChainActorRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveSupplyChainActorView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, actorIndex)(request, messages).toString
    }

    "when yes submitted" - {
      "must redirect to add another supply chain actor and remove supply chain actor at specified index" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActorTypes.head)
          .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)

        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeSupplyChainActorRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          supplyChainActorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode, itemIndex).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(SupplyChainActorSection(itemIndex, actorIndex)) mustNot be(defined)
      }
    }

    "when no submitted" - {
      "must redirect to add another supply chain actor and not remove supply chain actor at specified index" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActorTypes.head)
          .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)
        reset(mockSessionRepository)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeSupplyChainActorRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          supplyChainActorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode, itemIndex).url

        verify(mockSessionRepository, never()).set(any())(any())

      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers
        .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), supplyChainActorTypes.head)
        .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, removeSupplyChainActorRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveSupplyChainActorView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, actorIndex)(request, messages).toString
    }
  }

  "must redirect for a GET" - {
    "when no existing data found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeSupplyChainActorRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "when no supply chain actor found" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeSupplyChainActorRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        supplyChainActorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode, itemIndex).url
    }
  }

  "must redirect for a POST" - {
    "when no existing data found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeSupplyChainActorRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "when no supply chain actor found" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, removeSupplyChainActorRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        supplyChainActorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode, itemIndex).url
    }
  }
}
