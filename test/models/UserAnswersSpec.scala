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
import pages.external.ConsignmentAdditionalInformationTypePage
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

    "removeConsignmentAdditionalInformation" - {
      "when country of destination is in CL009" - {
        val isCountryOfDestinationInCL009 = true

        "and there is consignment additional information" - {
          "and none have type 30600" - {
            "must do nothing" in {
              val userAnswers = emptyUserAnswers
                .setValue(ConsignmentAdditionalInformationTypePage(Index(0)), "20100")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(1)), "20200")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(2)), "20300")

              val result = userAnswers.removeConsignmentAdditionalInformation(isCountryOfDestinationInCL009)

              val expectedResult = userAnswers

              result mustBe expectedResult
            }
          }

          "and some have type 30600" - {
            "must remove them" in {
              val userAnswers = emptyUserAnswers
                .setValue(ConsignmentAdditionalInformationTypePage(Index(0)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(1)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(2)), "20100")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(3)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(4)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(5)), "20200")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(6)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(7)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(8)), "20300")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(9)), "30600")
                .setValue(ConsignmentAdditionalInformationTypePage(Index(10)), "30600")

              val result = userAnswers.removeConsignmentAdditionalInformation(isCountryOfDestinationInCL009)

              val expectedData = Json.parse("""
                  |{
                  |  "transportDetails" : {
                  |    "additionalInformation" : [
                  |      {
                  |        "type" : {
                  |          "code" : "20100"
                  |        }
                  |      },
                  |      {
                  |        "type" : {
                  |          "code" : "20200"
                  |        }
                  |      },
                  |      {
                  |        "type" : {
                  |          "code" : "20300"
                  |        }
                  |      }
                  |    ]
                  |  }
                  |}
                  |""".stripMargin)

              result.data mustBe expectedData
            }
          }
        }

        "and there is no consignment additional information" - {
          "must do nothing" in {
            val userAnswers = emptyUserAnswers

            val result = userAnswers.removeConsignmentAdditionalInformation(isCountryOfDestinationInCL009)

            val expectedResult = userAnswers

            result mustBe expectedResult
          }
        }
      }

      "when country of destination is not in CL009" - {
        val isCountryOfDestinationInCL009 = false

        "must do nothing" in {
          val userAnswers = emptyUserAnswers
            .setValue(ConsignmentAdditionalInformationTypePage(Index(0)), "20100")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(1)), "20200")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(2)), "20300")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(3)), "30600")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(4)), "20100")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(5)), "20200")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(6)), "20300")
            .setValue(ConsignmentAdditionalInformationTypePage(Index(7)), "30600")

          val result = userAnswers.removeConsignmentAdditionalInformation(isCountryOfDestinationInCL009)

          val expectedResult = userAnswers

          result mustBe expectedResult
        }
      }
    }
  }
}
