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

import config.FrontendAppConfig
import models.{LocalReferenceNumber, SubmissionState}
import models.requests._
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import models.UserAnswersResponse.{Answers, NotAcceptable}

@Singleton
class DataRequiredAction(lrn: LocalReferenceNumber, config: FrontendAppConfig)(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[OptionalDataRequest, DataRequest] {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case Answers(userAnswers) if userAnswers.status != SubmissionState.Submitted =>
        Future.successful(Right(DataRequest(request.request, request.eoriNumber, userAnswers)))
      case NotAcceptable => Future.successful(Left(Redirect(config.draftNotAvailableUrl)))
      case _ =>
        Future.successful(Left(Redirect(config.sessionExpiredUrl(lrn))))
    }
}

trait DataRequiredActionProvider {
  def apply(lrn: LocalReferenceNumber): ActionRefiner[OptionalDataRequest, DataRequest]
}

class DataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext, config: FrontendAppConfig) extends DataRequiredActionProvider {

  override def apply(lrn: LocalReferenceNumber): ActionRefiner[OptionalDataRequest, DataRequest] =
    new DataRequiredAction(lrn, config)
}
