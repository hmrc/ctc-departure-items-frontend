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

package services

import base.SpecBase
import models.{Document, SelectableList}
import play.api.libs.json.{JsObject, Json}

class DocumentsServiceSpec extends SpecBase {

  private val service = injector.instanceOf[DocumentsService]

  "Documents Service" - {

    "getDocuments" - {

      "must return some documents and filter out any documents already selected" - {
        "when documents present in user answers and documents selected" in {
          val json = Json
            .parse("""
              |{
              |  "documents" : [
              |    {
              |      "previousDocumentType" : {
              |        "type" : "Type 1",
              |        "code" : "Code 1",
              |        "description" : "Description 1"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 1"
              |      }
              |    },
              |    {
              |      "type" : {
              |        "type" : "Type 2",
              |        "code" : "Code 2"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 2"
              |      }
              |    }
              |  ],
              |  "items" : [
              |    {
              |      "documents" : [
              |        {
              |          "document" : {
              |            "type" : "Type 1",
              |            "code" : "Code 1",
              |            "description" : "Description 1",
              |            "referenceNumber" : "Ref no. 1"
              |          }
              |        }
              |      ]
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex)

          result.value mustBe SelectableList(
            Seq(
              Document("Type 2", "Code 2", None, "Ref no. 2")
            )
          )
        }

        "when documents present in user answers and no documents selected" in {
          val json = Json
            .parse("""
              |{
              |  "documents" : [
              |    {
              |      "previousDocumentType" : {
              |        "type" : "Type 1",
              |        "code" : "Code 1",
              |        "description" : "Description 1"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 1"
              |      }
              |    },
              |    {
              |      "type" : {
              |        "type" : "Type 2",
              |        "code" : "Code 2"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 2"
              |      }
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex)

          result.value mustBe SelectableList(
            Seq(
              Document("Type 1", "Code 1", Some("Description 1"), "Ref no. 1"),
              Document("Type 2", "Code 2", None, "Ref no. 2")
            )
          )
        }
      }

      "must return None" - {
        "when empty list of documents" in {
          val result = service.getDocuments(emptyUserAnswers, itemIndex)

          result mustBe None
        }

        "when data is in an invalid shape" in {
          val json = Json
            .parse("""
              |{
              |  "documents" : [
              |    {
              |      "foo" : "bar"
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex)

          result mustBe Some(SelectableList(List()))
        }
      }
    }
  }

}
