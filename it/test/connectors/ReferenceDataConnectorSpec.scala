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

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import itbase.{ItSpecBase, WireMockServerHandler}
import models.reference._
import models.{DeclarationTypeItemLevel, PackingType}
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customs-reference-data.port" -> server.port()
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

  private val emptyResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  "Reference Data" - {

    "getCusCode" - {
      val cusCode = "0010001-6"
      val url     = s"/$baseUrl/lists/CUSCode?data.code=$cusCode"

      "must return CUSCode when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(cusCodeResponseJson))
        )

        val expectedResult = CUSCode(cusCode)

        connector.getCUSCode(cusCode).futureValue mustEqual expectedResult
      }
    }

    "getDeclarationTypeItemLevel" - {
      val url = s"/$baseUrl/lists/DeclarationTypeItemLevel"

      "must return Seq of declaration types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(declarationTypesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          DeclarationTypeItemLevel("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
          DeclarationTypeItemLevel("TIR", "TIR carnet")
        )

        val res = connector.getDeclarationTypeItemLevel().futureValue

        res mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getDeclarationTypeItemLevel())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getDeclarationTypeItemLevel())
      }
    }

    "getCountries" - {
      val url = s"/$baseUrl/lists/CountryCodesFullList"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseJson("CountryCodesFullList")))
        )

        val expectedResult = NonEmptySet.of(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountries().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountries())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountries())
      }
    }

    "getCountryCodesForAddress" - {
      val url = s"/$baseUrl/lists/CountryCodesForAddress"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseJson("CountryCodesForAddress")))
        )

        val expectedResult = NonEmptySet.of(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getCountryCodesForAddress().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountryCodesForAddress())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountryCodesForAddress())
      }
    }

    "getCountriesWithoutZip" - {
      val url = s"/$baseUrl/lists/CountryWithoutZip"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseJson("CountryWithoutZip")))
        )

        val expectedResult = NonEmptySet.of(
          CountryCode("GB"),
          CountryCode("AD")
        )

        connector.getCountriesWithoutZip().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountriesWithoutZip())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountriesWithoutZip())
      }
    }

    "getPackageTypes" - {
      val url = s"/$baseUrl/lists/KindOfPackages"

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(packageTypeJson("KindOfPackages")))
        )

        val expectResult = NonEmptySet.of(
          PackageType("VA", Some("Vat"), PackingType.Other),
          PackageType("UC", Some("Uncaged"), PackingType.Other)
        )

        connector.getPackageTypes().futureValue mustEqual expectResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getPackageTypes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getPackageTypes())
      }
    }

    "getPackageTypesBulk" - {
      val url = s"/$baseUrl/lists/KindOfPackagesBulk"

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(packageTypeJson("KindOfPackagesBulk")))
        )

        val expectResult = NonEmptySet.of(
          PackageType("VA", Some("Vat"), PackingType.Bulk),
          PackageType("UC", Some("Uncaged"), PackingType.Bulk)
        )

        connector.getPackageTypesBulk().futureValue mustEqual expectResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getPackageTypesBulk())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getPackageTypesBulk())
      }
    }

    "getPackageTypesUnpacked" - {
      val url = s"/$baseUrl/lists/KindOfPackagesUnpacked"

      "must return list of package types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(packageTypeJson("KindOfPackagesUnpacked")))
        )

        val expectResult = NonEmptySet.of(
          PackageType("VA", Some("Vat"), PackingType.Unpacked),
          PackageType("UC", Some("Uncaged"), PackingType.Unpacked)
        )

        connector.getPackageTypesUnpacked().futureValue mustEqual expectResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getPackageTypesUnpacked())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getPackageTypesUnpacked())
      }
    }

    "getAdditionalReferences" - {
      val url = s"/$baseUrl/lists/AdditionalReference"

      "must return Seq of AdditionalReference when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(additionalReferenceJson))
        )

        val expectedResult = NonEmptySet.of(
          AdditionalReference("documentType1", "desc1"),
          AdditionalReference("documentType2", "desc2")
        )

        connector.getAdditionalReferences().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getAdditionalReferences())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getAdditionalReferences())
      }
    }

    "getAdditionalInformationTypes" - {
      val url = s"/$baseUrl/lists/AdditionalInformation"

      "must return list of additional information types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(additionalInformationJson))
        )

        val expectResult = NonEmptySet.of(
          AdditionalInformation("additionalInfoCode1", "additionalInfoDesc1"),
          AdditionalInformation("additionalInfoCode2", "additionalInfoDesc2")
        )

        connector.getAdditionalInformationTypes().futureValue mustEqual expectResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getAdditionalInformationTypes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getAdditionalInformationTypes())
      }
    }

    "getMethodOfPaymentTypes" - {
      val url = s"/$baseUrl/lists/TransportChargesMethodOfPayment"

      "must return Seq of MethodOfPayments when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(methodOfPaymentJson))
        )

        val expectedResult = NonEmptySet.of(
          TransportChargesMethodOfPayment("A", "Payment By Card"),
          TransportChargesMethodOfPayment("B", "PayPal")
        )

        connector.getTransportChargesMethodOfPaymentTypes().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getTransportChargesMethodOfPaymentTypes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getTransportChargesMethodOfPaymentTypes())
      }
    }

    "getSupplyChainActorTypes" - {
      val url: String = s"/$baseUrl/lists/AdditionalSupplyChainActorRoleCode"

      "must return Seq of SupplyChainActorType when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(supplyChainActorTypesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          SupplyChainActorType("CS", "Consolidator"),
          SupplyChainActorType("MF", "Manufacturer")
        )

        connector.getSupplyChainActorTypes().futureValue mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getSupplyChainActorTypes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getSupplyChainActorTypes())
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, result: => Future[_]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(emptyResponseJson))
    )

    whenReady[Throwable, Assertion](result.failed) {
      _ mustBe a[NoReferenceDataFoundException]
    }
  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
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

        whenReady[Throwable, Assertion](result.failed) {
          _ mustBe an[Exception]
        }
    }
  }
}
