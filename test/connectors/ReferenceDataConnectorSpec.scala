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
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import helper.WireMockServerHandler
import models.PackingType
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val baseUrl = "test-only/transit-movements-trader-reference-data"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.referenceData.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val countriesResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"AD",
      |   "description":"Andorra"
      | }
      |]
      |""".stripMargin

  private val packageTypeJson: String =
    """
      |[
      | {
      |    "code": "VA",
      |    "description": "Vat",
      |    "type": "Other"
      |  },
      |  {
      |    "code": "UC",
      |    "description": "Uncaged",
      |    "type": "Other"
      |  }
      |]
      |""".stripMargin

  private val additionalReferenceJson: String =
    """
      |[
      | {
      |    "documentType": "documentType1",
      |    "description": "desc1"
      |  },
      |  {
      |    "documentType": "documentType2",
      |    "description": "desc2"
      |  }
      |]
      |""".stripMargin

  private val additionalInformationJson: String =
    """
      |[
      | {
      |    "code": "additionalInfoCode1",
      |    "description": "additionalInfoDesc1"
      |  },
      |  {
      |    "code": "additionalInfoCode2",
      |    "description": "additionalInfoDesc2"
      |  }
      |]
      |""".stripMargin

  "Reference Data" - {

    "getCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/countries?customsOfficeRole=ANY&exclude=IT&exclude=DE&membership=ctc"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        val queryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "ctc"
        )

        connector.getCountries(queryParameters).futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/countries?customsOfficeRole=ANY", connector.getCountries(Nil))
      }
    }

    "getPackageTypes" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/kinds-of-package"))
            .willReturn(okJson(packageTypeJson))
        )

        val expectResult = Seq(
          PackageType("VA", Some("Vat"), PackingType.Other),
          PackageType("UC", Some("Uncaged"), PackingType.Other)
        )

        connector.getPackageTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/kinds-of-package", connector.getPackageTypes())
      }

    }

    "getAdditionalReferences" - {
      "must return Seq of AdditionalReference when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/additional-references"))
            .willReturn(okJson(additionalReferenceJson))
        )

        val expectedResult: Seq[AdditionalReference] = Seq(
          AdditionalReference("documentType1", "desc1"),
          AdditionalReference("documentType2", "desc2")
        )

        connector.getAdditionalReferences().futureValue mustEqual expectedResult
      }

    }

    "getAdditionalInformationTypes" - {

      "must return list of additional information types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/additional-information"))
            .willReturn(okJson(additionalInformationJson))
        )

        val expectResult = Seq(
          AdditionalInformation("additionalInfoCode1", "additionalInfoDesc1"),
          AdditionalInformation("additionalInfoCode2", "additionalInfoDesc2")
        )

        connector.getAdditionlInformationTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/additional-information", connector.getAdditionlInformationTypes())
      }

    }

  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
    val errorResponses: Gen[Int] = Gen
      .chooseNum(400: Int, 599: Int)
      .suchThat(_ != 404)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady(result.failed) {
          _ mustBe an[Exception]
        }
    }
  }

}
