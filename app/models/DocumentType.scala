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

import play.api.libs.json.*

sealed trait DocumentType {
  val display: String
  def isSupport: Boolean   = this == DocumentType.Support
  def isTransport: Boolean = this == DocumentType.Transport
  def isPrevious: Boolean  = this == DocumentType.Previous
}

object DocumentType {

  case object Support extends DocumentType {
    val display = "Supporting"
  }

  case object Transport extends DocumentType {
    val display = "Transport"
  }

  case object Previous extends DocumentType {
    val display = "Previous"
  }

  val values: Seq[DocumentType] = Seq(Support, Transport, Previous)

  implicit val reads: Reads[DocumentType] = Reads {
    case JsString("Support")   => JsSuccess(Support)
    case JsString("Transport") => JsSuccess(Transport)
    case JsString("Previous")  => JsSuccess(Previous)
    case _                     => JsError("Unexpected document type")
  }

  implicit val writes: Writes[DocumentType] = Writes {
    case Support   => JsString("Support")
    case Transport => JsString("Transport")
    case Previous  => JsString("Previous")
  }
}
