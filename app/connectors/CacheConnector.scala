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

package connectors

import config.FrontendAppConfig
import models.{LocalReferenceNumber, UserAnswers}
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CacheConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClient
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  def get(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    val url = s"$baseUrl/user-answers/$lrn"

    http.GET[UserAnswers](url).map {
      userAnswers => Some(userAnswers)
    } recover {
      case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
    }
  }

  def post(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/user-answers/${userAnswers.lrn}"

    http.POST[UserAnswers, HttpResponse](url, userAnswers).map {
      _.status == OK
    }
  }

}
