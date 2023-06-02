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
import models.{SelectableList, TransportEquipment}
import play.api.libs.json.{JsObject, Json}

class TransportEquipmentsServiceSpec extends SpecBase {

  private val service = injector.instanceOf[TransportEquipmentService]

  "TransportEquipments Service" - {

    "getTransportEquipments" - {

      "must return the transport equipments" in {
        val json = Json
          .parse("""
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
              |          "itemNumbers" : [
              |            {
              |              "itemNumber" : "1234"
              |            }
              |          ]
              |         },
              |         {
              |           "addContainerIdentificationNumberYesNo" : false,
              |           "addSealsYesNo" : false,
              |           "itemNumbers" : [
              |             {
              |               "itemNumber" : "1944"
              |             }
              |           ]
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
            TransportEquipment(1, Some("98777")),
            TransportEquipment(2, None)
          )
        )
      }
    }
  }
}
