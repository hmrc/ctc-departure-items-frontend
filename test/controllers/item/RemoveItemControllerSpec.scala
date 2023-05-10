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
import controllers.{routes => itemRoutes}
import forms.YesNoFormProvider
import models.{Index, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import pages.sections.ItemSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.RemoveItemView

class RemoveItemControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider           = new YesNoFormProvider()
  private def form(itemIndex: Index) = formProvider("item.removeItem", itemIndex.display)

  private lazy val removeItemRoute = routes.RemoveItemController.onPageLoad(lrn, itemIndex).url

  "RemoveItem Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeItemRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveItemView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(itemIndex), lrn, itemIndex)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeItemRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          itemRoutes.AddAnotherItemController.onPageLoad(lrn).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())

        userAnswersCaptor.getValue.get(ItemSection(itemIndex)) mustNot be(defined)
      }

      "when no is submitted" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeItemRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          itemRoutes.AddAnotherItemController.onPageLoad(lrn).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeItemRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(itemIndex).bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveItemView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, itemIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeItemRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }

    "must redirect to Session Expired for a POST" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeItemRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }
  }
}
