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
           |      "documentReferenceNumber" : "RefNo1"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Transport",
           |  	  "code" : "2"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo2"
           |    }
           |  },
           |  {
           |    "type" : {
           |  	  "type" : "Previous",
           |  	  "code" : "3"
           |    },
           |    "details" : {
           |      "documentReferenceNumber" : "RefNo3"
           |    }
           |  },
           |  {
           |    "previousDocumentType" : {
           |  	  "type" : "Previous",
           |  	  "code" : "4"
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
            `type` = "Support",
            code = "1",
            description = None,
            referenceNumber = "RefNo1"
          ),
          Document(
            `type` = "Transport",
            code = "2",
            description = None,
            referenceNumber = "RefNo2"
          ),
          Document(
            `type` = "Previous",
            code = "3",
            description = None,
            referenceNumber = "RefNo3"
          ),
          Document(
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
