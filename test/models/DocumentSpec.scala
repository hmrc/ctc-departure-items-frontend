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
import models.Document.{PreviousDocument, SupportDocument, TransportDocument}
import play.api.libs.json.Json

class DocumentSpec extends SpecBase {

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

      val expectedResult = TransportDocument(
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document]

      result mustBe expectedResult
    }

    "when support document" in {

      val code           = "Y028"
      val description    = "Carrier (AEO certificate number)"
      val lineItemNumber = 1

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
          |  	 "lineItemNumber" : $lineItemNumber
          |  }
          |}
          |""".stripMargin)

      val expectedResult = SupportDocument(
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber,
        lineItemNumber = Some(lineItemNumber)
      )

      val result = json.as[Document]

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

      val expectedResult = PreviousDocument(
        code = code,
        description = Some(description),
        referenceNumber = referenceNumber
      )

      val result = json.as[Document]

      result mustBe expectedResult
    }
  }
}
