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

package models.reference

import cats.Order
import config.FrontendAppConfig
import models.Selectable
import play.api.libs.functional.syntax.*
import play.api.libs.json.{__, Json, OFormat, Reads}

case class AdditionalReference(documentType: String, description: String) extends Selectable {

  override def toString: String = s"($documentType) $description"

  override val value: String = documentType
}

object AdditionalReference {

  def reads(config: FrontendAppConfig): Reads[AdditionalReference] =
    if (config.isPhase6Enabled) {
      (
        (__ \ "key").read[String] and
          (__ \ "value").read[String]
      )(AdditionalReference.apply)
    } else {
      Json.reads[AdditionalReference]
    }

  implicit val format: OFormat[AdditionalReference] = Json.format[AdditionalReference]

  implicit val order: Order[AdditionalReference] = (x: AdditionalReference, y: AdditionalReference) => (x, y).compareBy(_.description, _.documentType)
}
