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

import com.google.inject.Singleton
import models.LocalReferenceNumber
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRetrievalActionProviderImpl @Inject() (sessionRepository: SessionRepository)(implicit ec: ExecutionContext) extends DataRetrievalActionProvider {

  def apply(lrn: LocalReferenceNumber): ActionTransformer[IdentifierRequest, OptionalDataRequest] =
    new DataRetrievalAction(lrn, sessionRepository)
}

trait DataRetrievalActionProvider {

  def apply(lrn: LocalReferenceNumber): ActionTransformer[IdentifierRequest, OptionalDataRequest]
}

class DataRetrievalAction(
  lrn: LocalReferenceNumber,
  sessionRepository: SessionRepository
)(implicit val executionContext: ExecutionContext)
    extends ActionTransformer[IdentifierRequest, OptionalDataRequest] {

  override protected def transform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    sessionRepository.get(lrn).map {
      userAnswers =>
        OptionalDataRequest(request.request, request.eoriNumber, userAnswers)
    }
  }
}
