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

package models.journeyDomain.item

import base.SpecBase
import config.Constants.DeclarationType.*
import config.Constants.SecurityType.*
import generators.Generators
import models.DeclarationTypeItemLevel.*
import models.journeyDomain.item.additionalInformation.{AdditionalInformationDomain, AdditionalInformationListDomain}
import models.journeyDomain.item.additionalReferences.{AdditionalReferenceDomain, AdditionalReferencesDomain}
import models.journeyDomain.item.dangerousGoods.{DangerousGoodsDomain, DangerousGoodsListDomain}
import models.journeyDomain.item.documents.{DocumentDomain, DocumentsDomain}
import models.journeyDomain.item.packages.{PackageDomain, PackagesDomain}
import models.reference.*
import models.{DeclarationTypeItemLevel, Index, SubmissionState}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external.*
import pages.item.*
import pages.item.additionalInformation.index.*
import pages.item.additionalReference.index.*
import pages.item.dangerousGoods.index.UNNumberPage
import pages.item.documents.index.DocumentPage
import pages.item.packages.index.*
import pages.sections.additionalInformation.AdditionalInformationListSection
import pages.sections.additionalReference.AdditionalReferencesSection
import pages.sections.dangerousGoods.DangerousGoodsListSection
import pages.sections.documents.DocumentsSection
import pages.sections.external
import pages.sections.external.TransportEquipmentsSection
import pages.sections.packages.PackagesSection
import play.api.libs.json.{JsArray, Json}

import java.util.UUID

class ItemDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Item Domain" - {

    "userAnswersReader" - {
      "can not be read from user answers" - {
        "when item description page is unanswered" in {
          val result = ItemDomain.userAnswersReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe DescriptionPage(itemIndex)
          result.left.value.pages mustBe Seq(
            DescriptionPage(itemIndex)
          )
        }
      }
    }

    "transportEquipmentReader" - {
      def equipments(uuid: UUID) = Json
        .parse(s"""
                  |[
                  |  {
                  |    "containerIdentificationNumber" : "98777",
                  |    "addSealsYesNo" : true,
                  |    "seals" : [
                  |      {
                  |        "identificationNumber" : "TransportSeal1"
                  |      }
                  |    ],
                  |    "uuid": "$uuid"
                  |  }
                  |]
                  |""".stripMargin)
        .as[JsArray]

      "can be read from user answers" - {
        "when transport equipment sequence is present" in {
          forAll(arbitrary[UUID]) {
            uuid =>
              val userAnswers = emptyUserAnswers
                .setValue(TransportEquipmentsSection, equipments(uuid))
                .setValue(TransportEquipmentPage(itemIndex), uuid)

              val expectedResult = Some(uuid)

              val result = ItemDomain.transportEquipmentReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                TransportEquipmentPage(itemIndex)
              )
          }
        }

        "when transport equipment sequence is not present" in {
          val expectedResult = None

          val result = ItemDomain.transportEquipmentReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Nil
        }
      }

      "can not be read from user answers" - {
        "when transport equipment sequence is present" - {
          "and transport equipment is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransportEquipmentsSection, equipments(UUID.randomUUID()))

            val result = ItemDomain.transportEquipmentReader(itemIndex).apply(Nil).run(userAnswers)

            result.left.value.page mustBe TransportEquipmentPage(itemIndex)
            result.left.value.pages mustBe Seq(
              TransportEquipmentPage(itemIndex)
            )
          }
        }
      }
    }

    "declarationTypeReader" - {
      "can be read from user answers" - {
        "when declaration type is not T" in {
          forAll(arbitrary[String](arbitraryNonTDeclarationType)) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, declarationType)

              val expectedResult = None

              val result = ItemDomain.declarationTypeReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
          }
        }

        "when declaration type is T" in {
          forAll(arbitrary[DeclarationTypeItemLevel]) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, T)
                .setValue(DeclarationTypePage(itemIndex), declarationType)

              val expectedResult = Some(declarationType)

              val result = ItemDomain.declarationTypeReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                DeclarationTypePage(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {
        "when transit operation declaration type is T" - {
          "and declaration type is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransitOperationDeclarationTypePage, T)

            val result = ItemDomain.declarationTypeReader(itemIndex).apply(Nil).run(userAnswers)

            result.left.value.page mustBe DeclarationTypePage(itemIndex)
            result.left.value.pages mustBe Seq(
              DeclarationTypePage(itemIndex)
            )
          }
        }
      }

      "and consignment country of dispatch is defined" in {
        forAll(arbitrary[String](arbitraryConsignmentDeclarationType)) {
          declarationType =>
            forAll(arbitrary[Country]) {
              country =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationDeclarationTypePage, declarationType)
                  .setValue(ConsignmentCountryOfDispatchPage, country)

                val expectedResult = None

                val result = ItemDomain.countryOfDispatchReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Nil
            }
        }
      }

      "and consignment country of dispatch is undefined" in {
        forAll(arbitrary[String](arbitraryConsignmentDeclarationType)) {
          declarationType =>
            forAll(arbitrary[Country]) {
              country =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationDeclarationTypePage, declarationType)
                  .setValue(CountryOfDispatchPage(itemIndex), country)

                val expectedResult = Some(country)

                val result = ItemDomain.countryOfDispatchReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  CountryOfDispatchPage(itemIndex)
                )
            }
        }
      }
    }

    "countryOfDispatchReader" - {
      "can be read from user answers" - {
        "and consignment country of dispatch is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, TIR)
                .setValue(ConsignmentCountryOfDispatchPage, country)

              val expectedResult = None

              val result = ItemDomain.countryOfDispatchReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Nil
          }
        }

        "and consignment country of dispatch is undefined" in {
          forAll(arbitrary[Country]) {
            country =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, TIR)
                .setValue(CountryOfDispatchPage(itemIndex), country)

              val expectedResult = Some(country)

              val result = ItemDomain.countryOfDispatchReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                CountryOfDispatchPage(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {
        "when transit operation declaration type is TIR" - {
          "and consignment country of dispatch is undefined" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransitOperationDeclarationTypePage, TIR)

            val result = ItemDomain.countryOfDispatchReader(itemIndex).apply(Nil).run(userAnswers)

            result.left.value.page mustBe CountryOfDispatchPage(itemIndex)
            result.left.value.pages mustBe Seq(
              CountryOfDispatchPage(itemIndex)
            )
          }
        }
      }
    }

    "countryOfDestinationReader" - {
      "can be read from user answers" - {
        "when consignment country of destination is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, TIR)
                .setValue(ConsignmentCountryOfDestinationPage, country)

              val expectedResult = None

              val result = ItemDomain.countryOfDestinationReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Nil
          }
        }

        "when consignment country of destination is undefined" in {
          forAll(arbitrary[Country]) {
            country =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, TIR)
                .setValue(CountryOfDestinationPage(itemIndex), country)

              val expectedResult = Some(country)

              val result = ItemDomain.countryOfDestinationReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                CountryOfDestinationPage(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {
        "when consignment country of destination is undefined" in {
          val userAnswers = emptyUserAnswers
            .setValue(TransitOperationDeclarationTypePage, TIR)

          val result = ItemDomain.countryOfDestinationReader(itemIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe CountryOfDestinationPage(itemIndex)
          result.left.value.pages mustBe Seq(
            CountryOfDestinationPage(itemIndex)
          )
        }
      }
    }

    "ucrReader" - {
      "can be read from user answers" - {

        "when consignment UCR is defined" in {
          forAll(nonEmptyString) {
            ucr =>
              val userAnswers = emptyUserAnswers
                .setValue(ConsignmentUCRPage, ucr)

              val expectedResult = None

              val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Nil
          }
        }

        "when consignment UCR is undefined" - {
          "and consignment transport is defined" - {
            "and add ucr is answered yes" in {
              forAll(nonEmptyString, arbitrary[UUID]) {
                (ucr, documentUUID) =>
                  val documents = Json
                    .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Transport",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                    .as[JsArray]

                  val userAnswers = emptyUserAnswers
                    .setValue(external.DocumentsSection, documents)
                    .setValue(AddUCRYesNoPage(itemIndex), true)
                    .setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

                  val expectedResult = Some(ucr)

                  val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    AddUCRYesNoPage(itemIndex),
                    UniqueConsignmentReferencePage(itemIndex)
                  )
              }
            }

            "and add ucr is answered no" in {
              forAll(arbitrary[UUID]) {
                documentUUID =>
                  val documents = Json
                    .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Transport",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                    .as[JsArray]
                  val userAnswers = emptyUserAnswers
                    .setValue(external.DocumentsSection, documents)
                    .setValue(AddUCRYesNoPage(itemIndex), false)

                  val expectedResult = None

                  val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    AddUCRYesNoPage(itemIndex)
                  )
              }
            }
          }

          "and consignment transport is undefined" in {
            forAll(nonEmptyString) {
              ucr =>
                val userAnswers = emptyUserAnswers
                  .setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

                val expectedResult = Some(ucr)

                val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  UniqueConsignmentReferencePage(itemIndex)
                )
            }
          }

        }
      }

      "cannot be read from user answers" - {
        "when consignment UCR is undefined" - {
          "and consignment transport is undefined" - {
            "and UCR page is unanswered" in {
              val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(emptyUserAnswers)

              result.left.value.page mustBe UniqueConsignmentReferencePage(itemIndex)
              result.left.value.pages mustBe Seq(
                UniqueConsignmentReferencePage(itemIndex)
              )
            }
          }

          "and consignment transport is defined" - {

            "and AddUCRYesNo page is unanswered" in {

              forAll(arbitrary[UUID]) {
                documentUUID =>
                  val documents = Json
                    .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Transport",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                    .as[JsArray]
                  val userAnswers = emptyUserAnswers
                    .setValue(external.DocumentsSection, documents)

                  val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

                  result.left.value.page mustBe AddUCRYesNoPage(itemIndex)
                  result.left.value.pages mustBe Seq(
                    AddUCRYesNoPage(itemIndex)
                  )
              }
            }
            "and UCR page is unanswered" in {

              forAll(arbitrary[UUID]) {
                documentUUID =>
                  val documents = Json
                    .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Transport",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                    .as[JsArray]
                  val userAnswers = emptyUserAnswers
                    .setValue(external.DocumentsSection, documents)
                    .setValue(AddUCRYesNoPage(itemIndex), true)

                  val result = ItemDomain.ucrReader(itemIndex).apply(Nil).run(userAnswers)

                  result.left.value.page mustBe UniqueConsignmentReferencePage(itemIndex)
                  result.left.value.pages mustBe Seq(
                    AddUCRYesNoPage(itemIndex),
                    UniqueConsignmentReferencePage(itemIndex)
                  )
              }
            }
          }
        }
      }
    }

    "cusCodeReader" - {

      "can be read from user answers" - {
        "when add CUS code yes/no is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddCUSCodeYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.cusCodeReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddCUSCodeYesNoPage(itemIndex)
          )
        }

        "when CUS code is answered" in {
          forAll(nonEmptyString) {
            cusCode =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCUSCodeYesNoPage(itemIndex), true)
                .setValue(CustomsUnionAndStatisticsCodePage(itemIndex), cusCode)

              val expectedResult = Some(cusCode)

              val result = ItemDomain.cusCodeReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                AddCUSCodeYesNoPage(itemIndex),
                CustomsUnionAndStatisticsCodePage(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {
        "when add CUS code yes/no is unanswered" in {
          val result = ItemDomain.cusCodeReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe AddCUSCodeYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddCUSCodeYesNoPage(itemIndex)
          )
        }

        "when CUS code is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddCUSCodeYesNoPage(itemIndex), true)

          val result = ItemDomain.cusCodeReader(itemIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe CustomsUnionAndStatisticsCodePage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddCUSCodeYesNoPage(itemIndex),
            CustomsUnionAndStatisticsCodePage(itemIndex)
          )
        }
      }
    }

    "commodityCodeReader" - {

      "can be read from user answers" - {
        "when status is set to amend " - {
          "and commodity code should not be set" in {
            val userAnswers = emptyUserAnswers.copy(status = SubmissionState.Amendment)

            val expectedResult = None

            val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Nil
          }
        }

        "when status is not set to amend " - {
          "and commodity code should be set" in {
            forAll(nonEmptyString) {
              commodityCode =>
                val userAnswers = emptyUserAnswers
                  .copy(status = SubmissionState.NotSubmitted)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), true)
                  .setValue(CommodityCodePage(itemIndex), commodityCode)
                  .setValue(TransitOperationTIRCarnetNumberPage, "")

                val expectedResult = Some(commodityCode)

                val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddCommodityCodeYesNoPage(itemIndex),
                  CommodityCodePage(itemIndex)
                )
            }

          }
        }

        "and TIR Carnet reference number is defined" - {
          "and commodity code has been provided" in {
            forAll(nonEmptyString, nonEmptyString) {
              (tirReference, commodityCode) =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), true)
                  .setValue(CommodityCodePage(itemIndex), commodityCode)

                val expectedResult = Some(commodityCode)

                val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddCommodityCodeYesNoPage(itemIndex),
                  CommodityCodePage(itemIndex)
                )
            }
          }

          "and commodity code has not been provided" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), false)

                val expectedResult = None

                val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddCommodityCodeYesNoPage(itemIndex)
                )
            }
          }
        }

        "when TIR Carnet reference number is undefined" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)

              val expectedResult = Some(commodityCode)

              val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                CommodityCodePage(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {

        "and TIR Carnet reference number is defined" - {
          "and add commodity code yes/no is unanswered" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)

                val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe AddCommodityCodeYesNoPage(itemIndex)
                result.left.value.pages mustBe Seq(
                  AddCommodityCodeYesNoPage(itemIndex)
                )
            }
          }

          "and commodity code is unanswered" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), true)

                val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe CommodityCodePage(itemIndex)
                result.left.value.pages mustBe Seq(
                  AddCommodityCodeYesNoPage(itemIndex),
                  CommodityCodePage(itemIndex)
                )
            }
          }
        }

        "when TIR Carnet reference number is undefined" - {
          "and commodity code is unanswered" in {
            val result = ItemDomain.commodityCodeReader(itemIndex).apply(Nil).run(emptyUserAnswers)

            result.left.value.page mustBe CommodityCodePage(itemIndex)
            result.left.value.pages mustBe Seq(
              CommodityCodePage(itemIndex)
            )
          }
        }
      }
    }

    "combinedNomenclatureCodeReader" - {

      "can be read from user answers" - {
        "when commodity code is defined" - {
          "and office of departure is in CL112" in {
            val userAnswers = emptyUserAnswers
              .setValue(CustomsOfficeOfDepartureInCL112Page, true)

            val expectedResult = None

            val result = ItemDomain.combinedNomenclatureCodeReader(itemIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Nil
          }

          "and office of departure is not in CL112" in {
            forAll(nonEmptyString, nonEmptyString) {
              (commodityCode, combinedNomenclatureCode) =>
                val userAnswers = emptyUserAnswers
                  .setValue(CommodityCodePage(itemIndex), commodityCode)
                  .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                  .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)
                  .setValue(CombinedNomenclatureCodePage(itemIndex), combinedNomenclatureCode)

                val expectedResult = Some(combinedNomenclatureCode)

                val result = ItemDomain.combinedNomenclatureCodeReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddCombinedNomenclatureCodeYesNoPage(itemIndex),
                  CombinedNomenclatureCodePage(itemIndex)
                )
            }
          }
        }

        "when commodity code is undefined" in {
          val expectedResult = None

          val result = ItemDomain.combinedNomenclatureCodeReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Nil
        }
      }

      "can not be read from user answers" - {

        "when add combined nomenclature code yes/no is unanswered" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(CustomsOfficeOfDepartureInCL112Page, false)

              val result = ItemDomain.combinedNomenclatureCodeReader(itemIndex).apply(Nil).run(userAnswers)

              result.left.value.page mustBe AddCombinedNomenclatureCodeYesNoPage(itemIndex)
              result.left.value.pages mustBe Seq(
                AddCombinedNomenclatureCodeYesNoPage(itemIndex)
              )
          }
        }

        "when combined nomenclature code is unanswered" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

              val result = ItemDomain.combinedNomenclatureCodeReader(itemIndex).apply(Nil).run(userAnswers)

              result.left.value.page mustBe CombinedNomenclatureCodePage(itemIndex)
              result.left.value.pages mustBe Seq(
                AddCombinedNomenclatureCodeYesNoPage(itemIndex),
                CombinedNomenclatureCodePage(itemIndex)
              )
          }
        }
      }
    }

    "dangerousGoodsReader" - {
      "can be read from user answers" - {
        "when dangerous goods added" in {
          forAll(nonEmptyString) {
            unNumber =>
              val userAnswers = emptyUserAnswers
                .setValue(AddDangerousGoodsYesNoPage(itemIndex), true)
                .setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), unNumber)

              val expectedResult = Some(
                DangerousGoodsListDomain(
                  Seq(
                    DangerousGoodsDomain(unNumber)(itemIndex, dangerousGoodsIndex)
                  )
                )(itemIndex)
              )

              val result = ItemDomain.dangerousGoodsReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                AddDangerousGoodsYesNoPage(itemIndex),
                UNNumberPage(itemIndex, dangerousGoodsIndex),
                DangerousGoodsListSection(itemIndex)
              )
          }
        }

        "when dangerous goods not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddDangerousGoodsYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.dangerousGoodsReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddDangerousGoodsYesNoPage(itemIndex)
          )
        }
      }

      "can not be read from user answers" - {
        "when add dangerous goods yes/no is unanswered" in {
          val result = ItemDomain.dangerousGoodsReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe AddDangerousGoodsYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddDangerousGoodsYesNoPage(itemIndex)
          )
        }
      }
    }

    "netWeightReader" - {
      "can be read from user answers" - {
        "when net weight is defined" - {
          "and reduced data indicator is 0" in {
            forAll(positiveBigDecimals) {
              netWeight =>
                val userAnswers = emptyUserAnswers
                  .setValue(ApprovedOperatorPage, false)
                  .setValue(AddItemNetWeightYesNoPage(itemIndex), true)
                  .setValue(NetWeightPage(itemIndex), netWeight)

                val expectedResult = Some(netWeight)

                val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddItemNetWeightYesNoPage(itemIndex),
                  NetWeightPage(itemIndex)
                )
            }
          }

          "and reduced indicator is 1" in {
            val userAnswers = emptyUserAnswers
              .setValue(ApprovedOperatorPage, true)

            val expectedResult = None

            val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Nil
          }

          "and reduced indicator is undefined (infer as false)" in {
            forAll(positiveBigDecimals) {
              netWeight =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddItemNetWeightYesNoPage(itemIndex), true)
                  .setValue(NetWeightPage(itemIndex), netWeight)

                val expectedResult = Some(netWeight)

                val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  AddItemNetWeightYesNoPage(itemIndex),
                  NetWeightPage(itemIndex)
                )
            }
          }
        }

        "when net weight is not defined" in {
          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)
            .setValue(AddItemNetWeightYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddItemNetWeightYesNoPage(itemIndex)
          )
        }
      }

      "can not be read from user answers" - {

        "when add net weight is unanswered" in {

          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)

          val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe AddItemNetWeightYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddItemNetWeightYesNoPage(itemIndex)
          )
        }

        "when net weight is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)
            .setValue(AddItemNetWeightYesNoPage(itemIndex), true)

          val result = ItemDomain.netWeightReader(itemIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe NetWeightPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddItemNetWeightYesNoPage(itemIndex),
            NetWeightPage(itemIndex)
          )
        }
      }
    }

    "supplementaryUnitsReader" - {
      "can be read from user answers" - {
        "when supplementary units added" in {
          forAll(positiveBigDecimals) {
            supplementaryUnit =>
              val userAnswers = emptyUserAnswers
                .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)
                .setValue(SupplementaryUnitsPage(itemIndex), supplementaryUnit)

              val expectedResult = Some(supplementaryUnit)

              val result = ItemDomain.supplementaryUnitsReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                AddSupplementaryUnitsYesNoPage(itemIndex),
                SupplementaryUnitsPage(itemIndex)
              )
          }
        }

        "when supplementary units not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.supplementaryUnitsReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddSupplementaryUnitsYesNoPage(itemIndex)
          )
        }
      }

      "can not be read from user answers" - {
        "when add supplementary units yes/no is unanswered" in {
          val result = ItemDomain.supplementaryUnitsReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe AddSupplementaryUnitsYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddSupplementaryUnitsYesNoPage(itemIndex)
          )
        }
      }
    }

    "packagesReader" - {
      "can be read from user answers" - {
        "when unpacked packageType added" in {
          forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), Gen.posNum[Int].sample.value, arbitrary[String]) {
            (packageType, quantity, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult =
                PackagesDomain(
                  Seq(
                    PackageDomain(
                      packageType,
                      Some(quantity),
                      Some(shippingMark)
                    )(itemIndex, packageIndex)
                  )
                )(itemIndex)

              val result = ItemDomain.packagesReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                PackageTypePage(itemIndex, packageIndex),
                NumberOfPackagesPage(itemIndex, packageIndex),
                AddShippingMarkYesNoPage(itemIndex, packageIndex),
                ShippingMarkPage(itemIndex, packageIndex),
                PackagesSection(itemIndex)
              )
          }
        }

        "when bulk packageType added" in {
          forAll(arbitrary[PackageType](arbitraryBulkPackageType), arbitrary[String]) {
            (packageType, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult =
                PackagesDomain(
                  Seq(
                    PackageDomain(
                      packageType,
                      None,
                      Some(shippingMark)
                    )(itemIndex, packageIndex)
                  )
                )(itemIndex)

              val result = ItemDomain.packagesReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                PackageTypePage(itemIndex, packageIndex),
                AddShippingMarkYesNoPage(itemIndex, packageIndex),
                ShippingMarkPage(itemIndex, packageIndex),
                PackagesSection(itemIndex)
              )
          }
        }

        "when other packageType added" in {
          forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[String], positiveInts) {
            (packageType, shippingMark, numberOfPackages) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult =
                PackagesDomain(
                  Seq(
                    PackageDomain(
                      packageType,
                      Some(numberOfPackages),
                      Some(shippingMark)
                    )(itemIndex, packageIndex)
                  )
                )(itemIndex)

              val result = ItemDomain.packagesReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                PackageTypePage(itemIndex, packageIndex),
                NumberOfPackagesPage(itemIndex, packageIndex),
                ShippingMarkPage(itemIndex, packageIndex),
                PackagesSection(itemIndex)
              )
          }
        }
      }

      "can not be read from user answers" - {
        "when packages is not added" in {
          val result = ItemDomain.packagesReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe PackageTypePage(itemIndex, packageIndex)
          result.left.value.pages mustBe Seq(
            PackageTypePage(itemIndex, packageIndex)
          )
        }
      }
    }

    "consigneeReader" - {
      "then result must not be defined" in {
        forAll(arbitraryConsigneeAnswers(emptyUserAnswers, itemIndex)) {
          userAnswers =>
            val result = ItemDomain.consigneeReader().apply(Nil).run(userAnswers)

            result.value.value must not be defined
            result.value.pages mustBe Nil
        }
      }
    }

    "documentsReader" - {

      val genForT2OrT2FConsignmentLevel =
        Gen.oneOf(T2, T2F)

      val genForT2OrT2FItemLevel: Gen[DeclarationTypeItemLevel] =
        arbitrary[DeclarationTypeItemLevel](arbitraryT2OrT2FDeclarationType)

      val genForNonT2OrT2F: Gen[DeclarationTypeItemLevel] =
        arbitrary[DeclarationTypeItemLevel](arbitraryT1DeclarationType)

      val genForOtherConsignmentLevel: Gen[String] =
        Gen.oneOf(TIR, T1)

      "can be read from user answers" - {

        "when declaration type is T2 or T2F and GB office of departure" - {

          "and Consignment level previous document is defined for all items" - {

            "and InferredAddDocumentsYesNoPage is true" in {

              forAll(genForT2OrT2FConsignmentLevel, arbitrary[UUID]) {
                (declarationType, documentUUID) =>
                  val documents = Json
                    .parse(s"""
                              |[
                              |    {
                              |      "attachToAllItems" : true,
                              |      "type" : {
                              |        "type" : "Previous",
                              |        "code" : "Code 1",
                              |        "description" : "Description 1"
                              |      },
                              |      "details" : {
                              |        "documentReferenceNumber" : "Ref no. 1",
                              |        "uuid" : "$documentUUID"
                              |      }
                              |    }
                              |]
                              |""".stripMargin)
                    .as[JsArray]

                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, declarationType)
                    .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                    .setValue(external.DocumentsSection, documents)
                    .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                  val expectedResult = Some(
                    DocumentsDomain(
                      Seq(
                        DocumentDomain(documentUUID)(itemIndex, Index(0))
                      )
                    )(itemIndex)
                  )

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    DocumentPage(itemIndex, Index(0)),
                    DocumentsSection(itemIndex)
                  )
              }
            }
          }

          "and Consignment level previous document is not defined" in {

            forAll(genForT2OrT2FConsignmentLevel, arbitrary[UUID]) {
              (declarationType, documentUUID) =>
                val userAnswers = emptyUserAnswers
                  .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                  .setValue(TransitOperationDeclarationTypePage, declarationType)
                  .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                val expectedResult = Some(
                  DocumentsDomain(
                    Seq(
                      DocumentDomain(documentUUID)(itemIndex, Index(0))
                    )
                  )(itemIndex)
                )

                val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  DocumentPage(itemIndex, Index(0)),
                  DocumentsSection(itemIndex)
                )
            }
          }

          "and Consignment level previous document is defined but not for all items" in {

            forAll(genForT2OrT2FConsignmentLevel, arbitrary[UUID]) {
              (declarationType, documentUUID) =>
                val documents = Json
                  .parse(s"""
                            |[
                            |    {
                            |      "attachToAllItems" : false,
                            |      "type" : {
                            |        "type" : "Previous",
                            |        "code" : "Code 1",
                            |        "description" : "Description 1"
                            |      },
                            |      "details" : {
                            |        "documentReferenceNumber" : "Ref no. 1",
                            |        "uuid" : "$documentUUID"
                            |      }
                            |    }
                            |]
                            |""".stripMargin)
                  .as[JsArray]

                val userAnswers = emptyUserAnswers
                  .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                  .setValue(TransitOperationDeclarationTypePage, declarationType)
                  .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)
                  .setValue(external.DocumentsSection, documents)

                val expectedResult = Some(
                  DocumentsDomain(
                    Seq(
                      DocumentDomain(documentUUID)(itemIndex, Index(0))
                    )
                  )(itemIndex)
                )

                val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  DocumentPage(itemIndex, Index(0)),
                  DocumentsSection(itemIndex)
                )
            }
          }
        }

        "when mixed declaration type (T) and GB office of departure" - {

          "and item level declaration type is T2 or T2F" - {

            "and Consignment level previous document is defined for all items" - {

              "and InferredAddDocumentsYesNoPage is true" in {

                forAll(genForT2OrT2FItemLevel, arbitrary[UUID]) {
                  (declarationType, documentUUID) =>
                    val documents = Json
                      .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Previous",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                      .as[JsArray]

                    val userAnswers = emptyUserAnswers
                      .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                      .setValue(TransitOperationDeclarationTypePage, T)
                      .setValue(DeclarationTypePage(index), declarationType)
                      .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                      .setValue(external.DocumentsSection, documents)
                      .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                    val expectedResult = Some(
                      DocumentsDomain(
                        Seq(
                          DocumentDomain(documentUUID)(itemIndex, Index(0))
                        )
                      )(itemIndex)
                    )

                    val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                    result.value.value mustBe expectedResult
                    result.value.pages mustBe Seq(
                      DocumentPage(itemIndex, Index(0)),
                      DocumentsSection(itemIndex)
                    )
                }
              }
            }

            "and Consignment level previous document is not defined" in {

              forAll(genForT2OrT2FItemLevel, arbitrary[UUID]) {
                (declarationType, documentUUID) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, T)
                    .setValue(DeclarationTypePage(index), declarationType)
                    .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                  val expectedResult = Some(
                    DocumentsDomain(
                      Seq(
                        DocumentDomain(documentUUID)(itemIndex, Index(0))
                      )
                    )(itemIndex)
                  )

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    DocumentPage(itemIndex, Index(0)),
                    DocumentsSection(itemIndex)
                  )
              }
            }

            "and Consignment level previous document is defined but not for all items" in {

              forAll(genForT2OrT2FItemLevel, arbitrary[UUID]) {
                (declarationType, documentUUID) =>
                  val documents = Json
                    .parse(s"""
                              |[
                              |    {
                              |      "attachToAllItems" : false,
                              |      "type" : {
                              |        "type" : "Previous",
                              |        "code" : "Code 1",
                              |        "description" : "Description 1"
                              |      },
                              |      "details" : {
                              |        "documentReferenceNumber" : "Ref no. 1",
                              |        "uuid" : "$documentUUID"
                              |      }
                              |    }
                              |]
                              |""".stripMargin)
                    .as[JsArray]

                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, T)
                    .setValue(DeclarationTypePage(index), declarationType)
                    .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)
                    .setValue(external.DocumentsSection, documents)

                  val expectedResult = Some(
                    DocumentsDomain(
                      Seq(
                        DocumentDomain(documentUUID)(itemIndex, Index(0))
                      )
                    )(itemIndex)
                  )

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    DocumentPage(itemIndex, Index(0)),
                    DocumentsSection(itemIndex)
                  )
              }
            }
          }

          "and item level declaration type is not T2 or T2F" - {

            "and ConsignmentAddDocumentsPage is true" - {

              "and InferredAddDocumentsYesNoPage is true" in {

                forAll(genForNonT2OrT2F, arbitrary[UUID]) {
                  (declarationType, documentUUID) =>
                    val documents = Json
                      .parse(s"""
                                |[
                                |    {
                                |      "attachToAllItems" : true,
                                |      "type" : {
                                |        "type" : "Previous",
                                |        "code" : "Code 1",
                                |        "description" : "Description 1"
                                |      },
                                |      "details" : {
                                |        "documentReferenceNumber" : "Ref no. 1",
                                |        "uuid" : "$documentUUID"
                                |      }
                                |    }
                                |]
                                |""".stripMargin)
                      .as[JsArray]

                    val userAnswers = emptyUserAnswers
                      .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                      .setValue(TransitOperationDeclarationTypePage, T)
                      .setValue(DeclarationTypePage(index), declarationType)
                      .setValue(ConsignmentAddDocumentsPage, true)
                      .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                      .setValue(external.DocumentsSection, documents)
                      .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                    val expectedResult = Some(
                      DocumentsDomain(
                        Seq(
                          DocumentDomain(documentUUID)(itemIndex, Index(0))
                        )
                      )(itemIndex)
                    )

                    val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                    result.value.value mustBe expectedResult
                    result.value.pages mustBe Seq(
                      DocumentPage(itemIndex, Index(0)),
                      DocumentsSection(itemIndex)
                    )
                }
              }
            }

            "and ConsignmentAddDocumentsPage is false" in {

              forAll(genForNonT2OrT2F) {
                declarationType =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, T)
                    .setValue(DeclarationTypePage(index), declarationType)
                    .setValue(ConsignmentAddDocumentsPage, false)

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value must not be defined
                  result.value.pages mustBe Nil
              }
            }
          }
        }

        "when any other declaration type" - {

          "and ConsignmentAddDocumentsPage is true" - {

            "and AddDocumentsYesNoPage is true" in {

              forAll(genForOtherConsignmentLevel, genForNonT2OrT2F, arbitrary[UUID]) {
                (declarationTypeConsignmentLevel, declarationTypeItemLevel, documentUUID) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                    .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                    .setValue(ConsignmentAddDocumentsPage, true)
                    .setValue(AddDocumentsYesNoPage(itemIndex), true)
                    .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                  val expectedResult = Some(
                    DocumentsDomain(
                      Seq(
                        DocumentDomain(documentUUID)(itemIndex, Index(0))
                      )
                    )(itemIndex)
                  )

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    AddDocumentsYesNoPage(itemIndex),
                    DocumentPage(itemIndex, Index(0)),
                    DocumentsSection(itemIndex)
                  )
              }
            }

            "and AddDocumentsYesNoPage is false" in {

              forAll(genForOtherConsignmentLevel, genForNonT2OrT2F) {
                (declarationTypeConsignmentLevel, declarationTypeItemLevel) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                    .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                    .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                    .setValue(ConsignmentAddDocumentsPage, true)
                    .setValue(AddDocumentsYesNoPage(itemIndex), false)

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value must not be defined
                  result.value.pages mustBe Seq(
                    AddDocumentsYesNoPage(itemIndex)
                  )
              }
            }
          }

          "and ConsignmentAddDocumentsPage is false" in {

            forAll(genForOtherConsignmentLevel, genForNonT2OrT2F) {
              (declarationTypeConsignmentLevel, declarationTypeItemLevel) =>
                val userAnswers = emptyUserAnswers
                  .setValue(CustomsOfficeOfDepartureInCL112Page, true)
                  .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                  .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                  .setValue(ConsignmentAddDocumentsPage, false)

                val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                result.value.value must not be defined
                result.value.pages mustBe Nil
            }
          }
        }

        "when non GB customs office" - {

          "and ConsignmentAddDocumentsPage is true" - {

            "and AddDocumentsYesNoPage is true" in {

              forAll(genForT2OrT2FConsignmentLevel, genForT2OrT2FItemLevel, arbitrary[UUID]) {
                (declarationTypeConsignmentLevel, declarationTypeItemLevel, documentUUID) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                    .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                    .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                    .setValue(ConsignmentAddDocumentsPage, true)
                    .setValue(AddDocumentsYesNoPage(itemIndex), true)
                    .setValue(DocumentPage(itemIndex, Index(0)), documentUUID)

                  val expectedResult = Some(
                    DocumentsDomain(
                      Seq(
                        DocumentDomain(documentUUID)(itemIndex, Index(0))
                      )
                    )(itemIndex)
                  )

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    AddDocumentsYesNoPage(itemIndex),
                    DocumentPage(itemIndex, Index(0)),
                    DocumentsSection(itemIndex)
                  )
              }
            }

            "and AddDocumentsYesNoPage is false" in {

              forAll(genForT2OrT2FConsignmentLevel, genForT2OrT2FItemLevel) {
                (declarationTypeConsignmentLevel, declarationTypeItemLevel) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                    .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                    .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                    .setValue(ConsignmentAddDocumentsPage, true)
                    .setValue(AddDocumentsYesNoPage(itemIndex), false)

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value must not be defined
                  result.value.pages mustBe Seq(
                    AddDocumentsYesNoPage(itemIndex)
                  )
              }
            }

            "and ConsignmentAddDocumentsPage is false" in {

              forAll(genForT2OrT2FConsignmentLevel, genForT2OrT2FItemLevel) {
                (declarationTypeConsignmentLevel, declarationTypeItemLevel) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                    .setValue(TransitOperationDeclarationTypePage, declarationTypeConsignmentLevel)
                    .setValue(DeclarationTypePage(index), declarationTypeItemLevel)
                    .setValue(ConsignmentAddDocumentsPage, false)

                  val result = ItemDomain.documentsReader(itemIndex).apply(Nil).run(userAnswers)

                  result.value.value must not be defined
                  result.value.pages mustBe Nil
              }
            }
          }
        }
      }
    }

    "additionalReferencesReader" - {
      "can be read from user answers" - {
        "when additional references added" in {
          forAll(arbitrary[AdditionalReference]) {
            `type` =>
              val userAnswers = emptyUserAnswers
                .setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)
                .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), `type`)
                .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), false)

              val expectedResult = Some(
                AdditionalReferencesDomain(
                  Seq(
                    AdditionalReferenceDomain(`type`, None)(itemIndex, additionalReferenceIndex)
                  )
                )(itemIndex)
              )

              val result = ItemDomain.additionalReferencesReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                AddAdditionalReferenceYesNoPage(itemIndex),
                AdditionalReferencePage(itemIndex, additionalReferenceIndex),
                AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex),
                AdditionalReferencesSection(itemIndex)
              )
          }
        }

        "when additional references not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddAdditionalReferenceYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.additionalReferencesReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddAdditionalReferenceYesNoPage(itemIndex)
          )
        }
      }

      "can not be read from user answers" - {
        "when add additional references yes/no is unanswered" in {
          val result = ItemDomain.additionalReferencesReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe AddAdditionalReferenceYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddAdditionalReferenceYesNoPage(itemIndex)
          )
        }
      }
    }

    "additionalInformationListReader" - {
      "can be read from user answers" - {
        "when additional information added" in {
          forAll(arbitrary[AdditionalInformation], nonEmptyString) {
            (`type`, value) =>
              val userAnswers = emptyUserAnswers
                .setValue(AddAdditionalInformationYesNoPage(itemIndex), true)
                .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), `type`)
                .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), value)

              val expectedResult = Some(
                AdditionalInformationListDomain(
                  Seq(
                    AdditionalInformationDomain(`type`, value)(itemIndex, additionalInformationIndex)
                  )
                )(itemIndex)
              )

              val result = ItemDomain.additionalInformationListReader(itemIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                AddAdditionalInformationYesNoPage(itemIndex),
                AdditionalInformationTypePage(itemIndex, additionalInformationIndex),
                AdditionalInformationPage(itemIndex, additionalInformationIndex),
                AdditionalInformationListSection(itemIndex)
              )
          }
        }

        "when additional information not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddAdditionalInformationYesNoPage(itemIndex), false)

          val expectedResult = None

          val result = ItemDomain.additionalInformationListReader(itemIndex).apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
          result.value.pages mustBe Seq(
            AddAdditionalInformationYesNoPage(itemIndex)
          )
        }
      }

      "can not be read from user answers" - {
        "when add additional information yes/no is unanswered" in {
          val result = ItemDomain.additionalInformationListReader(itemIndex).apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe AddAdditionalInformationYesNoPage(itemIndex)
          result.left.value.pages mustBe Seq(
            AddAdditionalInformationYesNoPage(itemIndex)
          )
        }
      }
    }

    "transportChargesReader" - {
      "can be read from user answers" in {
        forAll(arbitrary[String](arbitrarySecurityDetailsType)) {
          securityDetails =>
            val userAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, securityDetails)

            val expectedResult = None

            val result = ItemDomain.transportChargesReader().apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Nil
        }
      }

      "and security type is 0" in {
        forAll(Gen.oneOf(None, Some(false), Some(true))) {
          addConsignmentTransportChargesYesNo =>
            val userAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(AddConsignmentTransportChargesYesNoPage, addConsignmentTransportChargesYesNo)

            val expectedResult = None

            val result = ItemDomain.transportChargesReader().apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Nil
        }
      }
    }
  }
}
