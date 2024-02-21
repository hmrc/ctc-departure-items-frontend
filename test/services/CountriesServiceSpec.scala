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

package services

import base.SpecBase
import cats.data.NonEmptySet
import connectors.ReferenceDataConnector
import generators.Generators
import models.SelectableList
import models.reference.{Country, CountryCode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CountriesServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

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

    "getCountriesWithoutZip" - {
      "must return a list of countries without ZIP codes" in {

        when(mockRefDataConnector.getCountriesWithoutZip()(any(), any()))
          .thenReturn(Future.successful(countries.map(_.code)))

        service.getCountriesWithoutZip().futureValue mustBe
          Seq(country3.code, country2.code, country1.code)

        verify(mockRefDataConnector).getCountriesWithoutZip()(any(), any())
      }
    }

    "doesCountryRequireZip" - {
      "must return true" - {
        "when countries without zip doesn't contain this country" in {
          when(mockRefDataConnector.getCountriesWithoutZip()(any(), any()))
            .thenReturn(Future.successful(countries.map(_.code)))

          val country = Arbitrary.arbitrary[Country].retryUntil(!countries.contains(_)).sample.value

          val result = service.doesCountryRequireZip(country).futureValue

          result mustBe true

        }
      }

      "must return false" - {
        "when countries without zip does contain this country" in {
          when(mockRefDataConnector.getCountriesWithoutZip()(any(), any()))
            .thenReturn(Future.successful(countries.map(_.code)))

          val country = countries.head

          val result = service.doesCountryRequireZip(country).futureValue

          result mustBe false

        }
      }
    }
  }
}
