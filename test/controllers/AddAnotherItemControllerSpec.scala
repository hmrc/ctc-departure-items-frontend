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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import generators.Generators
import models.NormalMode
import navigation.ItemsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddAnotherItemViewModel.AddAnotherItemViewModelProvider
import viewmodels.{AddAnotherItemViewModel, ListItem}
import views.html.AddAnotherItemView

class AddAnotherItemControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider = new AddAnotherFormProvider()

  private def form(viewModel: AddAnotherItemViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMore(frontendAppConfig))

  private val mode = NormalMode

  private val mockViewModelProvider = mock[AddAnotherItemViewModelProvider]

  private lazy val addAnotherItemRoute = routes.AddAnotherItemController.onPageLoad(lrn).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherItemViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[ItemsNavigatorProvider]).toInstance(fakeItemsNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxItems - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxItems)(listItem)

  private val viewModel = arbitrary[AddAnotherItemViewModel].sample.value

  private val emptyViewModel       = viewModel.copy(listItems = Nil)
  private val notMaxedOutViewModel = viewModel.copy(listItems = listItems)
  private val maxedOutViewModel    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherItem Controller" - {

    "must redirect to item description page when 0 item added" in {
      when(mockViewModelProvider.apply(any(), any())(any(), any()))
        .thenReturn(emptyViewModel)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, addAnotherItemRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.item.routes.DescriptionController.onPageLoad(lrn, mode, itemIndex).url
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        when(mockViewModelProvider.apply(any(), any())(any(), any()))
          .thenReturn(notMaxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherItemRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherItemView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(notMaxedOutViewModel), lrn, notMaxedOutViewModel)(request, messages, frontendAppConfig).toString
      }

      "when max limit reached" in {

        when(mockViewModelProvider.apply(any(), any())(any(), any()))
          .thenReturn(maxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherItemRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherItemView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(maxedOutViewModel), lrn, maxedOutViewModel)(request, messages, frontendAppConfig).toString
      }
    }
  }
}
