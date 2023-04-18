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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Document(
  index: Int,
  `type`: String,
  code: String,
  description: Option[String],
  referenceNumber: String
) extends Selectable {

  override def toString: String = description match {
    case Some(value) => s"($code) $value - $referenceNumber"
    case None        => s"$code - $referenceNumber"
  }

  override val value: String = index.toString
}

object Document {

  def reads(index: Int): Reads[Document] = {

    def readsForKey(key: String): Reads[Document] = (
      (index: Reads[Int]) and
        (__ \ key \ "type").read[String] and
        (__ \ key \ "code").read[String] and
        (__ \ key \ "description").readNullable[String] and
        (__ \ "details" \ "documentReferenceNumber").read[String]
    )(Document.apply _)

    readsForKey("type") orElse readsForKey("previousDocumentType")
  }

  implicit val writes: Writes[Document] = Json.writes[Document]
}
