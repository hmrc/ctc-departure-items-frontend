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

import base.SpecBase
import play.api.libs.json.Json

import java.util.UUID

class SelectableListSpec extends SpecBase {

  "documentsReads" - {
    "must read JsArray as list of documents" in {
      val json = Json.parse(s"""
           |[
           |  {
           |    "type" : {
           |  	  "type" : "Support",
           |  	  "code" : "1"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo1",
           |      "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Transport",
           |  	  "code" : "2"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo2",
           |      "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Previous",
           |  	  "code" : "3"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo3",
           |      "uuid" : "3882459f-b7bc-478d-9d24-359533aa8fe3"
           |    }
           |  },
           |  {
           |    "previousDocumentType" : {
           |  	  "type" : "Previous",
           |  	  "code" : "4"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo4",
           |      "uuid" : "865e9cbe-be12-4e3e-96db-e8394c4356ec"
           |    }
           |  }
           |]
           |""".stripMargin)

      val result = json.as[SelectableList[Document]](SelectableList.documentsReads)

      result mustBe SelectableList(
        Seq(
          Document(
            `type` = "Support",
            code = "1",
            description = None,
            referenceNumber = "RefNo1",
            uuid = UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
          ),
          Document(
            `type` = "Transport",
            code = "2",
            description = None,
            referenceNumber = "RefNo2",
            uuid = UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a")
          ),
          Document(
            `type` = "Previous",
            code = "3",
            description = None,
            referenceNumber = "RefNo3",
            uuid = UUID.fromString("3882459f-b7bc-478d-9d24-359533aa8fe3")
          ),
          Document(
            `type` = "Previous",
            code = "4",
            description = None,
            referenceNumber = "RefNo4",
            uuid = UUID.fromString("865e9cbe-be12-4e3e-96db-e8394c4356ec")
          )
        )
      )
    }
  }

  "itemDocumentsReads" - {
    "must read JsArray as list of documents" in {
      val json = Json.parse(s"""
           |[
           |  {
           |    "document" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
           |  },
           |  {
           |    "document" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
           |  }
           |]
           |""".stripMargin)

      val result = json.as[Seq[UUID]](SelectableList.itemDocumentUuidsReads)

      result mustBe Seq(
        UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe"),
        UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a")
      )
    }
  }
}
