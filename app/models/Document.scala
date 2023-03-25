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
import play.api.libs.json.{__, Reads}

sealed trait Document {
  val code: String
  val description: Option[String]
  val referenceNumber: String
}

object Document {

  case class TransportDocument(
    code: String,
    description: Option[String],
    referenceNumber: String
  ) extends Document

  case class SupportDocument(
    code: String,
    description: Option[String],
    referenceNumber: String,
    lineItemNumber: Option[Int]
  ) extends Document

  case class PreviousDocument(
    code: String,
    description: Option[String],
    referenceNumber: String
  ) extends Document

  implicit val reads: Reads[Document] = {
    def previousDocumentReads(key: String): Reads[Document] = (
      (__ \ key \ "code").read[String] and
        (__ \ key \ "description").readNullable[String] and
        (__ \ "details" \ "documentReferenceNumber").read[String]
    )(PreviousDocument.apply _)

    lazy val genericReads: Reads[Document] =
      (__ \ "type" \ "type").read[String].flatMap {
        case "Transport" =>
          (
            (__ \ "type" \ "code").read[String] and
              (__ \ "type" \ "description").readNullable[String] and
              (__ \ "details" \ "documentReferenceNumber").read[String]
          )(TransportDocument.apply _)
        case "Support" =>
          (
            (__ \ "type" \ "code").read[String] and
              (__ \ "type" \ "description").readNullable[String] and
              (__ \ "details" \ "documentReferenceNumber").read[String] and
              (__ \ "details" \ "lineItemNumber").readNullable[Int]
          )(SupportDocument.apply _)
        case "Previous" =>
          previousDocumentReads("type")
      }

    genericReads orElse previousDocumentReads("previousDocumentType")
  }
}
