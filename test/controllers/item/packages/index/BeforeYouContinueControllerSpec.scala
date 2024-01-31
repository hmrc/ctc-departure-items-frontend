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

package controllers.item.packages.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.{Mode, UserAnswers}
import navigation.PackageNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalacheck.Arbitrary.arbitrary
import pages.item.packages.index.BeforeYouContinuePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.packages.index.BeforeYouContinueView

class BeforeYouContinueControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mode = arbitrary[Mode].sample.value

  private lazy val beforeYouContinueRoute = routes.BeforeYouContinueController.onPageLoad(lrn, mode, itemIndex, packageIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PackageNavigatorProvider]).toInstance(fakePackageNavigatorProvider))

  "BeforeYouContinue Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[BeforeYouContinueView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, mode, itemIndex, packageIndex)(request, messages).toString
    }

    "must redirect to the next page for a POST" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, beforeYouContinueRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(BeforeYouContinuePage(itemIndex, packageIndex)).value mustBe true
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, beforeYouContinueRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, beforeYouContinueRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }
  }
}
