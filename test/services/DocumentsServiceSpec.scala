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

package services

import base.SpecBase
import generators.Generators
import models.DocumentType.Previous
import models.{DeclarationTypeItemLevel, Document, Index, ItemLevelDocuments, SelectableList}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external.*
import pages.item.DeclarationTypePage
import pages.item.documents.index.DocumentPage
import pages.sections.external
import play.api.libs.json.{JsArray, JsObject, Json}

import java.util.UUID

class DocumentsServiceSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val service = injector.instanceOf[DocumentsService]

  "Documents Service" - {

    "getDocuments" - {

      "when document index undefined" - {

        "must return documents and filter out any documents already selected" - {
          "when documents present in user answers and documents selected" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : {
                  |    "addDocumentsYesNo" : true,
                  |    "documents" : [
                  |      {
                  |        "attachToAllItems" : false,
                  |        "previousDocumentType" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 1",
                  |          "description" : "Description 1"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 1",
                  |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |        }
                  |      },
                  |      {
                  |        "attachToAllItems" : true,
                  |        "type" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 3",
                  |          "description" : "Description 3"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 3",
                  |          "uuid" : "cc09f64b-e519-4b21-9961-243ba7cad1b7"
                  |        }
                  |      },
                  |      {
                  |        "attachToAllItems" : false,
                  |        "type" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 2"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 2",
                  |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                  |        }
                  |      }
                  |    ]
                  |  },
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

            result mustBe SelectableList(
              Seq(
                Document(attachToAllItems = false, Previous, "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
              )
            )
          }

          "when documents present in user answers and no documents selected" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : {
                  |    "addDocumentsYesNo" : true,
                  |    "documents" : [
                  |      {
                  |        "attachToAllItems" : false,
                  |        "previousDocumentType" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 1",
                  |          "description" : "Description 1"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 1",
                  |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |        }
                  |      },
                  |      {
                  |        "attachToAllItems" : false,
                  |        "type" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 2"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 2",
                  |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                  |        }
                  |      }
                  |    ]
                  |  }
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, None)

            result mustBe SelectableList(
              Seq(
                Document(
                  attachToAllItems = false,
                  Previous,
                  "Code 1",
                  Some("Description 1"),
                  "Ref no. 1",
                  UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
                ),
                Document(attachToAllItems = false, Previous, "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
              )
            )
          }
        }

        "must return empty selectable list" - {
          "when empty list of documents" in {
            val result = service.getDocuments(emptyUserAnswers, itemIndex, None)

            result mustBe SelectableList(Nil)
          }

          "when data is in an invalid shape" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : {
                  |    "addDocumentsYesNo" : true,
                  |    "documents" : [
                  |      {
                  |        "foo" : "bar"
                  |      }
                  |    ]
                  |    }
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, None)

            result mustBe SelectableList(Nil)
          }
        }
      }

      "when document index defined" - {

        "must return documents and filter out any documents already selected but not the one at current index" - {
          "when documents present in user answers and documents selected" - {
            "and at an already answered index" in {
              val json = Json
                .parse("""
                    |{
                    |  "documents" : {
                    |    "addDocumentsYesNo" : true,
                    |    "documents" : [
                    |      {
                    |        "attachToAllItems" : false,
                    |        "previousDocumentType" : {
                    |          "type" : "Previous",
                    |          "code" : "Code 1",
                    |          "description" : "Description 1"
                    |        },
                    |        "details" : {
                    |          "documentReferenceNumber" : "Ref no. 1",
                    |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                    |        }
                    |      },
                    |      {
                    |        "attachToAllItems" : false,
                    |        "type" : {
                    |          "type" : "Previous",
                    |          "code" : "Code 2"
                    |        },
                    |        "details" : {
                    |          "documentReferenceNumber" : "Ref no. 2",
                    |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                    |        }
                    |      }
                    |    ]
                    |  },
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

              result mustBe SelectableList(
                Seq(
                  Document(
                    attachToAllItems = false,
                    Previous,
                    "Code 1",
                    Some("Description 1"),
                    "Ref no. 1",
                    UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
                  ),
                  Document(attachToAllItems = false, Previous, "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
                )
              )
            }

            "and not at an already answered index" in {
              val json = Json
                .parse("""
                    |{
                    |  "documents" : {
                    |    "addDocumentsYesNo" : true,
                    |    "documents" : [
                    |      {
                    |        "attachToAllItems" : false,
                    |        "previousDocumentType" : {
                    |          "type" : "Previous",
                    |          "code" : "Code 1",
                    |          "description" : "Description 1"
                    |        },
                    |        "details" : {
                    |          "documentReferenceNumber" : "Ref no. 1",
                    |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                    |        }
                    |      },
                    |      {
                    |        "attachToAllItems" : false,
                    |        "type" : {
                    |          "type" : "Previous",
                    |          "code" : "Code 2"
                    |        },
                    |        "details" : {
                    |          "documentReferenceNumber" : "Ref no. 2",
                    |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                    |        }
                    |      }
                    |    ]
                    |  },
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

              result mustBe SelectableList(
                Seq(
                  Document(attachToAllItems = false, Previous, "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
                )
              )
            }
          }

          "when documents present in user answers and no documents selected" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : {
                  |    "addDocumentsYesNo" : true,
                  |    "documents" : [
                  |      {
                  |        "attachToAllItems" : false,
                  |        "previousDocumentType" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 1",
                  |          "description" : "Description 1"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 1",
                  |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                  |        }
                  |      },
                  |      {
                  |        "attachToAllItems" : false,
                  |        "type" : {
                  |          "type" : "Previous",
                  |          "code" : "Code 2"
                  |        },
                  |        "details" : {
                  |          "documentReferenceNumber" : "Ref no. 2",
                  |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                  |        }
                  |      }
                  |    ]
                  |  }
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, Some(documentIndex))

            result mustBe SelectableList(
              Seq(
                Document(
                  attachToAllItems = false,
                  Previous,
                  "Code 1",
                  Some("Description 1"),
                  "Ref no. 1",
                  UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
                ),
                Document(attachToAllItems = false, Previous, "Code 2", None, "Ref no. 2", UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a"))
              )
            )
          }
        }

        "must return empty selectable list" - {
          "when empty list of documents" in {
            val result = service.getDocuments(emptyUserAnswers, itemIndex, Some(documentIndex))

            result mustBe SelectableList(Nil)
          }

          "when data is in an invalid shape" in {
            val json = Json
              .parse("""
                  |{
                  |  "documents" : {
                  |    "documents" : [
                  |      {
                  |        "foo" : "bar"
                  |      }
                  |    ]
                  |  }
                  |}
                  |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers.copy(data = json)

            val result = service.getDocuments(userAnswers, itemIndex, Some(documentIndex))

            result mustBe SelectableList(Nil)
          }
        }
      }
    }

    "getConsignmentLevelDocuments" - {

      "must return all consignment level documents" - {
        "when documents present in user answers" in {
          val json = Json
            .parse("""
                |{
                |  "documents" : {
                |    "addDocumentsYesNo" : true,
                |    "documents" : [
                |      {
                |        "attachToAllItems" : false,
                |        "previousDocumentType" : {
                |          "type" : "Previous",
                |          "code" : "Code 1",
                |          "description" : "Description 1"
                |        },
                |        "details" : {
                |          "documentReferenceNumber" : "Ref no. 1",
                |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
                |        }
                |      },
                |      {
                |        "attachToAllItems" : true,
                |        "type" : {
                |          "type" : "Previous",
                |          "code" : "Code 3"
                |        },
                |        "details" : {
                |          "documentReferenceNumber" : "Ref no. 3",
                |          "uuid" : "cc09f64b-e519-4b21-9961-243ba7cad1b7"
                |        }
                |      },
                |      {
                |        "attachToAllItems" : false,
                |        "type" : {
                |          "type" : "Previous",
                |          "code" : "Code 2"
                |        },
                |        "details" : {
                |          "documentReferenceNumber" : "Ref no. 2",
                |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
                |        }
                |      }
                |    ]
                |  }
                |}
                |""".stripMargin)
            .as[JsObject]

          val userAnswers = emptyUserAnswers.copy(data = json)

          val result = service.getConsignmentLevelDocuments(userAnswers)

          result mustBe Seq(
            Document(attachToAllItems = true, Previous, "Code 3", None, "Ref no. 3", UUID.fromString("cc09f64b-e519-4b21-9961-243ba7cad1b7"))
          )

        }
      }
    }

    "getDocument" - {

      val json = Json
        .parse("""
          |{
          |  "documents" : {
          |    "addDocumentsYesNo" : true,
          |    "documents" : [
          |      {
          |        "attachToAllItems" : true,
          |        "previousDocumentType" : {
          |          "type" : "Previous",
          |          "code" : "Code 1",
          |          "description" : "Description 1"
          |        },
          |        "details" : {
          |          "documentReferenceNumber" : "Ref no. 1",
          |          "uuid" : "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
          |        }
          |      },
          |      {
          |        "attachToAllItems" : true,
          |        "type" : {
          |          "type" : "Previous",
          |          "code" : "Code 2"
          |        },
          |        "details" : {
          |          "documentReferenceNumber" : "Ref no. 2",
          |          "uuid" : "a573bfd3-6470-40c4-a290-ea2d8d43c02a"
          |        }
          |      }
          |    ]
          |  },
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

          result1.value mustBe Document(
            attachToAllItems = true,
            `type` = Previous,
            code = "Code 1",
            description = Some("Description 1"),
            referenceNumber = "Ref no. 1",
            uuid = UUID.fromString("1794d93b-17d5-44fe-a18d-aaa2059d06fe")
          )
          result2.value mustBe Document(
            attachToAllItems = true,
            `type` = Previous,
            code = "Code 2",
            description = None,
            referenceNumber = "Ref no. 2",
            uuid = UUID.fromString("a573bfd3-6470-40c4-a290-ea2d8d43c02a")
          )
        }
      }

      "must return None" - {
        "when UUID not found" in {
          val result = service.getDocument(userAnswers, itemIndex, Index(2))

          result mustBe None
        }
      }
    }

    "getItemLevelDocuments" - {

      "must return count of each document type" - {

        "when document index defined" - {

          "when previous document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Previous",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), Some(Index(0)))

            result mustBe ItemLevelDocuments(0, 0, 0)
          }

          "when supporting document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Support",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), Some(Index(0)))

            result mustBe ItemLevelDocuments(0, 0, 0)
          }

          "when transport document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Transport",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), Some(Index(0)))

            result mustBe ItemLevelDocuments(0, 0, 0)
          }
        }

        "when document index undefined" - {

          "when previous document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Previous",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), None)

            result mustBe ItemLevelDocuments(1, 0, 0)
          }

          "when supporting document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Support",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), None)

            result mustBe ItemLevelDocuments(0, 1, 0)
          }

          "when transport document" in {

            val uuid = "cc09f64b-e519-4b21-9961-243ba7cad1b7"

            val json = Json
              .parse(s"""
                   |{
                   |  "documents" : {
                   |    "addDocumentsYesNo" : true,
                   |    "documents" : [
                   |      {
                   |        "attachToAllItems" : false,
                   |        "type" : {
                   |          "type" : "Transport",
                   |          "code" : "Code"
                   |        },
                   |        "details" : {
                   |          "documentReferenceNumber" : "Ref no.",
                   |          "uuid" : "$uuid"
                   |        }
                   |      }
                   |    ]
                   |  }
                   |}
                   |""".stripMargin)
              .as[JsObject]

            val userAnswers = emptyUserAnswers
              .copy(data = json)
              .setValue(DocumentPage(Index(0), Index(0)), UUID.fromString(uuid))

            val result = service.getItemLevelDocuments(userAnswers, Index(0), None)

            result mustBe ItemLevelDocuments(0, 0, 1)
          }
        }
      }
    }

    "isConsignmentPreviousDocumentRequired" - {

      "item declaration type is T2 or T2F, office of departure in GB, no consignment level previous documents and no item level previous documents" - {
        "return true" in {
          forAll(arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)) {
            itemDeclarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, true)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual true
          }
        }
      }

      "item declaration type is not T2 or T2F" - {
        "must return false" in {
          forAll(arbitrary[DeclarationTypeItemLevel](arbitraryT1DeclarationType)) {
            itemDeclarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, true)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual false
          }
        }
      }

      "item declaration type is T2 or T2F, office of departure not in GB" - {
        "must return false" in {
          forAll(arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)) {
            itemDeclarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, false)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual false
          }
        }
      }

      "item declaration type is T2 or T2F, office of departure in GB, and a consignment level previous document" - {
        "must return false" in {
          forAll(arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)) {
            itemDeclarationType =>
              val documentsJson = Json
                .parse("""
                  |[
                  |  {
                  |    "attachToAllItems" : true,
                  |    "type" : {
                  |      "type" : "Previous",
                  |      "code" : "Code",
                  |      "description" : "Description"
                  |    },
                  |    "details" : {
                  |      "documentReferenceNumber" : "Ref no.",
                  |      "uuid" : "91631d90-e890-440a-9c0c-a7a88815ac4e"
                  |    }
                  |  }
                  |]
                  |""".stripMargin)
                .as[JsArray]

              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                .setValue(external.DocumentsSection, documentsJson)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual false
          }
        }
      }

      "item declaration type is T2 or T2F, office of departure in GB, no consignment level previous documents and an attached item level previous document" - {
        "must return false" in {
          forAll(arbitrary[UUID], arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)) {
            (uuid, itemDeclarationType) =>
              val documentsJson = Json
                .parse(s"""
                    |[
                    |  {
                    |    "attachToAllItems" : false,
                    |    "type" : {
                    |      "type" : "Previous",
                    |      "code" : "Code",
                    |      "description" : "Description"
                    |    },
                    |    "details" : {
                    |      "documentReferenceNumber" : "Ref no.",
                    |      "uuid" : "${uuid.toString}"
                    |    }
                    |  }
                    |]
                    |""".stripMargin)
                .as[JsArray]

              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                .setValue(external.DocumentsSection, documentsJson)
                .setValue(DocumentPage(Index(0), Index(0)), uuid)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual false
          }
        }
      }

      "item declaration type is T2 or T2F, office of departure in GB, no consignment level previous documents and no attached item level previous document" - {
        "must return false" in {
          forAll(arbitrary[UUID], arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)) {
            (uuid, itemDeclarationType) =>
              val documentsJson = Json
                .parse(s"""
                    |[
                    |  {
                    |    "attachToAllItems" : false,
                    |    "type" : {
                    |      "type" : "Previous",
                    |      "code" : "Code",
                    |      "description" : "Description"
                    |    },
                    |    "details" : {
                    |      "documentReferenceNumber" : "Ref no.",
                    |      "uuid" : "${uuid.toString}"
                    |    }
                    |  }
                    |]
                    |""".stripMargin)
                .as[JsArray]

              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(Index(0)), itemDeclarationType)
                .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                .setValue(external.DocumentsSection, documentsJson)

              val result = service.isConsignmentPreviousDocumentRequired(userAnswers, Index(0))

              result mustEqual false
          }
        }
      }
    }
  }
}
