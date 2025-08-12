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

import base.SpecBase
import config.FrontendAppConfig
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsString, Json}

class CountryCodeSpec extends SpecBase with ScalaCheckPropertyChecks {
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  "CountryCode" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr) {
        code =>
          val countryCode = CountryCode(code)
          Json.toJson(countryCode) mustEqual JsString(code)
      }
    }

    "must deserialise" - {
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr) {
          code =>
            val countryCode = CountryCode(code)
            JsString(code).as[CountryCode] mustEqual countryCode
        }
      }

      "when reading from reference data" - {
        "when phase 5" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(false)
          forAll(Gen.alphaNumStr) {
            code =>
              Json
                .parse(s"""
                     |{
                     |  "code": "$code"
                     |}
                     |""".stripMargin)
                .as[CountryCode](CountryCode.reads(mockFrontendAppConfig)) mustEqual CountryCode(code)
          }
        }

        "when phase 6" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(true)
          forAll(Gen.alphaNumStr) {
            code =>
              Json
                .parse(s"""
                     |{
                     |  "key": "$code"
                     |}
                     |""".stripMargin)
                .as[CountryCode](CountryCode.reads(mockFrontendAppConfig)) mustEqual CountryCode(code)
          }
        }
      }
    }
  }
}
