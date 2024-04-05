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
import models.{TaskStatus, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

class UpdateTaskControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val continueUrl = RedirectUrl("http://localhost:10130/foo")
  private val task        = ".items"

  private lazy val updateTaskRoute = routes.UpdateTaskController.updateTask(lrn, continueUrl).url

  "Update Task Controller" - {

    "must update task and redirect" - {
      "when task status is undefined" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, updateTaskRoute)
        val result  = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual continueUrl.unsafeValue

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.tasks.get(task) must not be defined
      }

      "when task status is CannotStartYet" in {
        val userAnswers = emptyUserAnswers.copy(tasks = Map(task -> TaskStatus.CannotStartYet))
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, updateTaskRoute)
        val result  = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual continueUrl.unsafeValue

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.tasks.get(task).value mustBe TaskStatus.CannotStartYet
      }

      "when task status is NotStarted" in {
        val userAnswers = emptyUserAnswers.copy(tasks = Map(task -> TaskStatus.NotStarted))
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, updateTaskRoute)
        val result  = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual continueUrl.unsafeValue

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.tasks.get(task).value mustBe TaskStatus.NotStarted
      }

      "when task status is InProgress" in {
        val userAnswers = emptyUserAnswers.copy(tasks = Map(task -> TaskStatus.InProgress))
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, updateTaskRoute)
        val result  = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual continueUrl.unsafeValue

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.tasks.get(task).value mustBe TaskStatus.InProgress
      }

      "when task status is Completed" in {
        val userAnswers = emptyUserAnswers.copy(tasks = Map(task -> TaskStatus.Completed))
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, updateTaskRoute)
        val result  = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual continueUrl.unsafeValue

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.tasks.get(task).value mustBe TaskStatus.InProgress
      }
    }
  }
}
