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

import models.{PackingType, Selectable}
import org.apache.commons.text.StringEscapeUtils
import play.api.libs.json.{Json, OFormat}

case class PackageType(code: String, description: Option[String], `type`: PackingType) extends Selectable {

  override def toString: String = {
    val str = description match {
      case Some(value) if value.trim.nonEmpty => s"($code) $value"
      case _                                  => code
    }
    StringEscapeUtils.unescapeXml(str)
  }

  override val value: String = code
}

object PackageType {
  implicit val format: OFormat[PackageType] = Json.format[PackageType]
}
