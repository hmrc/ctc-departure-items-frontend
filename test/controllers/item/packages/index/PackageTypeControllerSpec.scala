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

package controllers.item.packages.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider.PackageFormProvider
import generators.Generators
import models.{NormalMode, SelectableList}
import navigation.PackageNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.item.packages.index.PackageTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PackagesService
import views.html.item.packages.index.PackageTypeView

import scala.concurrent.Future

class PackageTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val packageType1    = arbitraryPackageType.arbitrary.sample.get
  private val packageType2    = arbitraryPackageType.arbitrary.sample.get
  private val packageTypeList = SelectableList(Seq(packageType1, packageType2))

  private val formProvider = new PackageFormProvider()
  private val field        = formProvider.field
  private val form         = formProvider("item.packages.index.packageType", packageTypeList)
  private val mode         = NormalMode

  private val mockPackagesService: PackagesService = mock[PackagesService]
  private lazy val packageTypeRoute                = routes.PackageTypeController.onPageLoad(lrn, mode, itemIndex, packageIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PackageNavigatorProvider]).toInstance(fakePackageNavigatorProvider))
      .overrides(bind(classOf[PackagesService]).toInstance(mockPackagesService))

  "PackageType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockPackagesService.getPackageTypes()(any())).thenReturn(Future.successful(packageTypeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, packageTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[PackageTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, packageTypeList.values, mode, itemIndex, packageIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockPackagesService.getPackageTypes()(any())).thenReturn(Future.successful(packageTypeList))
      val userAnswers = emptyUserAnswers.setValue(PackageTypePage(itemIndex, packageIndex), packageType1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, packageTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map(field -> packageType1.code))

      val view = injector.instanceOf[PackageTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, packageTypeList.values, mode, itemIndex, packageIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockPackagesService.getPackageTypes()(any())).thenReturn(Future.successful(packageTypeList))
      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, packageTypeRoute)
        .withFormUrlEncodedBody((field, packageType1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockPackagesService.getPackageTypes()(any())).thenReturn(Future.successful(packageTypeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, packageTypeRoute).withFormUrlEncodedBody((field, "invalid value"))
      val boundForm = form.bind(Map(field -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[PackageTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, packageTypeList.values, mode, itemIndex, packageIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, packageTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, packageTypeRoute)
        .withFormUrlEncodedBody((field, packageType1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
