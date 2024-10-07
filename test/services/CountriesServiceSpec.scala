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

package services

import base.SpecBase
import cats.data.NonEmptySet
import connectors.ReferenceDataConnector
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import generators.Generators
import models.SelectableList
import models.reference.{Country, CountryCode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with Generators with ScalaCheckPropertyChecks {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new CountriesService(mockRefDataConnector)

  private val country1: Country = Country(CountryCode("GB"), "United Kingdom")
  private val country2: Country = Country(CountryCode("FR"), "France")
  private val country3: Country = Country(CountryCode("ES"), "Spain")
  private val countries         = NonEmptySet.of(country1, country2, country3)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CountriesService" - {

    "getCountries" - {
      "must return a list of sorted countries" in {

        when(mockRefDataConnector.getCountries()(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountries().futureValue mustBe
          SelectableList(Seq(country2, country3, country1))

        verify(mockRefDataConnector).getCountries()(any(), any())
      }
    }

    "getCountryCodesForAddress" - {
      "must return a list of countries" in {

        when(mockRefDataConnector.getCountryCodesForAddress()(any(), any()))
          .thenReturn(Future.successful(countries))

        service.getCountryCodesForAddress().futureValue mustBe
          SelectableList(Seq(country2, country3, country1))

        verify(mockRefDataConnector).getCountryCodesForAddress()(any(), any())
      }
    }

    "doesCountryRequireZip" - {
      "must return true" - {
        "when countries without zip doesn't contain this country" in {
          forAll(arbitrary[Country]) {
            country =>
              when(mockRefDataConnector.getCountriesWithoutZipCountry(any())(any(), any()))
                .thenReturn(Future.successful(country.code))

              val result = service.doesCountryRequireZip(country).futureValue

              result mustBe true
          }
        }
      }

      "must return false" - {
        "when countries without zip does contain this country" in {
          forAll(arbitrary[Country]) {
            country =>
              when(mockRefDataConnector.getCountriesWithoutZipCountry(any())(any(), any()))
                .thenReturn(Future.failed(new NoReferenceDataFoundException("")))

              val result = service.doesCountryRequireZip(country).futureValue

              result mustBe false
          }
        }
      }
    }

    "isCountryInCL009" - {
      "must return true" - {
        "when connector call returns the country" in {
          when(mockRefDataConnector.getCountryCodeCommonTransit(any())(any(), any()))
            .thenReturn(Future.successful(country1))

          val result = service.isCountryInCL009(country1).futureValue

          result mustBe true
        }
      }

      "must return false" - {
        "when connector call returns NoReferenceDataFoundException" in {
          when(mockRefDataConnector.getCountryCodeCommonTransit(any())(any(), any()))
            .thenReturn(Future.failed(new NoReferenceDataFoundException("")))

          val result = service.isCountryInCL009(country1).futureValue

          result mustBe false
        }
      }

      "must fail" - {
        "when connector call otherwise fails" in {
          when(mockRefDataConnector.getCountryCodeCommonTransit(any())(any(), any()))
            .thenReturn(Future.failed(new Throwable("")))

          val result = service.isCountryInCL009(country1)

          result.failed.futureValue mustBe a[Throwable]
        }
      }
    }
  }
}
