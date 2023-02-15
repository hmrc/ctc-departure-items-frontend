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
import models.reference.CustomsOffice
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class CustomsOfficeListSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "getAll" - {
    "return the full list of customs offices" in {
      forAll(nonEmptyListOf[CustomsOffice](10)) {
        customsOffices =>
          val customsOfficeList = CustomsOfficeList(customsOffices.toList)

          customsOfficeList.getAll must contain theSameElementsAs customsOffices.toList
      }
    }
  }

  "getById" - {
    "return a customs office if it exists" in {
      forAll(nonEmptyListOf[CustomsOffice](10)) {
        customsOffices =>
          val customsOfficeList = CustomsOfficeList(customsOffices.toList)

          val officeId = customsOffices.head.id

          customsOfficeList.getCustomsOffice(officeId).value mustEqual customsOffices.head
      }
    }

    "return a None if it does not exists" in {

      val customsOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("FR3", "three", None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeId: String = "GB4"

      customsOfficeList.getCustomsOffice(officeId) mustEqual None

    }
  }

  "filter" - {
    "return a list of customs offices without the office with matching id" in {
      val customsOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("GB3", "three", None),
        CustomsOffice("GB4", "four", Some("phoneNumber")),
        CustomsOffice("GB5", "five", None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("GB3")

      val expectedOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("GB4", "four", Some("phoneNumber")),
        CustomsOffice("GB5", "five", None)
      )

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return a list of customs offices without the offices with matching ids" in {
      val customsOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("GB3", "three", None),
        CustomsOffice("GB4", "four", Some("phoneNumber")),
        CustomsOffice("GB5", "five", None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("GB5", "GB3")

      val expectedOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("GB4", "four", Some("phoneNumber"))
      )

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs expectedOffices

    }

    "return the full list of customs offices when there are no offices with matching id" in {
      val customsOffices = Seq(
        CustomsOffice("GB1", "one", Some("phoneNumber")),
        CustomsOffice("GB2", "two", None),
        CustomsOffice("GB3", "three", None),
        CustomsOffice("GB4", "four", Some("phoneNumber")),
        CustomsOffice("GB5", "five", None)
      )

      val customsOfficeList = CustomsOfficeList(customsOffices)

      val officeIds = Seq("13")

      customsOfficeList.filterNot(officeIds) must contain theSameElementsAs customsOffices

    }

  }

  "officesOfExitReads" - {
    "must read offices of exit as CustomsOfficeList" in {
      val json = Json.parse("""
          |[
          |  {
          |    "officeOfExit": {
          |      "id": "GB1",
          |      "name": "Newcastle"
          |    }
          |  },
          |  {
          |    "officeOfExit": {
          |      "id": "GB2",
          |      "name": "London",
          |      "phoneNumber": "999"
          |    }
          |  }
          |]
          |""".stripMargin)

      val result = json.as[CustomsOfficeList](CustomsOfficeList.officesOfExitReads)

      result mustBe CustomsOfficeList(
        Seq(
          CustomsOffice("GB1", "Newcastle", None),
          CustomsOffice("GB2", "London", Some("999"))
        )
      )
    }
  }

  "officesOfTransitReads" - {
    "must read offices of transit as CustomsOfficeList" in {
      val json = Json.parse("""
          |[
          |  {
          |    "officeOfTransit": {
          |      "id": "GB1",
          |      "name": "Newcastle"
          |    }
          |  },
          |  {
          |    "officeOfTransit": {
          |      "id": "GB2",
          |      "name": "London",
          |      "phoneNumber": "999"
          |    }
          |  }
          |]
          |""".stripMargin)

      val result = json.as[CustomsOfficeList](CustomsOfficeList.officesOfTransitReads)

      result mustBe CustomsOfficeList(
        Seq(
          CustomsOffice("GB1", "Newcastle", None),
          CustomsOffice("GB2", "London", Some("999"))
        )
      )
    }
  }

}
