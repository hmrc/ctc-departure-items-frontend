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
import org.scalacheck.Arbitrary.arbitrary
import config.FrontendAppConfig
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import play.api.test.Helpers.running
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class AdditionalReferenceSpec extends SpecBase with ScalaCheckPropertyChecks {

  "Additional Reference" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (documentType, description) =>
          val additionalReference = AdditionalReference(documentType, description)
          Json.toJson(additionalReference) mustEqual Json.parse(s"""
              |{
              |  "documentType": "$documentType",
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
              val config                                     = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[AdditionalReference] = AdditionalReference.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (documentType, description) =>
                  val additionalReference = AdditionalReference(documentType, description)
                  Json
                    .parse(s"""
                         |{
                         |  "documentType": "$documentType",
                         |  "description": "$description"
                         |}
                         |""".stripMargin)
                    .as[AdditionalReference] mustEqual additionalReference
              }
          }
        }

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config                                     = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[AdditionalReference] = AdditionalReference.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (documentType, description) =>
                  val additionalReference = AdditionalReference(documentType, description)
                  Json
                    .parse(s"""
                         |{
                         |  "key": "$documentType",
                         |  "value": "$description"
                         |}
                         |""".stripMargin)
                    .as[AdditionalReference] mustEqual additionalReference
              }
          }
        }
      }
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (documentType, description) =>
            val additionalReference = AdditionalReference(documentType, description)
            Json
              .parse(s"""
                   |{
                   |  "documentType": "$documentType",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[AdditionalReference] mustEqual additionalReference
        }
      }
    }

    "must convert to select item" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr, arbitrary[Boolean]) {
        (documentType, description, selected) =>
          val additionalReference = AdditionalReference(documentType, description)
          additionalReference.toSelectItem(selected) mustEqual SelectItem(Some(documentType), s"($documentType) $description", selected)
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr) {
        description =>
          val additionalReference = AdditionalReference("documentType", description)
          additionalReference.toString mustEqual s"(documentType) $description"
      }
    }
  }

}
