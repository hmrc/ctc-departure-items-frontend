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
import controllers.item.additionalReference.{routes => additionalReferenceRoutes}
import forms.YesNoFormProvider
import generators.Generators
import models.{Index, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.additionalReference.index.RemoveAdditionalReferenceView

class RemoveAdditionalReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                          = new YesNoFormProvider()
  private def form(additionalReferenceIndex: Index) = formProvider("item.additionalReference.index.removeAdditionalReference", additionalReferenceIndex.display)
  private val mode                                  = NormalMode
  private lazy val removeAdditionalReferenceRoute   = routes.RemoveAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex, additionalReferenceIndex).url

  "RemoveAdditionalReference Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeAdditionalReferenceRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveAdditionalReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(additionalReferenceIndex), lrn, mode, itemIndex, additionalReferenceIndex)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual additionalReferenceRoutes.AddAnotherAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())

        userAnswersCaptor.getValue.get(AdditionalReferenceSection(itemIndex, additionalReferenceIndex)) mustNot be(defined)
      }

      "when no is submitted" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeAdditionalReferenceRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual additionalReferenceRoutes.AddAnotherAdditionalReferenceController.onPageLoad(lrn, mode, itemIndex).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeAdditionalReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(additionalReferenceIndex).bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveAdditionalReferenceView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, additionalReferenceIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" - {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeAdditionalReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl

    }

    "must redirect to Session Expired for a POST if no existing data is found" - {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeAdditionalReferenceRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl

    }
  }
}
