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

package models.reference

import cats.Order
import play.api.libs.json._

case class CountryCode(code: String)

object CountryCode {

  object Constants {
    val countryCodeLength = 2
  }

  implicit val countryCodeWrites: Writes[CountryCode] = (countryCode: CountryCode) => JsString(countryCode.code)

  implicit val countryCodeReads: Reads[CountryCode] = {
    case JsObject(mapping) => JsSuccess(CountryCode(mapping("code").as[String]))
    case JsString(code)    => JsSuccess(CountryCode(code))
    case x                 => JsError(s"Expected a string, got a ${x.getClass}")
  }

  implicit val order: Order[CountryCode] = (x: CountryCode, y: CountryCode) => {
    (x, y).compareBy(_.code)
  }
}
