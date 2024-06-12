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
import controllers.item.packages.{routes => packageRoutes}
import forms.YesNoFormProvider
import generators.Generators
import models.reference.PackageType
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.item.packages.index.{NumberOfPackagesPage, PackageTypePage}
import pages.sections.packages.PackageSection
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.item.packages.index.RemovePackageView

import scala.concurrent.Future

class RemovePackageControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with Generators {

  private lazy val removePackageRoute = routes.RemovePackageController.onPageLoad(lrn, mode, itemIndex, packageIndex).url
  private val formProvider            = new YesNoFormProvider()
  private val mode                    = NormalMode
  private val packageType             = arbitrary[PackageType].sample.value

  private def form(packageType: PackageType): Form[Boolean] =
    formProvider("item.packages.index.removePackage", packageType)

  "RemovePackage Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(positiveInts) {
        quantity =>
          val userAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)
          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(GET, removePackageRoute)
          val result  = route(app, request).value

          val view = injector.instanceOf[RemovePackageView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(packageType), lrn, mode, itemIndex, packageIndex, packageType, Some(s"$quantity ${packageType.toString}"))(request, messages).toString
      }
    }

    "when yes submitted" - {
      "must redirect to add another package and remove package at specified index" in {

        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removePackageRoute)
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
      "must redirect to add another package and not remove package at specified index" in {
        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

        setExistingUserAnswers(userAnswers)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removePackageRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          packageRoutes.AddAnotherPackageController.onPageLoad(userAnswers.lrn, mode, itemIndex).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      forAll(positiveInts) {
        quantity =>
          val userAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)

          setExistingUserAnswers(userAnswers)

          val request   = FakeRequest(POST, removePackageRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form(packageType).bind(Map("value" -> ""))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST

          val view = injector.instanceOf[RemovePackageView]

          contentAsString(result) mustEqual
            view(boundForm, lrn, mode, itemIndex, packageIndex, packageType, Some(s"${quantity.toString} ${packageType.toString}"))(request, messages).toString
      }
    }

    "must redirect for a GET" - {
      "if no existing data found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removePackageRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
      }

      "if no package is found" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, removePackageRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          packageRoutes.AddAnotherPackageController.onPageLoad(lrn, mode, itemIndex).url
      }
    }

    "must redirect for a POST" - {
      "if no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removePackageRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
      }

      "if no package is found" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, removePackageRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          packageRoutes.AddAnotherPackageController.onPageLoad(lrn, mode, itemIndex).url
      }
    }
  }
}
