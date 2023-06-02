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

class TransportEquipmentSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "must deserialise from mongo" in {

    val json = Json
      .parse("""
          |{
          |  "containerIdentificationNumber" : "98777",
          |  "addSealsYesNo" : true,
          |  "seals" : [
          |    {
          |      "identificationNumber" : "TransportSeal1"
          |    }
          |   ],
          |   "itemNumbers" : [
          |     {
          |       "itemNumber" : "1234"
          |     }
          |   ]
          |}
          |""".stripMargin)

    val expectedResult = TransportEquipment(
      number = 1,
      containerId = Some("98777")
    )

    val result = json.as[TransportEquipment](TransportEquipment.equipmentReads(1))

    result mustBe expectedResult
  }

  "must format as string" - {
    "when containerId defined" in {
      forAll(positiveIntsMinMax(1: Int, 9999: Int), nonEmptyString) {
        (number, containerId) =>
          val numberString = String.format("%,d", number)
          val equipment = TransportEquipment(
            number = number,
            containerId = Some(containerId)
          )

          equipment.asString mustBe s"($numberString) Transport equipment - $containerId"
      }
    }

    "when containerId undefined" in {
      forAll(positiveIntsMinMax(1: Int, 9999: Int)) {
        number =>
          val numberString = String.format("%,d", number)
          val equipment = TransportEquipment(
            number = number,
            containerId = None
          )

          equipment.asString mustBe s"($numberString) Transport equipment"
      }
    }
  }

  "must convert to select item" in {
    forAll(positiveIntsMinMax(1: Int, 9999: Int), Gen.option(nonEmptyString), arbitrary[Boolean]) {
      (number, containerId, selected) =>
        val equipment = TransportEquipment(number, containerId)
        equipment.toSelectItem(selected) mustBe SelectItem(Some(equipment.value), equipment.asString, selected)
    }
  }
}
