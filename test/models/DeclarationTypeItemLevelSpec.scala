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

package models

import base.SpecBase
import config.FrontendAppConfig
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{Json, Reads}
import play.api.test.Helpers.running

class DeclarationTypeItemLevelSpec extends SpecBase with ScalaCheckPropertyChecks {

  "DeclarationTypeItemLevel" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val declarationTypeItemLevel = DeclarationTypeItemLevel(code, description)
          Json.toJson(declarationTypeItemLevel) mustEqual Json.parse(s"""
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
              val config                                          = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[DeclarationTypeItemLevel] = DeclarationTypeItemLevel.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val declarationTypeItemLevel = DeclarationTypeItemLevel(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "code": "$code",
                         |  "description": "$description"
                         |}
                         |""".stripMargin)
                    .as[DeclarationTypeItemLevel] mustEqual declarationTypeItemLevel
              }
          }
        }

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config                                          = app.injector.instanceOf[FrontendAppConfig]
              implicit val reads: Reads[DeclarationTypeItemLevel] = DeclarationTypeItemLevel.reads(config)

              forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
                (code, description) =>
                  val declarationTypeItemLevel = DeclarationTypeItemLevel(code, description)
                  Json
                    .parse(s"""
                         |{
                         |  "key": "$code",
                         |  "value": "$description"
                         |}
                         |""".stripMargin)
                    .as[DeclarationTypeItemLevel] mustEqual declarationTypeItemLevel
              }
          }
        }
      }
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
          (code, description) =>
            val declarationTypeItemLevel = DeclarationTypeItemLevel(code, description)
            Json
              .parse(s"""
                   |{
                   |  "code": "$code",
                   |  "description": "$description"
                   |}
                   |""".stripMargin)
              .as[DeclarationTypeItemLevel] mustEqual declarationTypeItemLevel
        }
      }
    }

    "must format as string" in {
      forAll(Gen.alphaNumStr) {
        description =>
          val declarationTypeItemLevel = DeclarationTypeItemLevel("code", description)
          declarationTypeItemLevel.toString mustEqual s"code - $description"
      }
    }
  }

}
