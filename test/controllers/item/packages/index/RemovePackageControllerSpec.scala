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

import controllers.item.packages.{routes => packageRoutes}
import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.reference.PackageType
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import pages.item.packages.index.PackageTypePage
import pages.sections.packages.PackageSection
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.packages.index.RemovePackageView

import scala.concurrent.Future

class RemovePackageControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with Generators {

  private val formProvider = new YesNoFormProvider()

  private def form(packageType: PackageType): Form[Boolean] =
    formProvider("item.packages.index.removePackage", packageType)

  private val mode                     = NormalMode
  private lazy val removeDocumentRoute = routes.RemovePackageController.onPageLoad(lrn, mode, itemIndex, packageIndex).url
  private val packageType              = arbitrary[PackageType].sample.value

  "RemoveDocument Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeDocumentRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemovePackageView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(packageType), lrn, mode, itemIndex, packageIndex, packageType)(request, messages).toString
    }

    "when yes submitted" - {
      "must redirect to add another document and remove document at specified index" in {

        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          packageRoutes.AddAnotherPackageController.onPageLoad(userAnswers.lrn, mode, itemIndex).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())

        userAnswersCaptor.getValue.get(PackageSection(itemIndex, packageIndex)) mustNot be(defined)
      }
    }

    "when no submitted" - {
      "must redirect to add another document and not remove document at specified index" in {
        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

        setExistingUserAnswers(userAnswers)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          packageRoutes.AddAnotherPackageController.onPageLoad(userAnswers.lrn, mode, itemIndex).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, removeDocumentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(packageType).bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemovePackageView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, packageIndex, packageType)(request, messages).toString
    }

    "must redirect to Session Expired for a GET" - {
      "if no existing data found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeDocumentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no authorisation number is found" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, removeDocumentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }
    }

    "must redirect to Session Expired for a POST" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeDocumentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
      }

      "if no document is found" in {

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
