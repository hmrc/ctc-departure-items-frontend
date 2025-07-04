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
import models.{PackingType, Selectable}
import org.apache.commons.text.StringEscapeUtils
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class PackageType(code: String, description: String, `type`: PackingType) extends Selectable {

  override def toString: String = {
    val str = s"($code) $description"
    StringEscapeUtils.unescapeXml(str)
  }

  override val value: String = code
}

object PackageType {

  def reads(`type`: PackingType)(config: FrontendAppConfig): Reads[PackageType] = {
    val (codeField, descriptionField) =
      if (config.isPhase6Enabled) ("key", "value") else ("code", "description")

    (
      (__ \ codeField).read[String] and
        (__ \ descriptionField).read[String]
    )(
      (code, description) => PackageType(code, description, `type`)
    )
  }

  implicit val format: OFormat[PackageType] = Json.format[PackageType]

  implicit val order: Order[PackageType] = (x: PackageType, y: PackageType) => (x, y).compareBy(_.toString)
}
