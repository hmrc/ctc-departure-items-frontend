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

package models

import base.SpecBase
import config.TestConstants.declarationTypeValues
import generators.Generators
import models.DeclarationTypeItemLevel._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class DeclarationTypeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with Generators with SpecBase {

  "DeclarationType" - {

    "must deserialise valid values" in {

      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val declarationType = DeclarationTypeItemLevel(code, description)
          Json
            .parse(s"""
                 |{
                 |  "code": "$code",
                 |  "description": "$description"
                 |}
                 |""".stripMargin)
            .as[DeclarationTypeItemLevel] mustBe declarationType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!declarationTypeValues.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[DeclarationTypeItemLevel] mustEqual JsError("error.expected.jsobject")
      }
    }

    "must serialise" in {
      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val declarationType = DeclarationTypeItemLevel(code, description)
          Json.toJson(declarationType) mustBe Json.parse(s"""
               |{
               |  "code": "$code",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }

  }

}
