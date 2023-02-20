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
import models.CustomsOfficeList
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import play.mvc.Http.MimeTypes.JSON
import play.mvc.Http.Status._

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

  private val customsOfficeDestinationResponseJson: String =
    """
      |[
      | {
      |   "id" : "GB1",
      |   "name" : "testName1"
      | },
      | {
      |   "id" : "GB2",
      |   "name" : "testName2"
      | }
      |]
      |""".stripMargin

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

  private val currencyCodesResponseJson: String =
    """
      |[
      | {
      |   "currency":"GBP",
      |   "description":"Sterling"
      | },
      | {
      |   "currency":"CHF",
      |   "description":"Swiss Franc"
      | }
      |]
      |""".stripMargin

  private val countriesWithoutZipResponseJson: String =
    """
      |[
      | "AE",
      | "AG"
      |]
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesOfTransitForCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/GB?role=TRA"))
            .willReturn(okJson(customsOfficeDestinationResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("GB1", "testName1", None),
              CustomsOffice("GB2", "testName2", None)
            )
          )

        connector.getCustomsOfficesOfTransitForCountry(CountryCode("GB")).futureValue mustBe expectedResult
      }

      "must return a successful future response when CustomsOffice is not found" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/AR?role=TRA")).willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withHeader(CONTENT_TYPE, JSON)
          )
        )

        val expectedResult =
          CustomsOfficeList(Nil)

        connector.getCustomsOfficesOfTransitForCountry(CountryCode("AR")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/customs-offices/GB?role=TRA", connector.getCustomsOfficesOfTransitForCountry(CountryCode("GB")))
      }
    }

    "getCustomsOfficesOfDestinationForCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/GB?role=DES"))
            .willReturn(okJson(customsOfficeDestinationResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("GB1", "testName1", None),
              CustomsOffice("GB2", "testName2", None)
            )
          )

        connector.getCustomsOfficesOfDestinationForCountry(CountryCode("GB")).futureValue mustBe expectedResult
      }

      "must return a successful future response when CustomsOffice is not found" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/AR?role=DES")).willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withHeader(CONTENT_TYPE, JSON)
          )
        )

        val expectedResult =
          CustomsOfficeList(Nil)

        connector.getCustomsOfficesOfDestinationForCountry(CountryCode("AR")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/customs-offices/GB?role=DES", connector.getCustomsOfficesOfDestinationForCountry(CountryCode("GB")))
      }
    }

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

    "getCustomsSecurityAgreementAreaCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/country-customs-office-security-agreement-area"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCustomsSecurityAgreementAreaCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/country-customs-office-security-agreement-area", connector.getCustomsSecurityAgreementAreaCountries())
      }
    }

    "getCountryCodesCTC" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/country-codes-ctc"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountryCodesCTC().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/country-codes-ctc", connector.getCountryCodesCTC())
      }
    }

    "getAddressPostcodeBasedCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/country-address-postcode-based"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getAddressPostcodeBasedCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/country-address-postcode-based", connector.getAddressPostcodeBasedCountries())
      }
    }

    "getCountriesWithoutZip" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/country-without-zip"))
            .willReturn(okJson(countriesWithoutZipResponseJson))
        )

        val expectedResult: Seq[CountryCode] = Seq(
          CountryCode("AE"),
          CountryCode("AG")
        )

        connector.getCountriesWithoutZip().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/country-without-zip", connector.getCountriesWithoutZip())
      }
    }

    "getCurrencyCodes" - {
      "must return a successful future response with a sequence of currency codes" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/currency-codes"))
            .willReturn(okJson(currencyCodesResponseJson))
        )

        val expectedResult =
          Seq(
            CurrencyCode("GBP", Some("Sterling")),
            CurrencyCode("CHF", Some("Swiss Franc"))
          )

        connector.getCurrencyCodes().futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/currency-codes", connector.getCurrencyCodes())
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
