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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsString, Json}
import utils.Format._

import java.time.LocalDateTime

class DateTimeSpec extends AnyFreeSpec with Matchers with OptionValues with Generators with ScalaCheckPropertyChecks {

  "DateTime" - {

    "must serialise" in {

      forAll(arbitrary[DateTime]) {
        dateTime =>
          val formattedDate = dateTime.toLocalDateTime.toIE015Format

          Json.toJson(dateTime) mustEqual JsString(formattedDate)
      }
    }

    "must deserialise valid values" in {

      val localDateTime = arbitrary[LocalDateTime].sample.value

      val localDateTimeWithoutNano = localDateTime.minusNanos(localDateTime.getNano)

      val formattedDate = localDateTimeWithoutNano.toIE015Format

      val expectedResult = DateTime(localDateTime.toLocalDate, localDateTimeWithoutNano.toLocalTime)

      JsString(formattedDate).validate[DateTime].asOpt.value mustEqual expectedResult
    }

    "must fail to deserialise invalid values" in {

      JsString("invalidString").validate[DateTime].isError mustBe true

    }
  }
}
