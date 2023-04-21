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
           |  	  "code" : "1",
           |      "uuid" : "8e5a3f69-7d6d-490a-8071-002b1d35d3c1"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo1"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Transport",
           |  	  "code" : "2",
           |      "uuid" : "5e6fe4c6-f09f-4a95-b892-47a092a3b027"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo2"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Previous",
           |  	  "code" : "3",
           |      "uuid" : "d5d5832c-b754-47b7-9f59-4f69b7361902"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo3"
           |    }
           |  },
           |  {
           |    "previousDocumentType" : {
           |  	  "type" : "Previous",
           |  	  "code" : "4",
           |      "uuid" : "4b36f3af-665b-46a5-9a04-c8342654b57a"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo4"
           |    }
           |  }
           |]
           |""".stripMargin)

      val result = json.as[SelectableList[Document]]

      result mustBe SelectableList(
        Seq(
          Document(
            uuid = UUID.fromString("8e5a3f69-7d6d-490a-8071-002b1d35d3c1"),
            `type` = "Support",
            code = "1",
            description = None,
            referenceNumber = "RefNo1"
          ),
          Document(
            uuid = UUID.fromString("5e6fe4c6-f09f-4a95-b892-47a092a3b027"),
            `type` = "Transport",
            code = "2",
            description = None,
            referenceNumber = "RefNo2"
          ),
          Document(
            uuid = UUID.fromString("d5d5832c-b754-47b7-9f59-4f69b7361902"),
            `type` = "Previous",
            code = "3",
            description = None,
            referenceNumber = "RefNo3"
          ),
          Document(
            uuid = UUID.fromString("4b36f3af-665b-46a5-9a04-c8342654b57a"),
            `type` = "Previous",
            code = "4",
            description = None,
            referenceNumber = "RefNo4"
          )
        )
      )
    }
  }
}
