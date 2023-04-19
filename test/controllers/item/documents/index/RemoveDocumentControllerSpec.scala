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

package controllers.item.documents.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{Document, NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import org.scalacheck.Arbitrary.arbitrary
import pages.item.documents.index.DocumentPage
import pages.sections.documents.DocumentSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.documents.index.RemoveDocumentView

class RemoveDocumentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val document                 = arbitrary[Document].sample.value
  private val formProvider             = new YesNoFormProvider()
  private val form                     = formProvider("item.documents.index.removeDocument")
  private val mode                     = NormalMode
  private lazy val removeDocumentRoute = routes.RemoveDocumentController.onPageLoad(lrn, mode, itemIndex, documentIndex).url

  "RemoveDocument Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeDocumentRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveDocumentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, documentIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeDocumentRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "true"))

      val view = injector.instanceOf[RemoveDocumentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, documentIndex)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#" //TODO: update to add another page

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(DocumentSection(itemIndex, documentIndex)) mustNot be(defined)
      }

      "when no is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#" //TODO: update to add another page

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeDocumentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveDocumentView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, documentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeDocumentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no document is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, removeDocumentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }

    "must redirect to Session Expired for a POST" - {
      "if no document is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no existing data is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }
  }
}
