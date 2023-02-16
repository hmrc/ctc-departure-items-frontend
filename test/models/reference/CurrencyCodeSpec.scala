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

package models.reference

import base.SpecBase
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class CurrencyCodeSpec extends SpecBase with ScalaCheckPropertyChecks {

  "CurrencyCode" - {

    "must serialise" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (currency, description) =>
            val currencyCode = CurrencyCode(currency, Some(description))
            Json.toJson(currencyCode) mustBe Json.parse(s"""
              |{
              |  "currency": "$currency",
              |  "description": "$description"
              |}
              |""".stripMargin)
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr) {
          currency =>
            val currencyCode = CurrencyCode(currency, None)
            Json.toJson(currencyCode) mustBe Json.parse(s"""
              |{
              |  "currency": "$currency"
              |}
              |""".stripMargin)
        }
      }

    }

    "must deserialise" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (currency, description) =>
            val json = Json.parse(s"""
              |{
              |  "currency": "$currency",
              |  "description": "$description"
              |}
              |""".stripMargin)
            json.as[CurrencyCode] mustBe CurrencyCode(currency, Some(description))
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr) {
          currency =>
            val json = Json.parse(s"""
              |{
              |  "currency": "$currency"
              |}
              |""".stripMargin)
            json.as[CurrencyCode] mustBe CurrencyCode(currency, None)
        }
      }
    }

    "must convert to select item" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
          (currency, description, selected) =>
            val currencyCode = CurrencyCode(currency, Some(description))
            currencyCode.toSelectItem(selected) mustBe SelectItem(Some(currency), s"$currency - $description", selected)
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr, arbitrary[Boolean]) {
          (currency, selected) =>
            val currencyCode = CurrencyCode(currency, None)
            currencyCode.toSelectItem(selected) mustBe SelectItem(Some(currency), currency, selected)
        }
      }
    }

    "must format as string" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (currency, description) =>
            val currencyCode = CurrencyCode(currency, Some(description))
            currencyCode.toString mustBe s"$currency - $description"
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr) {
          currency =>
            val currencyCode = CurrencyCode(currency, None)
            currencyCode.toString mustBe currency
        }
      }
    }

    "must convert currency code to a symbol" - {
      "when EUR must return €" in {
        val currencyCode = CurrencyCode("EUR", None)
        currencyCode.symbol mustBe "€"
      }

      "when GBP must return £" in {
        val currencyCode = CurrencyCode("GBP", None)
        currencyCode.symbol mustBe "£"
      }

      "when unknown must return code" in {
        val currencyCode = CurrencyCode("blah", None)
        currencyCode.symbol mustBe "blah"
      }
    }
  }

}
