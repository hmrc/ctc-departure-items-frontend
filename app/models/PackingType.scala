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

import play.api.libs.json._

sealed trait PackingType

object PackingType {
  case object Bulk extends PackingType
  case object Unpacked extends PackingType
  case object Other extends PackingType

  val values: Seq[PackingType] = Seq(Bulk, Unpacked, Other)

  implicit val reads: Reads[PackingType] = Reads {
    case JsString("Bulk")     => JsSuccess(Bulk)
    case JsString("Unpacked") => JsSuccess(Unpacked)
    case JsString("Other")    => JsSuccess(Other)
    case _                    => JsError("Unexpected packing type")
  }

  implicit val writes: Writes[PackingType] = Writes {
    case Bulk     => JsString("Bulk")
    case Unpacked => JsString("Unpacked")
    case Other    => JsString("Other")
  }
}
