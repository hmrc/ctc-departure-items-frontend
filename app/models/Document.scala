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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.util.UUID

case class Document(
  attachToAllItems: Boolean,
  `type`: DocumentType,
  code: String,
  description: Option[String],
  referenceNumber: String,
  uuid: UUID
) extends Selectable {

  override def toString: String = description match {
    case Some(value) => s"${`type`.display} - ($code) $value - $referenceNumber"
    case None        => s"${`type`.display} - $code - $referenceNumber"
  }

  override val value: String = this.toString
}

object Document {

  implicit val reads: Reads[Document] = {

    def readsForKey(key: String): Reads[Document] = (
      ((__ \ "attachToAllItems").read[Boolean] orElse (__ \ "inferredAttachToAllItems").read[Boolean]) and
        (__ \ key \ "type").read[DocumentType] and
        (__ \ key \ "code").read[String] and
        (__ \ key \ "description").readNullable[String] and
        (__ \ "details" \ "documentReferenceNumber").read[String] and
        (__ \ "details" \ "uuid").read[UUID]
    )(Document.apply)

    readsForKey("type") orElse readsForKey("previousDocumentType")
  }

  implicit val documentsReads: Reads[Seq[Document]] = Reads {
    case x: JsArray =>
      JsSuccess(x.validateAsListOf[Document])
    case _ =>
      JsError("documentsReads: Failed to read documents from cache")
  }

  implicit val itemDocumentsReads: Reads[Seq[UUID]] = {
    case JsArray(values) =>
      JsSuccess(
        values.flatMap {
          value => (value \ "document").validate[UUID].asOpt
        }.toSeq
      )
    case _ =>
      JsError("itemDocumentReads: Failed to read document UUIDs from cache")
  }

  implicit class RichDocuments(value: Seq[Document]) {

    def isConsignmentPreviousDocumentPresent: Boolean =
      value.exists {
        document => document.attachToAllItems && document.`type`.isPrevious
      }

    def noConsignmentPreviousDocumentPresent: Boolean =
      !isConsignmentPreviousDocumentPresent
  }
}
