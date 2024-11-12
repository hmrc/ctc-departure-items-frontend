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

package models

import play.api.http.Status.*
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

sealed trait UserAnswersResponse

object UserAnswersResponse {

  case class Answers(value: UserAnswers) extends UserAnswersResponse

  case object NoAnswers extends UserAnswersResponse

  case object BadRequest extends UserAnswersResponse

  implicit val httpReads: HttpReads[UserAnswersResponse] =
    (_: String, _: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          response.json
            .validate[UserAnswers]
            .map(Answers.apply)
            .fold(
              errors => throw new RuntimeException(s"Failed to validate json: $errors"),
              identity
            )
        case NOT_FOUND   => NoAnswers
        case BAD_REQUEST => BadRequest
        case status      => throw new RuntimeException(s"Unexpected http status: $status")
      }
}
