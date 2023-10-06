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

package config

object Constants {

  object CountryCode {
    val GB = "GB"
    val AD = "AD"
  }

  object AdditionalReference {
    val C651 = "C651"
    val C658 = "C658"
  }

  object DeclarationType {
    val T1  = "T1"
    val T2  = "T2"
    val T2F = "T2F"
    val T   = "T"
    val TIR = "TIR"
  }

  object AdditionalInformation {
    val Type30600 = "30600"
  }

  object SecurityType {
    val NoSecurityDetails = "0"
  }
}
