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
import generators.Generators
import models.PackingType
import models.PackingType._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class PackageTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "PackageType" - {

    "must serialise" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val packageType = PackageType(code, Some(description), Bulk)
            Json.toJson(packageType) mustBe Json.parse(s"""
              |{
              |  "code": "$code",
              |  "description": "$description",
              |  "type": "Bulk"
              |}
              |""".stripMargin)
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr) {
          code =>
            val packageType = PackageType(code, None, Unpacked)
            Json.toJson(packageType) mustBe Json.parse(s"""
              |{
              |  "code": "$code",
              |  "type": "Unpacked"
              |}
              |""".stripMargin)
        }
      }

    }

    "must deserialise" - {
      "when description defined" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val json = Json.parse(s"""
              |{
              |  "code": "$code",
              |  "description": "$description",
              |  "type": "Bulk"
              |}
              |""".stripMargin)
            json.as[PackageType] mustBe PackageType(code, Some(description), Bulk)
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr) {
          code =>
            val json = Json.parse(s"""
              |{
              |  "code": "$code",
              |  "type": "Unpacked"
              |}
              |""".stripMargin)
            json.as[PackageType] mustBe PackageType(code, None, Unpacked)
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.option(Gen.alphaNumStr), arbitrary[Boolean], arbitrary[PackingType]) {
        (code, description, selected, packingType) =>
          val packageType = PackageType(code, description, packingType)
          packageType.toSelectItem(selected) mustBe SelectItem(Some(code), s"$packageType", selected)
      }
    }

    "must format as string" - {
      "when description defined and non-empty" in {
        forAll(Gen.alphaNumStr, nonEmptyString, arbitrary[PackingType]) {
          (code, description, packingType) =>
            val packageType = PackageType(code, Some(description), packingType)
            packageType.toString mustBe s"($code) $description"
        }
      }

      "when description defined and empty" in {
        forAll(Gen.alphaNumStr, arbitrary[PackingType]) {
          (code, packingType) =>
            val packageType = PackageType(code, Some(""), packingType)
            packageType.toString mustBe code
        }
      }

      "when description undefined" in {
        forAll(Gen.alphaNumStr, arbitrary[PackingType]) {
          (code, packingType) =>
            val packageType = PackageType(code, None, packingType)
            packageType.toString mustBe code
        }
      }

      "when description contains html" in {
        val packageType = PackageType("VY", Some("Bulk, solid, large particles (&quot;nodules&quot;)"), PackingType.Bulk)
        packageType.toString mustBe "(VY) Bulk, solid, large particles (\"nodules\")"
      }
    }
  }

}
