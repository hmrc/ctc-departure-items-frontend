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

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock._
import models.UserAnswers
import org.scalacheck.Gen
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._

class CacheConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.manage-transit-movements-departure-cache.port" -> server.port())

  private lazy val connector: CacheConnector = app.injector.instanceOf[CacheConnector]

  private val json: String =
    s"""
      |{
      |    "_id" : "2e8ede47-dbfb-44ea-a1e3-6c57b1fe6fe2",
      |    "lrn" : "$lrn",
      |    "eoriNumber" : "GB1234567",
      |    "data" : {},
      |    "tasks" : {},
      |    "createdAt" : "2022-09-05T15:58:44.188",
      |    "lastUpdated" : "2022-09-07T10:33:23.472"
      |}
      |""".stripMargin

  private val userAnswers = Json.parse(json).as[UserAnswers]

  "CacheConnector" - {

    "get" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/$lrn"

      "must return user answers when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(json))
        )

        val result: Option[UserAnswers] = await(connector.get(lrn))

        result mustBe Some(userAnswers)
      }

      "return None when no cached data found for provided LRN" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(notFound())
        )

        val result: Option[UserAnswers] = await(connector.get(lrn))

        result mustBe None
      }
    }

    "post" - {

      val url = s"/manage-transit-movements-departure-cache/user-answers/$lrn"

      "must return true when status is Ok" in {
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe true
      }

      "return false for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe false
      }
    }
  }

}
