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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.requests.DataRequest
import models.{TaskStatus, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.{Result, Results}
import play.api.test.Helpers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DependentTasksActionSpec extends SpecBase with ScalaCheckPropertyChecks with AppWithDefaultMockFixtures with Generators {

  private val dependentTasks = frontendAppConfig.dependentTasks

  def harness(userAnswers: UserAnswers): Future[Result] = {

    lazy val action = new DependentTasksActionImpl()

    action
      .invokeBlock(
        DataRequest(fakeRequest, eoriNumber, userAnswers),
        (_: DataRequest[?]) => Future.successful(Results.Ok)
      )
  }

  "DependentTasksAction" - {

    "return None if dependent sections are completed" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Completed)*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustEqual OK
      redirectLocation(result) must not be defined
    }

    "return None if dependent sections are in error state" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Error)*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustEqual OK
      redirectLocation(result) must not be defined
    }

    "return None if dependent sections are in unavailable state" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Unavailable)*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustEqual OK
      redirectLocation(result) must not be defined
    }

    "return None if dependent sections are in amend state" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Amended)*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustEqual OK
      redirectLocation(result) must not be defined
    }

    "return to task list" - {
      "when all dependent sections are incomplete" in {
        forAll(arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
          taskStatus =>
            val tasks       = Map(dependentTasks.map(_ -> taskStatus)*)
            val userAnswers = emptyUserAnswers.copy(tasks = tasks)
            val result      = harness(userAnswers)
            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual frontendAppConfig.taskListUrl(userAnswers.lrn)
        }
      }

      "when one dependent section is incomplete" in {
        forAll(Gen.oneOf(dependentTasks), arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
          (dependentTask, taskStatus) =>
            val tasks = Map(dependentTasks.map(_ -> TaskStatus.Completed)*)
              .updated(dependentTask, taskStatus)
            val userAnswers = emptyUserAnswers.copy(tasks = tasks)
            val result      = harness(userAnswers)
            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual frontendAppConfig.taskListUrl(userAnswers.lrn)
        }
      }
    }
  }
}
