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
import models.LockCheck.{LockCheckFailure, Locked, Unlocked}
import models.{LocalReferenceNumber, LockCheck, UserAnswers}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CacheConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClientV2
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  def get(lrn: LocalReferenceNumber)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    val url = url"$baseUrl/user-answers/$lrn"
    http
      .get(url)
      .execute[UserAnswers]
      .map(Some(_))
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
      }
  }

  def post(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = url"$baseUrl/user-answers/${userAnswers.lrn}"
    http
      .post(url)
      .withBody(Json.toJson(userAnswers))
      .execute[HttpResponse]
      .map(_.status == OK)
  }

  def checkLock(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[LockCheck] = {
    val url = url"$baseUrl/user-answers/${userAnswers.lrn}/lock"
    http
      .get(url)
      .execute[HttpResponse]
      .map {
        _.status match {
          case OK     => Unlocked
          case LOCKED => Locked
          case _      => LockCheckFailure
        }
      }
  }
}
