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

package controllers.item.documents.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.DocumentFormProvider
import generators.Generators
import models.{Document, ItemLevelDocuments, NormalMode, SelectableList, UserAnswers}
import navigation.DocumentNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.{DocumentPage, MandatoryDocumentPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.DocumentsService
import views.html.item.documents.index.{DocumentView, MustAttachPreviousDocumentView, NoDocumentsToAttachView}

import scala.concurrent.Future

class DocumentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val document1    = arbitrary[Document](arbitrarySupportingDocument).sample.value
  private val document2    = arbitrary[Document](arbitraryTransportDocument).sample.value
  private val documentList = SelectableList(Seq(document1, document2))

  private lazy val formProvider  = new DocumentFormProvider()
  private val itemLevelDocuments = ItemLevelDocuments(Nil)

  private lazy val form = formProvider("item.documents.index.document", documentList, itemLevelDocuments)
  private val mode      = NormalMode

  private val mockDocumentsService: DocumentsService = mock[DocumentsService]
  private lazy val documentRoute                     = routes.DocumentController.onPageLoad(lrn, mode, itemIndex, documentIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[DocumentNavigatorProvider]).toInstance(fakeDocumentNavigatorProvider))
      .overrides(bind(classOf[DocumentsService]).toInstance(mockDocumentsService))

  "Document Controller" - {

    "must return OK and the correct view for a GET" - {

      "when previous document required" in {

        when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(true)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, documentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[MustAttachPreviousDocumentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(lrn, itemIndex, documentIndex)(request, messages).toString
      }

      "when documents is empty" in {

        when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(false)

        when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(SelectableList(Nil))

        when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, documentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[NoDocumentsToAttachView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(lrn, itemIndex, documentIndex)(request, messages).toString
      }

      "when documents is non-empty" in {

        when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(false)

        when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(documentList)

        when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, documentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[DocumentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, documentList.values, mode, itemIndex, documentIndex)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(false)

      when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(documentList)

      when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

      val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document1.uuid)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, documentRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> document1.value))

      val view = injector.instanceOf[DocumentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, documentList.values, mode, itemIndex, documentIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(documentList)

      when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

      when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(false)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, documentRoute)
        .withFormUrlEncodedBody(("value", document1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(MandatoryDocumentPage(itemIndex, documentIndex)).value mustEqual false
    }

    "must redirect to the next page when required previous document is submitted" in {

      val document     = arbitrary[Document](arbitraryPreviousDocument).sample.value
      val documentList = SelectableList(Seq(document))

      when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(documentList)

      when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

      when(mockDocumentsService.isPreviousDocumentRequired(any(), any())).thenReturn(true)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, documentRoute)
        .withFormUrlEncodedBody(("value", document.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(MandatoryDocumentPage(itemIndex, documentIndex)).value mustEqual true
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockDocumentsService.getDocuments(any(), any(), any())).thenReturn(documentList)

      when(mockDocumentsService.getItemLevelDocuments(any(), any(), any())).thenReturn(itemLevelDocuments)

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, documentRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[DocumentView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, documentList.values, mode, itemIndex, documentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, documentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, documentRoute)
        .withFormUrlEncodedBody(("value", document1.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual frontendAppConfig.sessionExpiredUrl(lrn)
    }
  }
}
