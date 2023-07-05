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
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import play.api.libs.functional.syntax._

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

  def httpReads(`type`: PackingType): HttpReads[Seq[PackageType]] = (_: String, _: String, response: HttpResponse) => {
    val referenceData: JsValue = (response.json \ "data").getOrElse(
      throw new IllegalStateException("[Document][httpReads] Reference data could not be parsed")
    )

    referenceData match {
      case JsArray(values) =>
        values.flatMap(_.validate[PackageType](referenceDataReads(`type`)).asOpt).toSeq
      case _ =>
        Nil
    }
  }

  private def referenceDataReads(`type`: PackingType): Reads[PackageType] = (
    (__ \ "code").read[String] and
      (__ \ "description").readNullable[String]
  ).apply {
    (code, description) =>
      PackageType(code, description, `type`)
  }

  implicit val format: OFormat[PackageType] = Json.format[PackageType]
}
