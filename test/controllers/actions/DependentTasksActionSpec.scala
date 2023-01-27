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

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.TaskStatus
import models.requests.DataRequest
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DependentTasksActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  private class Harness extends DependentTasksActionImpl() {

    def callRefine(tasks: Map[String, TaskStatus]): Future[Either[Result, DataRequest[_]]] = {
      val request = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers.copy(tasks = tasks))
      refine(request)
    }
  }

  "a dependent tasks action" - {

    "must redirect to task list" - {

      "when not all dependent tasks have been completed" in {

        val action = new Harness()

        val tasks = Map(
          ".preTaskList"      -> TaskStatus.Completed,
          ".traderDetails"    -> TaskStatus.NotStarted,
          ".routeDetails"     -> TaskStatus.NotStarted,
          ".transportDetails" -> TaskStatus.NotStarted
        )

        whenReady(action.callRefine(tasks)) {
          r =>
            val result = Future.successful(r.left.value)
            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual
              s"http://localhost:10120/manage-transit-movements/departures/$lrn/task-list"
        }
      }
    }

    "must return data request" - {

      "when all dependent tasks have been completed" in {

        val action = new Harness()

        val tasks = Map(
          ".preTaskList"      -> TaskStatus.Completed,
          ".traderDetails"    -> TaskStatus.Completed,
          ".routeDetails"     -> TaskStatus.Completed,
          ".transportDetails" -> TaskStatus.Completed
        )

        whenReady(action.callRefine(tasks)) {
          r =>
            r mustBe Symbol("right")
        }
      }
    }
  }
}
