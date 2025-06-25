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

package connectors

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import itbase.{ItSpecBase, WireMockServerHandler}
import models.reference.*
import models.{DeclarationTypeItemLevel, PackingType}
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with EitherValues {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customs-reference-data.port" -> server.port()
    )

  private val phase5App: GuiceApplicationBuilder => GuiceApplicationBuilder =
    _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> false)

  private val phase6App: GuiceApplicationBuilder => GuiceApplicationBuilder =
    _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> true)

  private def countriesResponseJson(listName: String): String =
    s"""
       |{
       |  "_links": {
       |    "self": {
       |      "href": "/customs-reference-data/lists/$listName"
       |    }
       |  },
       |  "meta": {
       |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
       |    "snapshotDate": "2023-01-01"
       |  },
       |  "id": "$listName",
       |  "data": [
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "GB",
       |      "state": "valid",
       |      "description": "United Kingdom"
       |    },
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "AD",
       |      "state": "valid",
       |      "description": "Andorra"
       |    }
       |  ]
       |}
       |""".stripMargin

  private val countriesResponseP6Json: String =
    s"""
       |[
       |    {
       |      "key": "GB",
       |      "value": "United Kingdom"
       |    },
       |    {
       |      "key": "AD",
       |      "value": "Andorra"
       |    }
       |]
       |""".stripMargin

  private def countryResponseJson(listName: String): String =
    s"""
       |{
       |  "_links": {
       |    "self": {
       |      "href": "/customs-reference-data/lists/$listName"
       |    }
       |  },
       |  "meta": {
       |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
       |    "snapshotDate": "2023-01-01"
       |  },
       |  "id": "$listName",
       |  "data": [
       |    {
       |      "activeFrom": "2023-01-23",
       |      "code": "GB",
       |      "state": "valid",
       |      "description": "United Kingdom"
       |    }
       |  ]
       |}
       |""".stripMargin

  private val countryResponseP6Json: String =
    s"""
       |[
       |    {
       |      "key": "GB",
       |      "value": "United Kingdom"
       |    }
       |]
       |""".stripMargin

  private def packageTypeJson(listName: String): String =
    s"""
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/$listName"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "$listName",
      |  "data": [
      | {
      |    "code": "VA",
      |    "description": "Vat"
      |  },
      |  {
      |    "code": "UC",
      |    "description": "Uncaged"
      |  }
      |]
      |}
      |""".stripMargin

  private val packageTypeP6Json: String =
    s"""
      |[
      | {
      |    "key": "VA",
      |    "value": "Vat"
      |  },
      |  {
      |    "key": "UC",
      |    "value": "Uncaged"
      |  }
      |]
      |""".stripMargin

  private val additionalReferenceJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/AdditionalReference"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "AdditionalReference",
      |  "data": [
      | {
      |    "documentType": "documentType1",
      |    "description": "desc1"
      |  },
      |  {
      |    "documentType": "documentType2",
      |    "description": "desc2"
      |  }
      |]
      |}
      |""".stripMargin

  private val additionalReferenceP6Json: String =
    """
      |[
      | {
      |    "key": "documentType1",
      |    "value": "desc1"
      |  },
      |  {
      |    "key": "documentType2",
      |    "value": "desc2"
      |  }
      |]
      |""".stripMargin

  private val additionalInformationJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/AdditionalInformation"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "AdditionalInformation",
      |  "data": [
      | {
      |    "code": "additionalInfoCode1",
      |    "description": "additionalInfoDesc1"
      |  },
      |  {
      |    "code": "additionalInfoCode2",
      |    "description": "additionalInfoDesc2"
      |  }
      |]
      |}
      |""".stripMargin

  private val additionalInformationP6Json: String =
    """
      |[
      | {
      |    "key": "additionalInfoCode1",
      |    "value": "additionalInfoDesc1"
      |  },
      |  {
      |    "key": "additionalInfoCode2",
      |    "value": "additionalInfoDesc2"
      |  }
      |]
      |""".stripMargin

  private val methodOfPaymentJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/TransportChargesMethodOfPayment"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "MethodOfPayment",
      |  "data": [
      | {
      |    "method": "A",
      |    "description": "Payment By Card"
      |  },
      |  {
      |    "method": "B",
      |    "description": "PayPal"
      |  }
      |]
      |}
      |""".stripMargin

  private val methodOfPaymentP6Json: String =
    """
      |[
      | {
      |    "key": "A",
      |    "value": "Payment By Card"
      |  },
      |  {
      |    "key": "B",
      |    "value": "PayPal"
      |  }
      |]
      |""".stripMargin

  private val declarationTypesResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/DeclarationTypeItemLevel"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "DeclarationTypeItemLevel",
      |  "data": [
      |    {
      |      "code": "T2",
      |      "description": "Goods having the customs status of Union goods, which are placed under the common transit procedure"
      |    },
      |    {
      |      "code": "TIR",
      |      "description": "TIR carnet"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val declarationTypesResponseP6Json: String =
    """
      |[
      |    {
      |      "key": "T2",
      |      "value": "Goods having the customs status of Union goods, which are placed under the common transit procedure"
      |    },
      |    {
      |      "key": "TIR",
      |      "value": "TIR carnet"
      |    }
      |]
      |""".stripMargin

  val supplyChainActorTypesResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/AdditionalSupplyChainActorRoleCode"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "AdditionalSupplyChainActorRoleCode",
      |  "data": [
      |    {
      |      "role":"CS",
      |      "description":"Consolidator"
      |    },
      |    {
      |      "role":"MF",
      |      "description":"Manufacturer"
      |    }
      |  ]
      |}
      |""".stripMargin

  val supplyChainActorTypesResponseP6Json: String =
    """
      |[
      |    {
      |      "key":"CS",
      |      "value":"Consolidator"
      |    },
      |    {
      |      "key":"MF",
      |      "value":"Manufacturer"
      |    }
      |]
      |""".stripMargin

  private val cusCodeResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/CUSCode"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "CUSCode",
      |  "data": [
      |    {
      |      "code": "0010001-6"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val cusCodeResponseP6Json: String =
    """
      |[
      |    {
      |      "key": "0010001-6"
      |    }
      |]
      |""".stripMargin

  private val documentTypeExciseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/DocumentTypeExcise"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "DocumentTypeExcise",
      |  "data": [
      |  {
      |    "activeFrom": "2024-01-01",
      |    "code": "C651",
      |    "state": "valid",
      |    "description": "AAD - Administrative Accompanying Document (EMCS)"
      |  },
      |  {
      |    "activeFrom": "2024-01-01",
      |    "code": "C658",
      |    "state": "valid",
      |    "description": "FAD - Fallback e-AD (EMCS)"
      |  }
      |]
      |}
      |""".stripMargin

  private val documentTypeExciseP6Json: String =
    """
      |[
      |  {
      |    "key": "C651",
      |    "value": "AAD - Administrative Accompanying Document (EMCS)"
      |  },
      |  {
      |    "key": "C658",
      |    "value": "FAD - Fallback e-AD (EMCS)"
      |  }
      |]
      |""".stripMargin

  private val emptyPhase5ResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  private val emptyPhase6ResponseJson: String =
    """
      |[]
      |""".stripMargin

  "Reference Data" - {

    "getCusCode" - {

      val cusCode = "0010001-6"

      "when phase 5" - {
        val url = s"/$baseUrl/lists/CUSCode?data.code=$cusCode"

        "must return CUSCode when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(cusCodeResponseJson))
              )

              val expectedResult = CUSCode(cusCode)

              connector.getCUSCode(cusCode).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCUSCode(cusCode))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCUSCode(cusCode))
          }
        }
      }

      "when phase 6" - {
        val url = s"/$baseUrl/lists/CUSCode?keys=$cusCode"

        "must return CUSCode when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(cusCodeResponseP6Json))
              )

              val expectedResult = CUSCode(cusCode)

              connector.getCUSCode(cusCode).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCUSCode(cusCode))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCUSCode(cusCode))
          }
        }
      }

    }

    "getDocumentTypeExcise" - {
      val code = "C651"

      "when phase 5" - {
        val url = s"/$baseUrl/lists/DocumentTypeExcise?data.code=$code"

        "must return DocumentTypeExcise when successful" in {

          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(documentTypeExciseJson))
              )

              val expectedResult =
                DocTypeExcise(code = "C651", description = "AAD - Administrative Accompanying Document (EMCS)")

              connector.getDocumentTypeExcise(code).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getDocumentTypeExcise(code))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getDocumentTypeExcise(code))
          }
        }
      }

      "when phase 6" - {
        val url = s"/$baseUrl/lists/DocumentTypeExcise?keys=$code"

        "must return DocumentTypeExcise when successful" in {

          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(documentTypeExciseP6Json))
              )

              val expectedResult =
                DocTypeExcise(code = "C651", description = "AAD - Administrative Accompanying Document (EMCS)")

              connector.getDocumentTypeExcise(code).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getDocumentTypeExcise(code))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getDocumentTypeExcise(code))
          }
        }
      }
    }

    "getDeclarationTypeItemLevel" - {
      val url = s"/$baseUrl/lists/DeclarationTypeItemLevel"

      "when phase 5" - {
        "must return Seq of declaration types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(declarationTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                DeclarationTypeItemLevel("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
                DeclarationTypeItemLevel("TIR", "TIR carnet")
              )

              val res = connector.getDeclarationTypeItemLevel().futureValue.value

              res mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getDeclarationTypeItemLevel())
          }

        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getDeclarationTypeItemLevel())
          }
        }
      }

      "when phase 6" - {
        "must return Seq of declaration types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(declarationTypesResponseP6Json))
              )

              val expectedResult = NonEmptySet.of(
                DeclarationTypeItemLevel("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
                DeclarationTypeItemLevel("TIR", "TIR carnet")
              )

              val res = connector.getDeclarationTypeItemLevel().futureValue.value

              res mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getDeclarationTypeItemLevel())
          }

        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getDeclarationTypeItemLevel())
          }
        }
      }
    }

    "getCountries" - {
      val url = s"/$baseUrl/lists/CountryCodesFullList"

      "when phase 5" - {

        "must return Seq of Country when successful" in {

          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countriesResponseJson("CountryCodesFullList")))
              )

              val expectedResult = NonEmptySet.of(
                Country(CountryCode("GB"), "United Kingdom"),
                Country(CountryCode("AD"), "Andorra")
              )

              connector.getCountries().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCountries())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountries())
          }
        }
      }

      "when phase 6" - {

        "must return Seq of Country when successful" in {

          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseP6Json))
              )

              val expectedResult = NonEmptySet.of(
                Country(CountryCode("GB"), "United Kingdom"),
                Country(CountryCode("AD"), "Andorra")
              )

              connector.getCountries().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCountries())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountries())
          }
        }
      }

    }

    "getCountryCodesForAddress" - {
      val url = s"/$baseUrl/lists/CountryCodesForAddress"

      "when phase 5" - {

        "must return Seq of Country when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countriesResponseJson("CountryCodesForAddress")))
              )

              val expectedResult = NonEmptySet.of(
                Country(CountryCode("GB"), "United Kingdom"),
                Country(CountryCode("AD"), "Andorra")
              )

              connector.getCountryCodesForAddress().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCountryCodesForAddress())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountryCodesForAddress())
          }
        }
      }

      "when phase 6" - {

        "must return Seq of Country when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseP6Json))
              )

              val expectedResult = NonEmptySet.of(
                Country(CountryCode("GB"), "United Kingdom"),
                Country(CountryCode("AD"), "Andorra")
              )

              connector.getCountryCodesForAddress().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCountryCodesForAddress())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountryCodesForAddress())
          }
        }
      }
    }

    "getCountryCodeCommonTransit" - {

      "when phase 5" - {
        def url(code: String) = s"/$baseUrl/lists/CountryCodesCommonTransit?data.code=$code"

        "must return Country when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val code      = "GB"

              server.stubFor(
                get(urlEqualTo(url(code)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countryResponseJson("CountryCodesCommonTransit")))
              )

              val country = Country(CountryCode(code), "United Kingdom")

              connector.getCountryCodeCommonTransit(country).futureValue.value mustEqual country
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val code      = "FR"
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              val country = Country(CountryCode(code), "France")

              checkNoReferenceDataFoundResponse(url(code), emptyPhase5ResponseJson, connector.getCountryCodeCommonTransit(country))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val code      = "GB"
              val country   = Country(CountryCode(code), "United Kingdom")
              checkErrorResponse(url(code), connector.getCountryCodeCommonTransit(country))
          }
        }
      }

      "when phase 6" - {
        def url(code: String) = s"/$baseUrl/lists/CountryCodesCommonTransit?keys=$code"

        "must return Country when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val code      = "GB"

              server.stubFor(
                get(urlEqualTo(url(code)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countryResponseP6Json))
              )

              val country = Country(CountryCode(code), "United Kingdom")

              connector.getCountryCodeCommonTransit(country).futureValue.value mustEqual country
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val code      = "FR"
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              val country = Country(CountryCode(code), "France")

              checkNoReferenceDataFoundResponse(url(code), emptyPhase6ResponseJson, connector.getCountryCodeCommonTransit(country))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val code      = "GB"
              val country   = Country(CountryCode(code), "United Kingdom")
              checkErrorResponse(url(code), connector.getCountryCodeCommonTransit(country))
          }
        }
      }
    }

    "getCountriesWithoutZipCountry" - {

      "when phase 5" - {
        def url(countryId: String) = s"/$baseUrl/lists/CountryWithoutZip?data.code=$countryId"

        "must return Seq of Country when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "GB"
              server.stubFor(
                get(urlEqualTo(url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countryResponseJson("CountryWithoutZip")))
              )

              val expectedResult = CountryCode(countryId)

              connector.getCountriesWithoutZipCountry(countryId).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val countryId = "FR"
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url(countryId), emptyPhase5ResponseJson, connector.getCountriesWithoutZipCountry(countryId))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "FR"
              checkErrorResponse(url(countryId), connector.getCountriesWithoutZipCountry(countryId))
          }
        }
      }

      "when phase 6" - {
        def url(countryId: String) = s"/$baseUrl/lists/CountryWithoutZip?keys=$countryId"

        "must return Seq of Country when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "GB"
              server.stubFor(
                get(urlEqualTo(url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countryResponseP6Json))
              )

              val expectedResult = CountryCode(countryId)

              connector.getCountriesWithoutZipCountry(countryId).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val countryId = "FR"
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url(countryId), emptyPhase6ResponseJson, connector.getCountriesWithoutZipCountry(countryId))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "FR"
              checkErrorResponse(url(countryId), connector.getCountriesWithoutZipCountry(countryId))
          }
        }
      }
    }

    "getPackageTypes" - {
      val url = s"/$baseUrl/lists/KindOfPackages"

      "when phase 5" - {

        "must return list of package types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(packageTypeJson("KindOfPackages")))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Other),
                PackageType("UC", "Uncaged", PackingType.Other)
              )

              connector.getPackageTypes().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getPackageTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypes())
          }

        }
      }

      "when phase 6" - {

        "must return list of package types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(packageTypeP6Json))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Other),
                PackageType("UC", "Uncaged", PackingType.Other)
              )

              connector.getPackageTypes().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getPackageTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypes())
          }

        }
      }

    }

    "getPackageTypesBulk" - {
      val url = s"/$baseUrl/lists/KindOfPackagesBulk"

      "when phase 5" - {

        "must return list of package types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(packageTypeJson("KindOfPackagesBulk")))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Bulk),
                PackageType("UC", "Uncaged", PackingType.Bulk)
              )

              connector.getPackageTypesBulk().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getPackageTypesBulk())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypesBulk())
          }
        }
      }

      "when phase 6" - {

        "must return list of package types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(packageTypeP6Json))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Bulk),
                PackageType("UC", "Uncaged", PackingType.Bulk)
              )

              connector.getPackageTypesBulk().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getPackageTypesBulk())

          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypesBulk())
          }
        }
      }

    }

    "getPackageTypesUnpacked" - {
      val url = s"/$baseUrl/lists/KindOfPackagesUnpacked"

      "when phase 5" - {
        "must return list of package types when successful" in {

          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(packageTypeJson("KindOfPackagesUnpacked")))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Unpacked),
                PackageType("UC", "Uncaged", PackingType.Unpacked)
              )

              connector.getPackageTypesUnpacked().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getPackageTypesUnpacked())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypesUnpacked())
          }
        }
      }

      "when phase 6" - {
        "must return list of package types when successful" in {

          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(packageTypeP6Json))
              )

              val expectResult = NonEmptySet.of(
                PackageType("VA", "Vat", PackingType.Unpacked),
                PackageType("UC", "Uncaged", PackingType.Unpacked)
              )

              connector.getPackageTypesUnpacked().futureValue.value mustEqual expectResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getPackageTypesUnpacked())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getPackageTypesUnpacked())
          }
        }
      }
    }

    "getAdditionalReferences" - {
      val url = s"/$baseUrl/lists/AdditionalReference"

      "when phase 5" - {
        "must return Seq of AdditionalReference when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(additionalReferenceJson))
              )

              val expectedResult = NonEmptySet.of(
                AdditionalReference("documentType1", "desc1"),
                AdditionalReference("documentType2", "desc2")
              )

              connector.getAdditionalReferences().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getAdditionalReferences())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getAdditionalReferences())
          }
        }
      }

      "when phase 6" - {
        "must return Seq of AdditionalReference when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(additionalReferenceP6Json))
              )

              val expectedResult = NonEmptySet.of(
                AdditionalReference("documentType1", "desc1"),
                AdditionalReference("documentType2", "desc2")
              )

              connector.getAdditionalReferences().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getAdditionalReferences())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getAdditionalReferences())
          }
        }
      }
    }

    "getAdditionalInformationTypes" - {
      val url = s"/$baseUrl/lists/AdditionalInformation"

      "when phase 5" - {
        "must return list of additional information types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(additionalInformationJson))
              )

              val expectResult = NonEmptySet.of(
                AdditionalInformation("additionalInfoCode1", "additionalInfoDesc1"),
                AdditionalInformation("additionalInfoCode2", "additionalInfoDesc2")
              )

              connector.getAdditionalInformationTypes().futureValue.value mustEqual expectResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getAdditionalInformationTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getAdditionalInformationTypes())
          }
        }
      }

      "when phase 6" - {
        "must return list of additional information types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(additionalInformationP6Json))
              )

              val expectResult = NonEmptySet.of(
                AdditionalInformation("additionalInfoCode1", "additionalInfoDesc1"),
                AdditionalInformation("additionalInfoCode2", "additionalInfoDesc2")
              )

              connector.getAdditionalInformationTypes().futureValue.value mustEqual expectResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getAdditionalInformationTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getAdditionalInformationTypes())
          }
        }
      }
    }

    "getMethodOfPaymentTypes" - {
      val url = s"/$baseUrl/lists/TransportChargesMethodOfPayment"

      "when phase 5" - {
        "must return Seq of MethodOfPayments when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(methodOfPaymentJson))
              )

              val expectedResult = NonEmptySet.of(
                TransportChargesMethodOfPayment("A", "Payment By Card"),
                TransportChargesMethodOfPayment("B", "PayPal")
              )

              connector.getTransportChargesMethodOfPaymentTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getTransportChargesMethodOfPaymentTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getTransportChargesMethodOfPaymentTypes())
          }
        }
      }

      "when phase 6" - {
        "must return Seq of MethodOfPayments when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(methodOfPaymentP6Json))
              )

              val expectedResult = NonEmptySet.of(
                TransportChargesMethodOfPayment("A", "Payment By Card"),
                TransportChargesMethodOfPayment("B", "PayPal")
              )

              connector.getTransportChargesMethodOfPaymentTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getTransportChargesMethodOfPaymentTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getTransportChargesMethodOfPaymentTypes())
          }
        }
      }
    }

    "getSupplyChainActorTypes" - {
      val url: String = s"/$baseUrl/lists/AdditionalSupplyChainActorRoleCode"

      "when phase 5" - {
        "must return Seq of SupplyChainActorType when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(supplyChainActorTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                SupplyChainActorType("CS", "Consolidator"),
                SupplyChainActorType("MF", "Manufacturer")
              )

              connector.getSupplyChainActorTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getSupplyChainActorTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getSupplyChainActorTypes())
          }
        }
      }

      "when phase 6" - {
        "must return Seq of SupplyChainActorType when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]

              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(supplyChainActorTypesResponseP6Json))
              )

              val expectedResult = NonEmptySet.of(
                SupplyChainActorType("CS", "Consolidator"),
                SupplyChainActorType("MF", "Manufacturer")
              )

              connector.getSupplyChainActorTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getSupplyChainActorTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getSupplyChainActorTypes())
          }
        }
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, json: String, result: => Future[Either[Exception, ?]]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(json))
    )

    result.futureValue.left.value mustBe a[NoReferenceDataFoundException]
  }

  private def checkErrorResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
    val errorResponses: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        result.futureValue.left.value mustBe a[Exception]
    }
  }
}
