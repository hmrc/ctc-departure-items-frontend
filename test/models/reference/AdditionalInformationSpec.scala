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
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import play.api.test.Helpers.running
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class AdditionalInformationSpec extends SpecBase with ScalaCheckPropertyChecks {

  "Additional Information" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val additionalInformation = AdditionalInformation(code, description)
          Json.toJson(additionalInformation) mustEqual Json.parse(s"""
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
          running(_.configure("feature-flags.phase-6-enabled" -> false)) {
            app =>
              val config                                       = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[AdditionalInformation] = AdditionalInformation.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val additionalInformation = AdditionalInformation(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "code": "$code",
                         |  "description": "$description"
                         |}
                         |""".stripMargin)
                    .as[AdditionalInformation] mustEqual additionalInformation
              }
          }
        }

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config                                       = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[AdditionalInformation] = AdditionalInformation.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val additionalInformation = AdditionalInformation(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "key": "$code",
                         |  "value": "$description"
                         |}
                         |""".stripMargin)
                    .as[AdditionalInformation] mustEqual additionalInformation
              }
          }
        }
      }
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val additionalInformation = AdditionalInformation(code, description)
            Json
              .parse(s"""
                   |{
                   |  "code": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[AdditionalInformation] mustEqual additionalInformation
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
        (code, description, selected) =>
          val additionalInformation = AdditionalInformation(code, description)
          additionalInformation.toSelectItem(selected) mustEqual SelectItem(Some(code), s"($code) $description", selected)
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr) {
        description =>
          val additionalInformation = AdditionalInformation("code", description)
          additionalInformation.toString mustEqual s"(code) $description"
      }
    }
  }

}
