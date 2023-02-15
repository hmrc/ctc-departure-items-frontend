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

package utils

import base.SpecBase
import utils.Format.{RichLocalDate, RichLocalDateTime}

import java.time.{LocalDate, LocalDateTime}

class FormatSpec extends SpecBase {

  "RichLocalDate" - {
    "formatAsString" - {
      "must format date like d MMMM yyyy" - {
        "when day is 1 to 9" in {
          val date   = LocalDate.of(2023, 1, 5)
          val result = date.formatAsString
          result mustBe "5 January 2023"
        }

        "when day is 10+" in {
          val date   = LocalDate.of(2023, 1, 13)
          val result = date.formatAsString
          result mustBe "13 January 2023"
        }
      }
    }

    "formatForHint" - {
      "must format date like dd MM yyyy" - {
        "when day is 1 to 9" in {
          val date   = LocalDate.of(2023, 1, 5)
          val result = date.formatForText
          result mustBe "05 01 2023"
        }

        "when day is 10+" in {
          val date   = LocalDate.of(2023, 1, 13)
          val result = date.formatForText
          result mustBe "13 01 2023"
        }
      }
    }
  }

  "RichLocalDateTime" - {
    "formatAsString" - {
      "must format date/time like d MMMM yyyy HH:mm" - {
        "when day is 1 to 9" - {
          val date   = LocalDateTime.of(2023, 1, 5, 21, 30)
          val result = date.formatAsString
          result mustBe "5 January 2023 21:30"
        }

        "when day is 10+" in {
          val date   = LocalDateTime.of(2023, 1, 13, 21, 30)
          val result = date.formatAsString
          result mustBe "13 January 2023 21:30"
        }
      }
    }
  }

}
