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
import play.api.libs.json.{Json, Reads}

class SupplyChainActorTypeSpec extends SpecBase with ScalaCheckPropertyChecks {
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  "SupplyChainActorType" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val supplyChainActorType = SupplyChainActorType(code, description)
          Json.toJson(supplyChainActorType) mustEqual Json.parse(s"""
                                                            |{
                                                            |  "role": "$code",
                                                            |  "description": "$description"
                                                            |}
                                                            |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when reading from reference data" - {
        "when phase 5" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(false)
          implicit val reads: Reads[SupplyChainActorType] = SupplyChainActorType.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (code, description) =>
              val supplyChainActorType = SupplyChainActorType(code, description)
              Json
                .parse(s"""
                     |{
                     |  "role": "$code",
                     |  "description": "$description"
                     |}
                     |""".stripMargin)
                .as[SupplyChainActorType] mustEqual supplyChainActorType
          }
        }

        "when phase 6" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(true)
          implicit val reads: Reads[SupplyChainActorType] = SupplyChainActorType.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (code, description) =>
              val supplyChainActorType = SupplyChainActorType(code, description)
              Json
                .parse(s"""
                     |{
                     |  "key": "$code",
                     |  "value": "$description"
                     |}
                     |""".stripMargin)
                .as[SupplyChainActorType] mustEqual supplyChainActorType
          }
        }
      }
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val supplyChainActorType = SupplyChainActorType(code, description)
            Json
              .parse(s"""
                   |{
                   |  "role": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[SupplyChainActorType] mustEqual supplyChainActorType
        }
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val supplyChainActorType = SupplyChainActorType(code, description)
          supplyChainActorType.toString mustEqual s"$description"
      }
    }

    "when description contains raw HTML" in {
      val supplyChainActorType = SupplyChainActorType("3", "one &amp; two")
      supplyChainActorType.toString mustEqual "one & two"
    }
  }

}
