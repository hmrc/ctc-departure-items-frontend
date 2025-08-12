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

package models.reference

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.FrontendAppConfig
import generators.Generators
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class CountrySpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  "Country" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val country = Country(CountryCode(code), description)
          Json.toJson(country) mustEqual Json.parse(s"""
              |{
              |  "code": "$code",
              |  "description": "$description"
              |}
              |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when reading from reference data" - {
        "when phase 5" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(false)
          implicit val reads: Reads[Country] = Country.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (code, description) =>
              val country = Country(CountryCode(code), description)
              Json
                .parse(s"""
                     |{
                     |  "code": "$code",
                     |  "description": "$description"
                     |}
                     |""".stripMargin)
                .as[Country] mustEqual country
          }
        }

        "when phase 6" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(true)
          implicit val reads: Reads[Country] = Country.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (code, description) =>
              val country = Country(CountryCode(code), description)
              Json
                .parse(s"""
                     |{
                     |  "key": "$code",
                     |  "value": "$description"
                     |}
                     |""".stripMargin)
                .as[Country] mustEqual country
          }
        }
      }

      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val country = Country(CountryCode(code), description)
            Json
              .parse(s"""
                   |{
                   |  "code": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[Country] mustEqual country
        }
      }
    }

    "must convert to select item" in {
      forAll(arbitrary[Country], arbitrary[Boolean]) {
        (country, selected) =>
          country.toSelectItem(selected) mustEqual SelectItem(Some(country.code.code), s"${country.description} - ${country.code.code}", selected)
      }
    }

    "must format as string" in {
      forAll(arbitrary[Country]) {
        country =>
          country.toString mustEqual s"${country.description} - ${country.code.code}"
      }
    }
  }

}
