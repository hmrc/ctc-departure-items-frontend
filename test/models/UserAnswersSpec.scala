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

package models

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsValue, Json}

class UserAnswersSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val userAnswers = UserAnswers(
    lrn = lrn,
    eoriNumber = eoriNumber,
    status = SubmissionState.NotSubmitted,
    data = Json.obj(),
    tasks = Map(
      "task1" -> TaskStatus.Completed,
      "task2" -> TaskStatus.InProgress,
      "task3" -> TaskStatus.NotStarted,
      "task4" -> TaskStatus.CannotStartYet
    )
  )

  "User answers" - {

    "being passed between backend and frontend" - {

      val json: JsValue = Json.parse(s"""
                                        |{
                                        |    "lrn" : "$lrn",
                                        |    "eoriNumber" : "${eoriNumber.value}",
                                        |    "isSubmitted" : "notSubmitted",
                                        |    "data" : {},
                                        |    "tasks" : {
                                        |        "task1" : "completed",
                                        |        "task2" : "in-progress",
                                        |        "task3" : "not-started",
                                        |        "task4" : "cannot-start-yet"
                                        |    }
                                        |}
                                        |""".stripMargin)

      "read correctly" in {
        val result = json.as[UserAnswers]
        result mustBe userAnswers
      }

      "write correctly" in {
        val result = Json.toJson(userAnswers)
        result mustBe json
      }

    }

  }
}
