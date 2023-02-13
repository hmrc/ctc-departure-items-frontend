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

import models.domain.StringFieldRegex._
import play.api.i18n.Messages

import scala.util.matching.Regex

sealed trait AddressLine {
  val field: String
  def arg(implicit messages: Messages): String = messages(s"address.$field")
}

object AddressLine {

  case object Country extends AddressLine {
    override val field: String = "country"
  }

  sealed trait AddressLineWithValidation extends AddressLine {
    val length: Int
    val regex: Regex
  }

  case object StreetNumber extends AddressLineWithValidation {
    override val field: String = "streetNumber"
    override val length: Int   = 17
    override val regex: Regex  = alphaNumericRegex
  }

  case object NumberAndStreet extends AddressLineWithValidation {
    override val field: String = "numberAndStreet"
    override val length: Int   = 70
    override val regex: Regex  = stringFieldRegex
  }

  case object City extends AddressLineWithValidation {
    override val field: String = "city"
    override val length: Int   = 35
    override val regex: Regex  = stringFieldRegex
  }

  case object PostalCode extends AddressLineWithValidation {
    override val field: String = "postalCode"
    override val length: Int   = 17
    override val regex: Regex  = alphaNumericWithSpacesRegex
  }
}
