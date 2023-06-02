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

package controllers.item.documents

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import generators.Generators
import models.{Index, NormalMode, UserAnswers}
import navigation.ItemNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import pages.item.AddDocumentsYesNoPage
import pages.item.documents.DocumentsInProgressPage
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.item.documents.AddAnotherDocumentViewModel
import viewmodels.item.documents.AddAnotherDocumentViewModel.AddAnotherDocumentViewModelProvider
import views.html.item.documents.AddAnotherDocumentView

class AddAnotherDocumentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with MockitoSugar {

  private val formProvider = new AddAnotherFormProvider()

  private def form(viewModel: AddAnotherDocumentViewModel): Form[Boolean] =
    formProvider(viewModel.prefix)

  private val mode                         = NormalMode
  private lazy val addAnotherDocumentRoute = routes.AddAnotherDocumentController.onPageLoad(lrn, mode, itemIndex).url

  private val mockViewModelProvider = mock[AddAnotherDocumentViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherDocumentViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[ItemNavigatorProvider]).toInstance(fakeItemNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val viewModel = arbitrary[AddAnotherDocumentViewModel].sample.value

  private val emptyViewModel       = viewModel.copy(listItems = Nil)
  private val notMaxedOutViewModel = viewModel.copy(allowMore = true)
  private val maxedOutViewModel    = viewModel.copy(allowMore = false)

  "AddAnotherDocument Controller" - {

    "must redirect to next page when 0 document added" in {
      when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
        .thenReturn(emptyViewModel)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, addAnotherDocumentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(AddDocumentsYesNoPage(itemIndex)) must not be defined
      userAnswersCaptor.getValue.get(DocumentsInProgressPage(itemIndex)) must not be defined
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
          .thenReturn(notMaxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherDocumentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherDocumentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(notMaxedOutViewModel), lrn, notMaxedOutViewModel, itemIndex)(request, messages).toString
      }

      "when max limit reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
          .thenReturn(maxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherDocumentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherDocumentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(maxedOutViewModel), lrn, maxedOutViewModel, itemIndex)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to Document page at next index" in {
          when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
            .thenReturn(notMaxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherDocumentRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.item.documents.index.routes.DocumentController.onPageLoad(lrn, mode, itemIndex, Index(viewModel.count)).url
        }
      }

      "when no submitted" - {
        "must redirect to next page and set DocumentsInProgressPage to false" in {
          when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
            .thenReturn(notMaxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherDocumentRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
          userAnswersCaptor.getValue.get(DocumentsInProgressPage(itemIndex)).value mustBe false
        }
      }
    }

    "when max limit reached" - {
      "must redirect to next page" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
          .thenReturn(maxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "when can't attach any more documents to item" - {
      "must redirect to next page" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
          .thenReturn(notMaxedOutViewModel.copy(documents = Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any(), any()))
          .thenReturn(notMaxedOutViewModel)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherDocumentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(notMaxedOutViewModel).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherDocumentView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, notMaxedOutViewModel, itemIndex)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherDocumentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherDocumentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl
    }

  }
}
