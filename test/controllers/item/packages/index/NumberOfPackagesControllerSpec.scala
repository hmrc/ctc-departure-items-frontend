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
import forms.IntFormProvider
import generators.Generators
import models.PackingType.Unpacked
import models.reference.PackageType
import models.{NormalMode, PackingType}
import navigation.PackageNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import pages.item.packages.index.{NumberOfPackagesPage, PackageTypePage}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.packages.index.NumberOfPackagesView

import scala.concurrent.Future

class NumberOfPackagesControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val packingType = Gen.oneOf(PackingType.values).retryUntil(_ != Unpacked).sample.value
  private val packageType = PackageType("code", "description", packingType)

  private def formProvider(minimum: Int) =
    new IntFormProvider().apply("item.packages.index.numberOfPackages", phaseConfig.maxNumberOfPackages, minimum, Seq(packageType.toString))
  private val form                       = formProvider(0)
  private val unpackedForm               = formProvider(1)
  private val mode                       = NormalMode
  private val validAnswer                = 1
  private lazy val numberOfPackagesRoute = routes.NumberOfPackagesController.onPageLoad(lrn, mode, itemIndex, packageIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PackageNavigatorProvider]).toInstance(fakePackageNavigatorProvider))

  "NumberOfPackages Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, numberOfPackagesRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[NumberOfPackagesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, packageIndex, packageType.toString)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
        .setValue(NumberOfPackagesPage(itemIndex, packageIndex), validAnswer)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, numberOfPackagesRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAnswer.toString))

      val view = injector.instanceOf[NumberOfPackagesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, packageIndex, packageType.toString)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, numberOfPackagesRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "when 0 is submitted" - {
      "and in transition" - {
        val app = transitionApplicationBuilder().build()
        "must redirect to next page" in {
          running(app) {
            val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

            setExistingUserAnswers(userAnswers)

            when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

            val request = FakeRequest(POST, numberOfPackagesRoute)
              .withFormUrlEncodedBody(("value", "0"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual onwardRoute.url
          }
        }
      }
      "and in post-transition" - {
        def app: Application = postTransitionApplicationBuilder().build()
        "and package type is unpacked" - {
          "must return Bad Request" in {
            running(app) {
              val packageType = PackageType("Unpacked", "Unpacked", Unpacked)
              val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

              setExistingUserAnswers(userAnswers)

              val request    = FakeRequest(POST, numberOfPackagesRoute).withFormUrlEncodedBody(("value", "0"))
              val filledForm = unpackedForm.bind(Map("value" -> "0"))

              val result = route(app, request).value

              status(result) mustEqual BAD_REQUEST

              val view = injector.instanceOf[NumberOfPackagesView]

              contentAsString(result) mustEqual
                view(filledForm, lrn, mode, itemIndex, packageIndex, packageType.toString)(request, messages).toString
            }
          }
        }

        "and package type is not unpacked" - {
          "must redirect to next page" in {
            running(app) {
              val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

              setExistingUserAnswers(userAnswers)

              when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

              val request = FakeRequest(POST, numberOfPackagesRoute)
                .withFormUrlEncodedBody(("value", "0"))

              val result = route(app, request).value

              status(result) mustEqual SEE_OTHER

              redirectLocation(result).value mustEqual onwardRoute.url
            }
          }
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType)

      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, numberOfPackagesRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[NumberOfPackagesView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, packageIndex, packageType.toString)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, numberOfPackagesRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, numberOfPackagesRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
