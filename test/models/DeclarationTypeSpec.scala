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

import generators.Generators
import models.DeclarationType._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class DeclarationTypeSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with OptionValues with Generators {

  "DeclarationType" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(DeclarationType.values)

      forAll(gen) {
        declarationType =>
          JsString(declarationType.toString).validate[DeclarationType].asOpt.value mustEqual declarationType
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!DeclarationType.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[DeclarationType] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(DeclarationType.values)

      forAll(gen) {
        declarationType =>
          Json.toJson(declarationType) mustEqual JsString(declarationType.toString)
      }
    }

    "values" - {
      "must return all declaration types" in {
        DeclarationType.values mustBe Seq(T1, T2, T2F, TIR, T)
      }
    }

    "valuesU" - {
      "must return T1, T2 and T2F" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            DeclarationType.itemValues mustBe Seq(T1, T2, T2F)
        }
      }
    }
  }
}
