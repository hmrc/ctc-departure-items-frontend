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

import play.api.libs.json._

import java.util.UUID

final case class Id(uuid: String)

object Id {

  def apply() =
    new Id(UUID.randomUUID().toString)

  implicit def reads: Reads[Id] =
    __.read[String].map(Id.apply)

  implicit def writes: Writes[Id] = Writes {
    id =>
      JsString(id.uuid)
  }
}
