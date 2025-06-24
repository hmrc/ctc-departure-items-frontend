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
import generators.Generators
import config.FrontendAppConfig
import models.PackingType
import models.PackingType.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import play.api.test.Helpers.running
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class PackageTypeSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "PackageType" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val packageType = PackageType(code, description, Bulk)
          Json.toJson(packageType) mustEqual Json.parse(s"""
               |{
               |  "code": "$code",
               |  "description": "$description",
               |  "type": "Bulk"
               |}
               |""".stripMargin)
      }
    }

    "must deserialise" - {
      "when reading from reference data" - {
        "when phase 5" in {
          running(_.configure("feature-flags.phase-6-enabled" -> false)) {
            app =>
              val config                             = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[PackageType] = PackageType.reads(Bulk)(config)
              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val json = Json.parse(s"""
                       |{
                       |  "code": "$code",
                       |  "description": "$description",
                       |  "type": "Bulk"
                       |}
                       |""".stripMargin)
                  json.as[PackageType] mustEqual PackageType(code, description, Bulk)
              }
          }
        }

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config                             = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[PackageType] = PackageType.reads(Bulk)(config)
              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val json = Json.parse(s"""
                       |{
                       |  "key": "$code",
                       |  "value": "$description",
                       |  "type": "Bulk"
                       |}
                       |""".stripMargin)
                  json.as[PackageType] mustEqual PackageType(code, description, Bulk)
              }
          }
        }
      }

      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val json = Json.parse(s"""
                 |{
                 |  "code": "$code",
                 |  "description": "$description",
                 |  "type": "Bulk"
                 |}
                 |""".stripMargin)
            json.as[PackageType] mustEqual PackageType(code, description, Bulk)
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean], arbitrary[PackingType]) {
        (code, description, selected, packingType) =>
          val packageType = PackageType(code, description, packingType)
          packageType.toSelectItem(selected) mustEqual SelectItem(Some(code), s"$packageType", selected)
      }
    }

    "must format as string" - {
      "when description defined and non-empty" in {
        forAll(Gen.alphaNumStr, nonEmptyString, arbitrary[PackingType]) {
          (code, description, packingType) =>
            val packageType = PackageType(code, description, packingType)
            packageType.toString mustEqual s"($code) $description"
        }
      }

      "when description contains html" in {
        val packageType = PackageType("VY", "Bulk, solid, large particles (&quot;nodules&quot;)", PackingType.Bulk)
        packageType.toString mustEqual "(VY) Bulk, solid, large particles (\"nodules\")"
      }
    }
  }

}
