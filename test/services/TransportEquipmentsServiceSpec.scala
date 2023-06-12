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
import models.{Index, SelectableList, TransportEquipment}
import play.api.libs.json.{JsObject, Json}

import java.util.UUID

class TransportEquipmentsServiceSpec extends SpecBase {

  private val service = injector.instanceOf[TransportEquipmentService]

  private val uuid1 = "1794d93b-17d5-44fe-a18d-aaa2059d06fe"
  private val uuid2 = "8a081ef8-5e49-42c8-b4fc-9140018afce9"

  "TransportEquipments Service" - {

    "getTransportEquipments" - {

      "must return the transport equipments" in {
        val json = Json
          .parse(s"""
              |{
              |  "transportDetails" : {
              |    "equipmentsAndCharges" : {
              |      "equipments" : [
              |        {
              |          "containerIdentificationNumber" : "98777",
              |          "addSealsYesNo" : true,
              |          "seals" : [
              |            {
              |              "identificationNumber" : "TransportSeal1"
              |            }
              |          ],
              |          "uuid": "$uuid1"
              |         },
              |         {
              |           "addContainerIdentificationNumberYesNo" : false,
              |           "addSealsYesNo" : false,
              |           "uuid": "$uuid2"
              |          }
              |        ]
              |      }
              |    }
              |}
              |""".stripMargin)
          .as[JsObject]

        val userAnswers = emptyUserAnswers.copy(data = json)

        val result = service.getTransportEquipments(userAnswers)

        result mustBe SelectableList(
          Seq(
            TransportEquipment(1, Some("98777"), UUID.fromString(uuid1)),
            TransportEquipment(2, None, UUID.fromString(uuid2))
          )
        )
      }
    }

    "getTransportEquipment" - {

      "when transport equipments present" - {
        val json = Json
          .parse(s"""
              |{
              |  "transportDetails" : {
              |    "equipmentsAndCharges" : {
              |      "equipments" : [
              |        {
              |          "containerIdentificationNumber" : "98777",
              |          "addSealsYesNo" : true,
              |          "seals" : [
              |            {
              |              "identificationNumber" : "TransportSeal1"
              |            }
              |          ],
              |          "uuid": "$uuid1"
              |         },
              |         {
              |           "addContainerIdentificationNumberYesNo" : false,
              |           "addSealsYesNo" : false,
              |           "uuid": "$uuid2"
              |          }
              |        ]
              |      }
              |    },
              |  "items": [
              |    {
              |      "transportEquipment" : "$uuid1"
              |    },
              |    {
              |      "transportEquipment" : "$uuid2"
              |    }
              |  ]
              |}
              |""".stripMargin)
          .as[JsObject]

        val userAnswers = emptyUserAnswers.copy(data = json)

        "must return some equipment" - {
          "when index found" in {
            val result1 = service.getTransportEquipment(userAnswers, Index(0))
            val result2 = service.getTransportEquipment(userAnswers, Index(1))

            result1.value mustBe TransportEquipment(
              number = 1,
              containerId = Some("98777"),
              uuid = UUID.fromString(uuid1)
            )
            result2.value mustBe TransportEquipment(
              number = 2,
              containerId = None,
              uuid = UUID.fromString(uuid2)
            )
          }
        }

        "must return None" - {
          "when Index not found" in {
            val result = service.getTransportEquipment(userAnswers, Index(2))

            result mustBe None
          }
        }
      }

      "when no transport equipment present" in {
        val json = Json
          .parse("""
              |{
              |  "transportDetails" : {
              |    "equipmentsAndCharges" : {
              |      "equipments" : []
              |      }
              |    }
              |}
              |""".stripMargin)
          .as[JsObject]

        val userAnswers = emptyUserAnswers.copy(data = json)

        val result = service.getTransportEquipment(userAnswers, Index(0))

        result mustBe None
      }
    }
  }
}
