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

import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import config.FrontendAppConfig
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import play.api.test.Helpers.running

class TransportChargesMethodOfPaymentSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

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
          running(_.configure("feature-flags.phase-6-enabled" -> false)) {
            app =>
              val config                                                 = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[TransportChargesMethodOfPayment] = TransportChargesMethodOfPayment.reads(config)
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

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config                                                 = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[TransportChargesMethodOfPayment] = TransportChargesMethodOfPayment.reads(config)
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
