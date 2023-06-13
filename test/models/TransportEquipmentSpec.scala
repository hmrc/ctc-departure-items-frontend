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
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

import java.util.UUID

class TransportEquipmentSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  private val uuid = "8a081ef8-5e49-42c8-b4fc-9140018afce9"

  "must deserialise from mongo" in {

    val json = Json
      .parse(s"""
          |{
          |  "containerIdentificationNumber" : "98777",
          |  "addSealsYesNo" : true,
          |  "seals" : [
          |    {
          |      "identificationNumber" : "TransportSeal1"
          |    }
          |   ],
          |  "uuid": "$uuid"
          |}
          |""".stripMargin)

    val expectedResult = TransportEquipment(
      number = 1,
      containerId = Some("98777"),
      uuid = UUID.fromString(uuid)
    )

    val result = json.as[TransportEquipment](TransportEquipment.equipmentReads(0))

    result mustBe expectedResult
  }

  "must format as string" - {
    "when containerId defined" in {
      forAll(positiveIntsMinMax(0: Int, 9998: Int), nonEmptyString) {
        (number, containerId) =>
          val numberString = String.format("%,d", number)
          val equipment = TransportEquipment(
            number = number,
            containerId = Some(containerId),
            uuid = UUID.fromString(uuid)
          )

          equipment.asString mustBe s"($numberString) Transport equipment - $containerId"
          equipment.toString mustBe s"$number - $containerId"
      }
    }

    "when containerId undefined" in {
      forAll(positiveIntsMinMax(1: Int, 9999: Int)) {
        number =>
          val numberString = String.format("%,d", number)
          val equipment = TransportEquipment(
            number = number,
            containerId = None,
            uuid = UUID.fromString(uuid)
          )

          equipment.asString mustBe s"($numberString) Transport equipment"
          equipment.toString mustBe s"$number"
      }
    }
  }

  "must convert to select item" in {
    forAll(positiveIntsMinMax(1: Int, 9999: Int), Gen.option(nonEmptyString), arbitrary[Boolean], arbitrary[UUID]) {
      (number, containerId, selected, uuid) =>
        val equipment = TransportEquipment(number, containerId, uuid)
        equipment.toSelectItem(selected) mustBe SelectItem(Some(equipment.value), equipment.asString, selected)
    }
  }
}
