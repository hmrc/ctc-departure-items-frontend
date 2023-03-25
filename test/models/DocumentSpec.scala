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

class DocumentSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val typeGen = Gen.oneOf("Transport", "Support", "Previous")

  private val i = arbitrary[Int].sample.value

  "must deserialise from mongo" - {

    val referenceNumber = "85968459869045"

    "when transport document" in {

      val code        = "740"
      val description = "Air waybill"

      val json = Json.parse(s"""
          |{
          |  "type" : {
          |  	 "type" : "Transport",
          |  	 "code" : "$code",
          |  	 "description" : "$description"
          |  },
          |  "details" : {
          |  	 "documentReferenceNumber" : "$referenceNumber"
          |  }
          |}
          |""".stripMargin)

      val expectedResult = Document(
        index = i,
        `type` = "Transport",
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document](Document.reads(i))

      result mustBe expectedResult
    }

    "when support document" in {

      val code        = "Y028"
      val description = "Carrier (AEO certificate number)"

      val json = Json.parse(s"""
          |{
          |  "type" : {
          |  	 "type" : "Support",
          |  	 "code" : "$code",
          |  	 "description" : "$description"
          |  },
          |  "details" : {
          |  	 "documentReferenceNumber" : "$referenceNumber",
          |  	 "addLineItemNumberYesNo" : true,
          |  	 "lineItemNumber" : 1
          |  }
          |}
          |""".stripMargin)

      val expectedResult = Document(
        index = i,
        `type` = "Support",
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document](Document.reads(i))

      result mustBe expectedResult
    }

    "when previous document" in {

      val code        = "T2"
      val description = "T2"

      val json = Json.parse(s"""
          |{
          |  "type" : {
          |  	 "type" : "Previous",
          |  	 "code" : "$code",
          |  	 "description" : "$description"
          |  },
          |  "details" : {
          |  	 "documentReferenceNumber" : "$referenceNumber",
          |  	 "addGoodsItemNumberYesNo" : true,
          |  	 "goodsItemNumber" : 1,
          |  	 "addTypeOfPackageYesNo" : true,
          |  	 "packageType" : {
          |  	 	 "code" : "AE",
          |  	 	 "description" : "Aerosol"
          |  	 },
          |  	 "addNumberOfPackagesYesNo" : true,
          |  	 "numberOfPackages" : 69,
          |  	 "declareQuantityOfGoodsYesNo" : true,
          |  	 "metric" : {
          |  	 	 "code" : "LTR",
          |  	 	 "description" : "Litre"
          |  	 },
          |  	 "quantity" : 1
          |  }
          |}
          |""".stripMargin)

      val expectedResult = Document(
        index = i,
        `type` = "Previous",
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document](Document.reads(i))

      result mustBe expectedResult
    }

    "when mandatory previous document" in {

      val code        = "T1"
      val description = "Document T1"

      val json = Json.parse(s"""
          |{
          |  "previousDocumentType" : {
          |  	 "type" : "Previous",
          |  	 "code" : "$code",
          |  	 "description" : "$description"
          |  },
          |  "details" : {
          |  	 "documentReferenceNumber" : "$referenceNumber",
          |  	 "addGoodsItemNumberYesNo" : false,
          |  	 "addTypeOfPackageYesNo" : false,
          |  	 "declareQuantityOfGoodsYesNo" : false
          |  }
          |}
          |""".stripMargin)

      val expectedResult = Document(
        index = i,
        `type` = "Previous",
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document](Document.reads(i))

      result mustBe expectedResult
    }
  }

  "must format as string" - {
    "when description defined" in {
      forAll(typeGen, nonEmptyString, nonEmptyString, nonEmptyString) {
        (`type`, code, description, referenceNumber) =>
          val document = Document(
            index = i,
            `type` = `type`,
            code = code,
            description = Some(description),
            referenceNumber = referenceNumber
          )

          document.toString mustBe s"($code) $description - $referenceNumber"
      }
    }

    "when description undefined" in {
      forAll(typeGen, nonEmptyString, nonEmptyString) {
        (`type`, code, referenceNumber) =>
          val document = Document(
            index = i,
            `type` = `type`,
            code = code,
            description = None,
            referenceNumber = referenceNumber
          )

          document.toString mustBe s"$code - $referenceNumber"
      }
    }
  }

  "must convert to select item" in {
    forAll(arbitrary[Int], typeGen, nonEmptyString, Gen.option(nonEmptyString), nonEmptyString, arbitrary[Boolean]) {
      (index, `type`, code, description, referenceNumber, selected) =>
        val document = Document(index, `type`, code, description, referenceNumber)
        document.toSelectItem(selected) mustBe SelectItem(Some(index.toString), document.toString, selected)
    }
  }
}
