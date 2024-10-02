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
import forms.item.CUSCodeFormProvider
import models.NormalMode
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import pages.item.CustomsUnionAndStatisticsCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CUSCodeService
import views.html.item.CustomsUnionAndStatisticsCodeView

import scala.concurrent.Future

class CustomsUnionAndStatisticsCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                            = new CUSCodeFormProvider()
  private val form                                    = formProvider("item.customsUnionAndStatisticsCode")
  private val mode                                    = NormalMode
  private lazy val customsUnionAndStatisticsCodeRoute = routes.CustomsUnionAndStatisticsCodeController.onPageLoad(lrn, mode, itemIndex).url
  private val cusCodeServiceMock: CUSCodeService      = mock[CUSCodeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[CUSCodeService]).toInstance(cusCodeServiceMock))

  "CustomsUnionAndStatisticsCode Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, customsUnionAndStatisticsCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsUnionAndStatisticsCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(CustomsUnionAndStatisticsCodePage(itemIndex), "validCode")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, customsUnionAndStatisticsCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "validCode"))

      val view = injector.instanceOf[CustomsUnionAndStatisticsCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(cusCodeServiceMock.doesCUSCodeExist(anyString())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, customsUnionAndStatisticsCodeRoute)
        .withFormUrlEncodedBody(("value", "validCode"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, customsUnionAndStatisticsCodeRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[CustomsUnionAndStatisticsCodeView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, customsUnionAndStatisticsCodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, customsUnionAndStatisticsCodeRoute)
        .withFormUrlEncodedBody(("value", "validCode"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
