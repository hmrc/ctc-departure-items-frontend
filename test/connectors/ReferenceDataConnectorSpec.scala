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
import models.{DeclarationTypeItemLevel, PackingType}
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customsReferenceData.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

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

  "Reference Data" - {

    "getDeclarationTypeItemLevel" - {
      val url = s"/$baseUrl/lists/DeclarationTypeItemLevel"
      "must return Seq of declaration types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(declarationTypesResponseJson))
        )

        val expectedResult: Seq[DeclarationTypeItemLevel] = Seq(
          DeclarationTypeItemLevel("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
          DeclarationTypeItemLevel("TIR", "TIR carnet")
        )

        val res = connector.getDeclarationTypeItemLevel().futureValue

        res mustEqual expectedResult
      }
    }

    "getCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/CountryCodesFullList"))
            .willReturn(okJson(countriesResponseJson("CountryCodesFullList")))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountries.futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/lists/CountryCodesFullList", connector.getCountries)
      }
    }

    "getCountryCodesForAddress" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/CountryCodesForAddress"))
            .willReturn(okJson(countriesResponseJson("CountryCodesForAddress")))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountryCodesForAddress.futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/lists/CountryCodesFullList", connector.getCountries)
      }
    }

    "getCountriesWithoutZip" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/CountryWithoutZip"))
            .willReturn(okJson(countriesResponseJson("CountryWithoutZip")))
        )

        val expectedResult: Seq[CountryCode] = Seq(
          CountryCode("GB"),
          CountryCode("AD")
        )

        connector.getCountriesWithoutZip().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/country-without-zip", connector.getCountriesWithoutZip())
      }
    }

    "getPackageTypes" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/KindOfPackages"))
            .willReturn(okJson(packageTypeJson("KindOfPackages")))
        )

        val expectResult = Seq(
          PackageType("VA", Some("Vat"), PackingType.Other),
          PackageType("UC", Some("Uncaged"), PackingType.Other)
        )

        connector.getPackageTypes.futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/lists/KindOfPackages", connector.getPackageTypes())
      }

    }

    "getPackageTypesBulk" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/KindOfPackagesBulk"))
            .willReturn(okJson(packageTypeJson("KindOfPackagesBulk")))
        )

        val expectResult = Seq(
          PackageType("VA", Some("Vat"), PackingType.Bulk),
          PackageType("UC", Some("Uncaged"), PackingType.Bulk)
        )

        connector.getPackageTypesBulk().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/lists/KindOfPackagesBulk", connector.getPackageTypesBulk())
      }

    }

    "getPackageTypesUnpacked" - {

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/KindOfPackagesUnpacked"))
            .willReturn(okJson(packageTypeJson("KindOfPackagesUnpacked")))
        )

        val expectResult = Seq(
          PackageType("VA", Some("Vat"), PackingType.Unpacked),
          PackageType("UC", Some("Uncaged"), PackingType.Unpacked)
        )

        connector.getPackageTypesUnpacked().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/lists/KindOfPackagesUnpacked", connector.getPackageTypesUnpacked())
      }

    }

    "getAdditionalReferences" - {
      "must return Seq of AdditionalReference when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/AdditionalReference"))
            .willReturn(okJson(additionalReferenceJson))
        )

        val expectedResult: Seq[AdditionalReference] = Seq(
          AdditionalReference("documentType1", "desc1"),
          AdditionalReference("documentType2", "desc2")
        )

        connector.getAdditionalReferences().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/lists/AdditionalReference", connector.getAdditionalReferences())
      }

    }

    "getAdditionalInformationTypes" - {

      "must return list of additional information types when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/AdditionalInformation"))
            .willReturn(okJson(additionalInformationJson))
        )

        val expectResult = Seq(
          AdditionalInformation("additionalInfoCode1", "additionalInfoDesc1"),
          AdditionalInformation("additionalInfoCode2", "additionalInfoDesc2")
        )

        connector.getAdditionalInformationTypes().futureValue mustEqual expectResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/lists/AdditionalInformation", connector.getAdditionalInformationTypes())
      }

    }

    "getMethodOfPaymentTypes" - {
      "must return Seq of MethodOfPayments when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/lists/TransportChargesMethodOfPayment"))
            .willReturn(okJson(methodOfPaymentJson))
        )

        val expectedResult: Seq[TransportChargesMethodOfPayment] = Seq(
          TransportChargesMethodOfPayment("A", "Payment By Card"),
          TransportChargesMethodOfPayment("B", "PayPal")
        )

        connector.getTransportChargesMethodOfPaymentTypes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/lists/TransportChargesMethodOfPayment", connector.getTransportChargesMethodOfPaymentTypes())
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
