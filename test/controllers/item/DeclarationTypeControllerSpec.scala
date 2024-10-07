/*
 * Copyright 2024 HM Revenue & Customs
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
import config.TestConstants.{declarationTypeItemValues, declarationTypeValues}
import forms.EnumerableFormProvider
import models.{DeclarationTypeItemLevel, NormalMode}
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.item.DeclarationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DeclarationTypeService
import views.html.item.DeclarationTypeView

import scala.concurrent.Future

class DeclarationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider              = new EnumerableFormProvider()
  private val form                      = formProvider[DeclarationTypeItemLevel]("item.declarationType")(declarationTypeValues)
  private val mode                      = NormalMode
  private lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, mode, itemIndex).url
  private val mockDeclarationService    = mock[DeclarationTypeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))
      .overrides(bind(classOf[DeclarationTypeService]).toInstance(mockDeclarationService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDeclarationService)
    when(mockDeclarationService.getDeclarationTypeItemLevel()(any()))
      .thenReturn(Future.successful(declarationTypeItemValues))
  }

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, declarationTypeItemValues, mode, itemIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage(itemIndex), declarationTypeValues.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> declarationTypeValues.head.code))

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, declarationTypeItemValues, mode, itemIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", declarationTypeValues.head.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[DeclarationTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, declarationTypeItemValues, mode, itemIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", declarationTypeValues.head.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
