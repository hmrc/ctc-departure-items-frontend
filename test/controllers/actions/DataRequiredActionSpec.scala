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
import models.requests.{DataRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, SubmissionState, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private class Harness(lrn: LocalReferenceNumber) extends DataRequiredAction(lrn, frontendAppConfig) {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "Data Required Action" - {

    "when there are no UserAnswers" - {

      "must return Left and redirect to session expired" in {

        val harness = new Harness(lrn)

        val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None)).map(_.left.value)

        status(result) mustBe 303
        redirectLocation(result).value mustBe frontendAppConfig.sessionExpiredUrl(lrn)
      }
    }

    "when there are UserAnswers" - {

      "and answers have previously been submitted" - {
        "must return Left and redirect to session expired" in {
          val userAnswers = UserAnswers(lrn, eoriNumber, status = SubmissionState.Submitted)

          val harness = new Harness(lrn)

          val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers))).map(_.left.value)

          status(result) mustBe 303
          redirectLocation(result).value mustBe frontendAppConfig.sessionExpiredUrl(lrn)
        }
      }

      "and answers have not previously been submitted" - {
        "must return Right with DataRequest" in {
          forAll(arbitrary[SubmissionState].retryUntil(_ != SubmissionState.Submitted)) {
            submissionStatus =>
              val userAnswers = UserAnswers(lrn = lrn, eoriNumber = eoriNumber, status = submissionStatus)

              val harness = new Harness(lrn)

              val result = harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

              whenReady[Either[Result, DataRequest[?]], Assertion](result) {
                result =>
                  result.value.userAnswers mustBe userAnswers
                  result.value.eoriNumber mustBe eoriNumber
              }
          }
        }
      }
    }
  }
}
