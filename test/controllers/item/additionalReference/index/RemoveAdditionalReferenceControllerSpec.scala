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

package controllers.item.additionalReference.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.reference.AdditionalReference
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import org.scalacheck.Arbitrary.arbitrary
import pages.item.additionalReference.index.AdditionalReferencePage
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.additionalReference.index.RemoveAdditionalReferenceView

class RemoveAdditionalReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                                   = new YesNoFormProvider()
  private def form(additionalReference: AdditionalReference) = formProvider("item.additionalReference.index.removeAdditionalReference", additionalReference)

  private val additionalReference                 = arbitrary[AdditionalReference].sample.value
  private val mode                                = NormalMode
  private lazy val removeAdditionalReferenceRoute = routes.RemoveAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex, additionalReferenceIndex).url

  "RemoveAdditionalReference Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeAdditionalReferenceRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveAdditionalReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(additionalReference), lrn, mode, itemIndex, additionalReferenceIndex, additionalReference)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual Call("GET", "#").url // TODO: redirect to AddAnother route

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())

        userAnswersCaptor.getValue.get(AdditionalReferenceSection(itemIndex, additionalReferenceIndex)) mustNot be(defined)
      }

      "when no is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual Call("GET", "#").url // TODO: redirect to AddAnother route

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, removeAdditionalReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(additionalReference).bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveAdditionalReferenceView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, additionalReferenceIndex, additionalReference)(request, messages).toString
    }

    "must redirect to Session Expired for a GET" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeAdditionalReferenceRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no document is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, removeAdditionalReferenceRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }

    "must redirect to Session Expired for a POST" - {
      "if no document is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no existing data is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }
  }
}
