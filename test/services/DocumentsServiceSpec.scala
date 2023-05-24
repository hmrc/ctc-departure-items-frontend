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
import models.{Document, Index, SelectableList}
import play.api.libs.json.{JsObject, Json}

import java.util.UUID

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
              |      "attachToAllItems" : false,
              |      "previousDocumentType" : {
              |        "type" : "Type 1",
              |        "code" : "Code 1",
              |        "description" : "Description 1"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 1",
              |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
              |      }
              |    },
              |    {
              |      "attachToAllItems" : true,
              |      "type" : {
              |        "type" : "Type 3",
              |        "code" : "Code 3",
              |        "description" : "Description 3"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 3",
              |        "uuid" : "cc09f64b-e519-4b21-9961-243ba7cad1b7"
              |      }
              |    },
              |    {
              |      "attachToAllItems" : false,
              |      "type" : {
              |        "type" : "Type 2",
              |        "code" : "Code 2"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 2",
              |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
              |      }
              |    }
              |  ],
              |  "items" : [
              |    {
              |      "documents" : [
              |        {
              |          "document" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
              |        }
              |      ]
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex, None)

          result.value mustBe SelectableList(
            Seq(
              Document(attachToAllItems = false, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
            )
          )
        }

        "when documents present in user answers and no documents selected" in {
          val json = Json
            .parse("""
              |{
              |  "documents" : [
              |    {
              |      "attachToAllItems" : false,
              |      "previousDocumentType" : {
              |        "type" : "Type 1",
              |        "code" : "Code 1",
              |        "description" : "Description 1"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 1",
              |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
              |      }
              |    },
              |    {
              |      "attachToAllItems" : false,
              |      "type" : {
              |        "type" : "Type 2",
              |        "code" : "Code 2"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 2",
              |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
              |      }
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex, None)

          result.value mustBe SelectableList(
            Seq(
              Document(
                attachToAllItems = false,
                "Type 1",
                "Code 1",
                Some("Description 1"),
                "Ref no. 1",
                UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
              ),
              Document(attachToAllItems = false, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
            )
          )
        }
      }

      "must return None" - {
        "when empty list of documents" in {
          val result = service.getDocuments(emptyUserAnswers, itemIndex, None)

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

          val result = service.getDocuments(userAnswers, itemIndex, None)

          result mustBe Some(SelectableList(List()))
        }
      }
    }

    "getDocuments (including current document)" - {

      "must return some documents and filter out any documents already selected but not the one at current index" - {
        "when documents present in user answers and documents selected" - {
          "and at an already answered index" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : [
                  |    {
                  |      "attachToAllItems" : false,
                  |      "previousDocumentType" : {
                  |        "type" : "Type 1",
                  |        "code" : "Code 1",
                  |        "description" : "Description 1"
                  |      },
                  |      "details" : {
                  |        "documentReferenceNumber" : "Ref no. 1",
                  |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |      }
                  |    },
                  |    {
                  |      "attachToAllItems" : false,
                  |      "type" : {
                  |        "type" : "Type 2",
                  |        "code" : "Code 2"
                  |      },
                  |      "details" : {
                  |        "documentReferenceNumber" : "Ref no. 2",
                  |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                  |      }
                  |    }
                  |  ],
                  |  "items" : [
                  |    {
                  |      "documents" : [
                  |        {
                  |          "document" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |        }
                  |      ]
                  |    }
                  |  ]
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, Some(documentIndex))

            result.value mustBe SelectableList(
              Seq(
                Document(
                  attachToAllItems = false,
                  "Type 1",
                  "Code 1",
                  Some("Description 1"),
                  "Ref no. 1",
                  UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
                ),
                Document(attachToAllItems = false, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
              )
            )
          }

          "and not at an already answered index" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : [
                  |    {
                  |      "attachToAllItems" : false,
                  |      "previousDocumentType" : {
                  |        "type" : "Type 1",
                  |        "code" : "Code 1",
                  |        "description" : "Description 1"
                  |      },
                  |      "details" : {
                  |        "documentReferenceNumber" : "Ref no. 1",
                  |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |      }
                  |    },
                  |    {
                  |      "attachToAllItems" : false,
                  |      "type" : {
                  |        "type" : "Type 2",
                  |        "code" : "Code 2"
                  |      },
                  |      "details" : {
                  |        "documentReferenceNumber" : "Ref no. 2",
                  |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                  |      }
                  |    }
                  |  ],
                  |  "items" : [
                  |    {
                  |      "documents" : [
                  |        {
                  |          "document" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |        }
                  |      ]
                  |    }
                  |  ]
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, Some(Index(1)))

            result.value mustBe SelectableList(
              Seq(
                Document(attachToAllItems = false, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
              )
            )
          }
        }

        "when documents present in user answers and no documents selected" in {
          val json = Json
            .parse("""
              |{
              |  "documents" : [
              |    {
              |      "attachToAllItems" : false,
              |      "previousDocumentType" : {
              |        "type" : "Type 1",
              |        "code" : "Code 1",
              |        "description" : "Description 1"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 1",
              |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
              |      }
              |    },
              |    {
              |      "attachToAllItems" : false,
              |      "type" : {
              |        "type" : "Type 2",
              |        "code" : "Code 2"
              |      },
              |      "details" : {
              |        "documentReferenceNumber" : "Ref no. 2",
              |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
              |      }
              |    }
              |  ]
              |}
              |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getDocuments(userAnswers, itemIndex, Some(documentIndex))

          result.value mustBe SelectableList(
            Seq(
              Document(
                attachToAllItems = false,
                "Type 1",
                "Code 1",
                Some("Description 1"),
                "Ref no. 1",
                UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
              ),
              Document(attachToAllItems = false, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
            )
          )
        }
      }

      "must return None" - {
        "when empty list of documents" in {
          val result = service.getDocuments(emptyUserAnswers, itemIndex, Some(documentIndex))

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

          val result = service.getDocuments(userAnswers, itemIndex, Some(documentIndex))

          result mustBe Some(SelectableList(List()))
        }
      }
    }

    "getConsignmentLevelDocuments" - {

      "must return all consignment level documents" - {
        "when documents present in user answers" in {
          val json = Json
            .parse("""
                |{
                |  "documents" : [
                |    {
                |      "attachToAllItems" : false,
                |      "previousDocumentType" : {
                |        "type" : "Type 1",
                |        "code" : "Code 1",
                |        "description" : "Description 1"
                |      },
                |      "details" : {
                |        "documentReferenceNumber" : "Ref no. 1",
                |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                |      }
                |    },
                |    {
                |      "attachToAllItems" : true,
                |      "type" : {
                |        "type" : "Type 3",
                |        "code" : "Code 3"
                |      },
                |      "details" : {
                |        "documentReferenceNumber" : "Ref no. 3",
                |        "uuid" : "cc09f64b-e519-4b21-9961-243ba7cad1b7"
                |      }
                |    },
                |    {
                |      "attachToAllItems" : false,
                |      "type" : {
                |        "type" : "Type 2",
                |        "code" : "Code 2"
                |      },
                |      "details" : {
                |        "documentReferenceNumber" : "Ref no. 2",
                |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                |      }
                |    }
                |  ]
                |}
                |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getConsignmentLevelDocuments(userAnswers)

          result mustBe Seq(
            Document(attachToAllItems = true, "Type 3", "Code 3", None, "Ref no. 3", UUID.fromString("cc09f64b-e519-4b21-9961-243ba7cad1b7"))
          )

        }
      }
    }

    "getDocument" - {

      val json = Json
        .parse("""
          |{
          |  "documents" : [
          |    {
          |      "attachToAllItems" : true,
          |      "previousDocumentType" : {
          |        "type" : "Type 1",
          |        "code" : "Code 1",
          |        "description" : "Description 1"
          |      },
          |      "details" : {
          |        "documentReferenceNumber" : "Ref no. 1",
          |        "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
          |      }
          |    },
          |    {
          |      "attachToAllItems" : true,
          |      "type" : {
          |        "type" : "Type 2",
          |        "code" : "Code 2"
          |      },
          |      "details" : {
          |        "documentReferenceNumber" : "Ref no. 2",
          |        "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
          |      }
          |    }
          |  ],
          |  "items" : [
          |    {
          |      "documents" : [
          |        {
          |          "document" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
          |        },
          |        {
          |          "document" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
          |        },
          |        {
          |          "document" : "3882459f-b7bc-478d-9d24-359533aa8fe3"
          |        }
          |      ]
          |    }
          |  ]
          |}
          |""".stripMargin)
        .as[JsObject]

      val userAnswers = emptyUserAnswers.copy(data = json)

      "must return some document" - {
        "when UUID found" in {
          val result1 = service.getDocument(userAnswers, itemIndex, Index(0))
          val result2 = service.getDocument(userAnswers, itemIndex, Index(1))

          result1.value mustBe Document(attachToAllItems = true,
                                        "Type 1",
                                        "Code 1",
                                        Some("Description 1"),
                                        "Ref no. 1",
                                        UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
          )
          result2.value mustBe Document(attachToAllItems = true, "Type 2", "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
        }
      }

      "must return None" - {
        "when UUID not found" in {
          val result = service.getDocument(userAnswers, itemIndex, Index(2))

          result mustBe None
        }
      }
    }
  }

}
