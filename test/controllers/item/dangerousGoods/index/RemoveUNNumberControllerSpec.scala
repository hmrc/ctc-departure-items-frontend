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

package controllers.item.dangerousGoods.index

import controllers.item.dangerousGoods.{routes => dangerousGoodsRoutes}
import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import pages.item.dangerousGoods.index.UNNumberPage
import pages.sections.dangerousGoods.DangerousGoodsSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.dangerousGoods.index.RemoveUNNumberView

class RemoveUNNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val UNNumber                 = nonEmptyString.sample.value
  private val formProvider             = new YesNoFormProvider()
  private val form                     = formProvider("item.dangerousGoods.index.removeUNNumber", UNNumber)
  private val mode                     = NormalMode
  private lazy val removeUNNumberRoute = routes.RemoveUNNumberController.onPageLoad(lrn, mode, itemIndex, dangerousGoodsIndex).url

  "RemoveUNNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), UNNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeUNNumberRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveUNNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, dangerousGoodsIndex, UNNumber)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), UNNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeUNNumberRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          dangerousGoodsRoutes.AddAnotherDangerousGoodsController.onPageLoad(userAnswers.lrn, mode, itemIndex).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(DangerousGoodsSection(itemIndex, dangerousGoodsIndex)) mustNot be(defined)
      }

      "when no is submitted" in {

        val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), UNNumber)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeUNNumberRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          dangerousGoodsRoutes.AddAnotherDangerousGoodsController.onPageLoad(userAnswers.lrn, mode, itemIndex).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), UNNumber)
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, removeUNNumberRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveUNNumberView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, dangerousGoodsIndex, UNNumber)(request, messages).toString
    }

    "must redirect to Session Expired for a GET" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeUNNumberRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no item number is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, removeUNNumberRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }

    "must redirect to Session Expired for a POST" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeUNNumberRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no item number is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removeUNNumberRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }
  }
}
