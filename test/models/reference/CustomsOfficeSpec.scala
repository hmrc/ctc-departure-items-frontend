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

class CustomsOfficeSpec extends SpecBase with ScalaCheckPropertyChecks {

  "CustomsOffice" - {

    "must serialise" - {
      "when phone number defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, phoneNumber) =>
            val customsOffice = CustomsOffice(id, name, Some(phoneNumber))
            Json.toJson(customsOffice) mustBe Json.parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "phoneNumber": "$phoneNumber"
                |}
                |""".stripMargin)
        }
      }

      "when phone number undefined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name) =>
            val customsOffice = CustomsOffice(id, name, None)
            Json.toJson(customsOffice) mustBe Json.parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name"
                |}
                |""".stripMargin)
        }
      }
    }

    "must deserialise" - {
      "when phone number defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name, phoneNumber) =>
            val customsOffice = CustomsOffice(id, name, Some(phoneNumber))
            Json
              .parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name",
                |  "phoneNumber": "$phoneNumber"
                |}
                |""".stripMargin)
              .as[CustomsOffice] mustBe customsOffice
        }
      }

      "when phone number undefined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (id, name) =>
            val customsOffice = CustomsOffice(id, name, None)
            Json
              .parse(s"""
                |{
                |  "id": "$id",
                |  "name": "$name"
                |}
                |""".stripMargin)
              .as[CustomsOffice] mustBe customsOffice
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
        (id, name, selected) =>
          val customsOffice = CustomsOffice(id, name, None)
          customsOffice.toSelectItem(selected) mustBe SelectItem(Some(id), s"$name ($id)", selected)
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (id, name) =>
          val customsOffice = CustomsOffice(id, name, None)
          customsOffice.toString mustBe s"$name ($id)"
      }
    }
  }

}
