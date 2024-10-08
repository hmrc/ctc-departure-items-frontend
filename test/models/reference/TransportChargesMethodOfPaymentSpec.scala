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
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class TransportChargesMethodOfPaymentSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "MethodOfPayment" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (method, description) =>
          val methodOfPayment = TransportChargesMethodOfPayment(method, description)
          Json.toJson(methodOfPayment) mustBe Json.parse(s"""
               |{
               |  "method": "$method",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }

    "must deserialise" in {
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
            .as[TransportChargesMethodOfPayment] mustBe methodOfPayment
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (method, description) =>
          val methodOfPayment = TransportChargesMethodOfPayment(method, description)
          methodOfPayment.toString mustBe description
      }
    }
  }

}
