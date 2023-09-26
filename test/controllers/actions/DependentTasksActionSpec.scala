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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.requests.DataRequest
import models.{TaskStatus, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.{Result, Results}
import play.api.test.Helpers._

import scala.concurrent.Future

class DependentTasksActionSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val dependentTasks = frontendAppConfig.dependentTasks

  def harness(userAnswers: UserAnswers): Future[Result] = {

    lazy val action = app.injector.instanceOf[DependentTasksAction]

    action
      .invokeBlock(
        DataRequest(fakeRequest, eoriNumber, userAnswers),
        {
          _: DataRequest[_] =>
            Future.successful(Results.Ok)
        }
      )
  }

  "DependentTasksAction" - {

    "return None if dependent sections are completed" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Completed): _*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return None if dependent sections are in error state" in {
      val tasks       = Map(dependentTasks.map(_ -> TaskStatus.Error): _*)
      val userAnswers = emptyUserAnswers.copy(tasks = tasks)
      val result      = harness(userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return to task list" - {
      "when all dependent sections are incomplete" in {
        forAll(arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
          taskStatus =>
            val tasks       = Map(dependentTasks.map(_ -> taskStatus): _*)
            val userAnswers = emptyUserAnswers.copy(tasks = tasks)
            val result      = harness(userAnswers)
            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe frontendAppConfig.taskListUrl(userAnswers.lrn)
        }
      }

      "when one dependent section is incomplete" in {
        forAll(Gen.oneOf(dependentTasks), arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
          (dependentTask, taskStatus) =>
            val tasks = Map(dependentTasks.map(_ -> TaskStatus.Completed): _*)
              .updated(dependentTask, taskStatus)
            val userAnswers = emptyUserAnswers.copy(tasks = tasks)
            val result      = harness(userAnswers)
            status(result) mustBe SEE_OTHER
            redirectLocation(result).value mustBe frontendAppConfig.taskListUrl(userAnswers.lrn)
        }
      }
    }
  }
}
