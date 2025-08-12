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
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}

class TransportChargesMethodOfPaymentSpec extends SpecBase with ScalaCheckPropertyChecks {
  private val mockFrontendAppConfig = mock[FrontendAppConfig]

  "MethodOfPayment" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (method, description) =>
          val methodOfPayment = TransportChargesMethodOfPayment(method, description)
          Json.toJson(methodOfPayment) mustEqual Json.parse(s"""
               |{
               |  "method": "$method",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when reading from reference data" - {
        "when phase 5" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(false)
          implicit val reads: Reads[TransportChargesMethodOfPayment] = TransportChargesMethodOfPayment.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (method, description) =>
              val methodOfPayment = TransportChargesMethodOfPayment(method, description)
              Json
                .parse(s"""
                     |{
                     |  "method": "$method",
                     |  "description": "$description"
                     |}
                     |""".stripMargin)
                .as[TransportChargesMethodOfPayment] mustEqual methodOfPayment
          }
        }

        "when phase 6" in {
          when(mockFrontendAppConfig.isPhase6Enabled).thenReturn(true)
          implicit val reads: Reads[TransportChargesMethodOfPayment] = TransportChargesMethodOfPayment.reads(mockFrontendAppConfig)
          forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
            (method, description) =>
              val methodOfPayment = TransportChargesMethodOfPayment(method, description)
              Json
                .parse(s"""
                     |{
                     |  "key": "$method",
                     |  "value": "$description"
                     |}
                     |""".stripMargin)
                .as[TransportChargesMethodOfPayment] mustEqual methodOfPayment
          }
        }
      }
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (method, description) =>
            val methodOfPayment = TransportChargesMethodOfPayment(method, description)
            Json
              .parse(s"""
                   |{
                   |  "method": "$method",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[TransportChargesMethodOfPayment] mustEqual methodOfPayment
        }
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (method, description) =>
          val methodOfPayment = TransportChargesMethodOfPayment(method, description)
          methodOfPayment.toString mustEqual description
      }
    }
  }

}
