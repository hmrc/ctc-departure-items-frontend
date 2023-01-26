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

import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, UserAnswers}
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FakeDataRetrievalAction(
  lrn: LocalReferenceNumber,
  sessionRepository: SessionRepository,
  dataToReturn: Option[UserAnswers]
) extends DataRetrievalAction(lrn, sessionRepository) {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] =
    Future.successful(OptionalDataRequest(request.request, request.eoriNumber, dataToReturn))
}

object FakeDataRetrievalAction {

  class FakeDataRetrievalActionProvider(dataToReturn: Option[UserAnswers]) {

    def apply(
      lrn: LocalReferenceNumber,
      sessionRepository: SessionRepository
    ): FakeDataRetrievalAction =
      new FakeDataRetrievalAction(lrn, sessionRepository, dataToReturn)
  }
}
