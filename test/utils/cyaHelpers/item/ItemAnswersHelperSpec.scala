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

package utils.cyaHelpers.item

import base.SpecBase
import controllers.item.additionalInformation.index.routes.*
import controllers.item.additionalReference.index.routes.*
import controllers.item.consignee.routes.*
import controllers.item.dangerousGoods.index.routes.UNNumberController
import controllers.item.documents.index.routes.DocumentController
import controllers.item.packages.index.routes.PackageTypeController
import controllers.item.routes.*
import controllers.item.supplyChainActors.index.routes.SupplyChainActorTypeController
import generators.Generators
import models.reference.*
import models.{CheckMode, DeclarationTypeItemLevel, Document, DynamicAddress, Index, Mode, SubmissionState, TransportEquipment, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.*
import pages.item.additionalInformation.index.{AdditionalInformationPage, AdditionalInformationTypePage}
import pages.item.additionalReference.index.{AddAdditionalReferenceNumberYesNoPage, AdditionalReferenceNumberPage, AdditionalReferencePage}
import pages.item.dangerousGoods.index.UNNumberPage
import pages.item.documents.index.DocumentPage
import pages.item.packages.index.{AddShippingMarkYesNoPage, NumberOfPackagesPage, PackageTypePage, ShippingMarkPage}
import pages.item.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}
import pages.sections.additionalInformation.AdditionalInformationSection
import pages.sections.additionalReference.AdditionalReferenceSection
import pages.sections.dangerousGoods.DangerousGoodsSection
import pages.sections.documents.DocumentSection
import pages.sections.packages.PackageSection
import pages.sections.supplyChainActors.SupplyChainActorSection
import play.api.libs.json.Json
import services.{DocumentsService, TransportEquipmentService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

class ItemAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mode: Mode = CheckMode

  implicit private val mockTransportEquipmentsService: TransportEquipmentService = mock[TransportEquipmentService]
  implicit val mockDocumentsService: DocumentsService                            = mock[DocumentsService]

  private def buildHelper(userAnswers: UserAnswers, index: Index): ItemAnswersHelper =
    new ItemAnswersHelper(mockDocumentsService, mockTransportEquipmentsService)(userAnswers, index)

  "ItemAnswersHelper" - {

    "itemDescription" - {
      "must return None" - {
        "when DescriptionPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.itemDescription
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when DescriptionPage is defined" in {
          forAll(nonEmptyString) {
            description =>
              val answers = emptyUserAnswers.setValue(DescriptionPage(itemIndex), description)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.itemDescription.get

              result.key.value mustEqual "Description"
              result.value.value mustEqual description

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual DescriptionController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "description for item 1"
              action.id mustEqual "change-description"
          }
        }
      }
    }

    "transportEquipment" - {
      import org.mockito.ArgumentMatchers.any
      import org.mockito.Mockito.when

      "must return None" - {
        "when TransportEquipmentPage is undefined" in {
          when(mockTransportEquipmentsService.getTransportEquipment(any(), any())).thenReturn(None)
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.transportEquipment
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when TransportEquipmentPage is defined" in {
          forAll(arbitrary[TransportEquipment]) {
            transportEquipment =>
              when(mockTransportEquipmentsService.getTransportEquipment(any(), any())).thenReturn(Some(transportEquipment))
              val answers = emptyUserAnswers.setValue(TransportEquipmentPage(itemIndex), transportEquipment.uuid)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.transportEquipment.get

              result.key.value mustEqual "Transport equipment"
              result.value.value mustEqual transportEquipment.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual TransportEquipmentController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "transport equipment for item 1"
              action.id mustEqual "change-transport-equipment"
          }
        }
      }
    }

    "declarationType" - {
      "must return None" - {
        "when DeclarationTypePage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.declarationType
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when DeclarationTypePage is defined" in {
          val userAnswers = emptyUserAnswers
          forAll(arbitrary[DeclarationTypeItemLevel]) {
            declarationType =>
              val answers = userAnswers.setValue(DeclarationTypePage(itemIndex), declarationType)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.declarationType.get

              result.key.value mustEqual "Declaration type"
              val key = s"item.declarationType.${declarationType.code}"
              messages.isDefinedAt(key) mustEqual true
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual DeclarationTypeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "declaration type for item 1"
              action.id mustEqual "change-declaration-type"
          }
        }
      }
    }

    "countryOfDispatch" - {
      "must return None" - {
        "when CountryOfDispatchPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.countryOfDispatch
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDispatchPage is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val answers = emptyUserAnswers.setValue(CountryOfDispatchPage(itemIndex), country)
              val helper  = buildHelper(answers, itemIndex)
              val result  = helper.countryOfDispatch.get

              result.key.value mustEqual "Country of dispatch"
              result.value.value mustEqual country.toString

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual CountryOfDispatchController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "country of dispatch for item 1"
              action.id mustEqual "change-country-of-dispatch"
          }
        }
      }
    }

    "countryOfDestination" - {
      "must return None" - {
        "when CountryOfDestinationPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.countryOfDestination
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDestinationPage is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val answers = emptyUserAnswers.setValue(CountryOfDestinationPage(itemIndex), country)
              val helper  = buildHelper(answers, itemIndex)
              val result  = helper.countryOfDestination.get

              result.key.value mustEqual "Country of destination"
              result.value.value mustEqual country.toString

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual CountryOfDestinationController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "country of destination for item 1"
              action.id mustEqual "change-country-of-destination"
          }
        }
      }
    }

    "ucrYesNo" - {
      "must return None" - {
        "when AddUCRYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.ucrYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddUCRYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddUCRYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.ucrYesNo.get

          result.key.value mustEqual "Do you want to add a Unique Consignment Reference (UCR)?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddUCRYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add a Unique Consignment Reference (UCR) for item 1"
          action.id mustEqual "change-add-ucr"
        }
      }
    }

    "uniqueConsignmentReference" - {
      "must return None" - {
        "when UniqueConsignmentReferencePage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.uniqueConsignmentReference
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when UniqueConsignmentReferencePage is defined" in {
          forAll(nonEmptyString) {
            ucr =>
              val answers = emptyUserAnswers.setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.uniqueConsignmentReference.get

              result.key.value mustEqual "Unique Consignment Reference (UCR)"
              result.value.value mustEqual ucr

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual UniqueConsignmentReferenceController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "Unique Consignment Reference (UCR) for item 1"
              action.id mustEqual "change-ucr"
          }
        }
      }
    }

    "cusCodeYesNo" - {
      "must return None" - {
        "when AddCUSCodeYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.cusCodeYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddCUSCodeYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddCUSCodeYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.cusCodeYesNo.get

          result.key.value mustEqual "Do you want to declare a Customs Union and Statistics (CUS) code?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddCUSCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to declare a Customs Union and Statistics (CUS) code for item 1"
          action.id mustEqual "change-add-cus-code"
        }
      }
    }

    "customsUnionAndStatisticsCode" - {
      "must return None" - {
        "when CustomsUnionAndStatisticsCodePage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.customsUnionAndStatisticsCode
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when CustomsUnionAndStatisticsCodePage is defined" in {
          forAll(nonEmptyString) {
            cusCode =>
              val answers = emptyUserAnswers.setValue(CustomsUnionAndStatisticsCodePage(itemIndex), cusCode)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.customsUnionAndStatisticsCode.get

              result.key.value mustEqual "Customs Union and Statistics (CUS) code"
              result.value.value mustEqual cusCode

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual CustomsUnionAndStatisticsCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "Customs Union and Statistics (CUS) code for item 1"
              action.id mustEqual "change-cus-code"
          }
        }
      }
    }

    "commodityCodeYesNo" - {
      "must return None" - {
        "when AddCommodityCodeYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.commodityCodeYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {

        "when AddCommodityCodeYesNoPage is defined with action" in {
          val answers = emptyUserAnswers
            .copy(status = SubmissionState.NotSubmitted)
            .setValue(AddCommodityCodeYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.commodityCodeYesNo.get

          result.key.value mustEqual "Do you want to add a commodity code?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddCommodityCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add a commodity code for item 1"
          action.id mustEqual "change-add-commodity-code"
        }

        "when AddCommodityCodeYesNoPage is defined without action" in {
          val answers = emptyUserAnswers
            .copy(status = SubmissionState.Amendment)
            .setValue(AddCommodityCodeYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.commodityCodeYesNo.get

          result.key.value mustEqual "Do you want to add a commodity code?"
          result.value.value mustEqual "Yes"

          val actions = result.actions
          actions.isDefined mustEqual false
        }
      }
    }

    "commodityCode" - {
      "must return None" - {
        "when CommodityCodePage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.commodityCode
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when CommodityCodePage is defined with action" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val answers = emptyUserAnswers
                .copy(status = SubmissionState.NotSubmitted)
                .setValue(CommodityCodePage(itemIndex), commodityCode)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.commodityCode.get

              result.key.value mustEqual "Commodity code"
              result.value.value mustEqual commodityCode

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual CommodityCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "commodity code for item 1"
              action.id mustEqual "change-commodity-code"
          }
        }

        "when CommodityCodePage is defined without action" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val answers = emptyUserAnswers
                .copy(status = SubmissionState.Amendment)
                .setValue(CommodityCodePage(itemIndex), commodityCode)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.commodityCode.get

              result.key.value mustEqual "Commodity code"
              result.value.value mustEqual commodityCode

              val actions = result.actions
              actions.isDefined mustEqual false
          }
        }
      }
    }

    "combinedNomenclatureCodeYesNo" - {
      "must return None" - {
        "when AddCombinedNomenclatureCodeYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.combinedNomenclatureCodeYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddCombinedNomenclatureCodeYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.combinedNomenclatureCodeYesNo.get

          result.key.value mustEqual "Do you want to add a combined nomenclature code?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddCombinedNomenclatureCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add a combined nomenclature code for item 1"
          action.id mustEqual "change-add-combined-nomenclature-code"
        }
      }
    }

    "combinedNomenclatureCode" - {
      "must return None" - {
        "when CombinedNomenclatureCodePage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.combinedNomenclatureCode
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when CombinedNomenclatureCodePage is defined" in {
          forAll(nonEmptyString) {
            combinedNomenclatureCode =>
              val answers = emptyUserAnswers.setValue(CombinedNomenclatureCodePage(itemIndex), combinedNomenclatureCode)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.combinedNomenclatureCode.get

              result.key.value mustEqual "Combined nomenclature code"
              result.value.value mustEqual combinedNomenclatureCode

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual CombinedNomenclatureCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual "combined nomenclature code for item 1"
              action.id mustEqual "change-combined-nomenclature-code"
          }
        }
      }
    }

    "dangerousGoodsYesNo" - {
      "must return None" - {
        "when AddDangerousGoodsYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.dangerousGoodsYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddDangerousGoodsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddDangerousGoodsYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.dangerousGoodsYesNo.get

          result.key.value mustEqual "Does the item contain any dangerous goods?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddDangerousGoodsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if the item 1 contains any dangerous goods"
          action.id mustEqual "change-add-dangerous-goods"
        }
      }
    }

    "dangerousGoods" - {
      "must return None" - {
        "when dangerousGoods is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.dangerousGoods(dangerousGoodsIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when dangerousGoods is defined" in {
          forAll(nonEmptyString) {
            unNumber =>
              val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), unNumber)
              val helper      = buildHelper(userAnswers, itemIndex)
              val result      = helper.dangerousGoods(dangerousGoodsIndex).get

              result.key.value mustEqual "UN number 1"
              result.value.value mustEqual unNumber
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, dangerousGoodsIndex).url
              action.visuallyHiddenText.get mustEqual s"UN number 1"
              action.id mustEqual "change-dangerous-goods-1"
          }
        }
      }
    }

    "addOrRemoveDangerousGoods" - {
      "must return None" - {
        "when dangerous goods array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemoveDangerousGoods
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when dangerous goods array is non-empty" in {
          val answers = emptyUserAnswers.setValue(DangerousGoodsSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemoveDangerousGoods.get

          result.id mustEqual "add-or-remove-dangerous-goods"
          result.text mustEqual "Add or remove dangerous goods"
          result.href mustEqual controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(answers.lrn, mode, itemIndex).url

        }
      }
    }

    "grossWeight" - {
      "must return None" - {
        "when GrossWeightPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.grossWeight
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when GrossWeightPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            grossWeight =>
              val answers = emptyUserAnswers.setValue(GrossWeightPage(itemIndex), grossWeight)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.grossWeight.get

              result.key.value mustEqual "Gross weight"
              result.value.value mustEqual s"${grossWeight}kg"

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual GrossWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual s"gross weight of item 1"
              action.id mustEqual "change-gross-weight"
          }
        }
      }
    }

    "itemNetWeightYesNo" - {
      "must return None" - {
        "when AddItemNetWeightYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.itemNetWeightYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddItemNetWeightYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddItemNetWeightYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.itemNetWeightYesNo.get

          result.key.value mustEqual "Do you want to add the item’s net weight?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddItemNetWeightYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add the net weight of item 1"
          action.id mustEqual "change-add-item-net-weight"
        }
      }
    }

    "netWeight" - {
      "must return None" - {
        "when NetWeightPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.netWeight
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when NetWeightPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            netWeight =>
              val answers = emptyUserAnswers.setValue(NetWeightPage(itemIndex), netWeight)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.netWeight.get

              result.key.value mustEqual "Net weight"
              result.value.value mustEqual s"${netWeight}kg"

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual NetWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual s"net weight of item 1"
              action.id mustEqual "change-net-weight"
          }
        }
      }
    }

    "supplementaryUnitsYesNo" - {
      "must return None" - {
        "when AddSupplementaryUnitsYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplementaryUnitsYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddSupplementaryUnitsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.supplementaryUnitsYesNo.get

          result.key.value mustEqual "Do you want to add supplementary units?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddSupplementaryUnitsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add supplementary units for item 1"
          action.id mustEqual "change-add-supplementary-units"
        }
      }
    }

    "supplementaryUnits" - {
      "must return None" - {
        "when SupplementaryUnitsPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplementaryUnits
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when SupplementaryUnitsPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            units =>
              val answers = emptyUserAnswers
                .setValue(SupplementaryUnitsPage(itemIndex), units)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.supplementaryUnits.get

              result.key.value mustEqual "Number of supplementary units"
              result.value.value mustEqual units.toString()

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual SupplementaryUnitsController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual s"number of supplementary units for item 1"
              action.id mustEqual "change-supplementary-units"
          }
        }
      }
    }

    "package" - {
      "must return None" - {
        "when package is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.`package`(packageIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when package is defined and number of packages is undefined" in {
          val packageType = arbitrary[PackageType](arbitraryBulkPackageType).sample.value

          val initialUserAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
            .setValue(ShippingMarkPage(itemIndex, packageIndex), nonEmptyString.sample.value)

          forAll(arbitraryPackageAnswers(initialUserAnswers, itemIndex, packageIndex)) {
            userAnswers =>
              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustEqual "Package 1"
              result.value.value mustEqual s"1 * ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustEqual s"package 1"
              action.id mustEqual "change-package-1"
          }
        }

        "when package is defined and number of packages is defined" in {
          val packageType = arbitrary[PackageType](arbitraryUnpackedPackageType).sample.value
          val quantity    = Gen.posNum[Int].sample.value
          val initialUserAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)
            .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
            .setValue(ShippingMarkPage(itemIndex, packageIndex), nonEmptyString.sample.value)

          forAll(arbitraryPackageAnswers(initialUserAnswers, itemIndex, packageIndex)) {
            userAnswers =>
              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustEqual "Package 1"
              val quantityString = String.format("%,d", quantity)
              result.value.value mustEqual s"$quantityString * ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustEqual "package 1"
              action.id mustEqual "change-package-1"
          }
        }
      }
    }

    "addOrRemovePackages" - {
      "must return None" - {
        "when packages array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemovePackages
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when packages array is non-empty" in {
          val answers = emptyUserAnswers.setValue(PackageSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemovePackages.get

          result.id mustEqual "add-or-remove-packages"
          result.text mustEqual "Add or remove packages"
          result.href mustEqual controllers.item.packages.routes.AddAnotherPackageController.onPageLoad(answers.lrn, mode, itemIndex).url
        }
      }
    }

    "supplyChainActorYesNo" - {
      "must return None" - {
        "when AddSupplyChainActorYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplyChainActorYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddSupplyChainActorYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddSupplyChainActorYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.supplyChainActorYesNo.get

          result.key.value mustEqual "Do you want to add a supply chain actor for this item?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddSupplyChainActorYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"if you want to add a supply chain actor for item ${itemIndex.display}"
          action.id mustEqual "change-add-supply-chain-actors"
        }
      }
    }

    "supplyChainActor" - {
      "must return None" - {
        "when supplyChainActor is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplyChainActor(actorIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when supplyChainActor is defined" in {
          forAll(arbitrary[SupplyChainActorType], nonEmptyString) {
            (actorType, identificationNumber) =>
              val userAnswers = emptyUserAnswers
                .setValue(SupplyChainActorTypePage(itemIndex, actorIndex), actorType)
                .setValue(IdentificationNumberPage(itemIndex, actorIndex), identificationNumber)
              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.supplyChainActor(actorIndex).get

              result.key.value mustEqual "Supply chain actor 1"
              result.value.value mustEqual s"${actorType.toString} - $identificationNumber"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual SupplyChainActorTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, actorIndex).url
              action.visuallyHiddenText.get mustEqual s"supply chain actor 1"
              action.id mustEqual "change-supply-chain-actor-1"
          }
        }
      }
    }

    "addOrRemoveSupplyChainActors" - {
      "must return None" - {
        "when supply chain actors array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemoveSupplyChainActors
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when supply chain actors array is non-empty" in {
          val answers = emptyUserAnswers.setValue(SupplyChainActorSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemoveSupplyChainActors.get

          result.id mustEqual "add-or-remove-supply-chain-actors"
          result.text mustEqual "Add or remove supply chain actors"
          result.href mustEqual controllers.item.supplyChainActors.routes.AddAnotherSupplyChainActorController
            .onPageLoad(answers.lrn, mode, itemIndex)
            .url

        }
      }
    }

    "documentYesNo" - {
      "must return None" - {
        "when AddDocumentsYesNoPage is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.documentsYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when AddDocumentsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddDocumentsYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.documentsYesNo.get

          result.key.value mustEqual "Do you want to add any documents for this item?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddDocumentsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add any documents for item 1"
          action.id mustEqual "change-add-documents"
        }
      }
    }

    "consignmentDocuments" - {
      import org.mockito.ArgumentMatchers.any
      import org.mockito.Mockito.when

      "must return None" - {
        "when document is undefined" in {
          when(mockDocumentsService.getConsignmentLevelDocuments(any())).thenReturn(Nil)
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consignmentDocuments
          result mustEqual Nil
        }
      }

      "must return List(Row)" - {
        "when document is defined" in {
          forAll(arbitrary[Document]) {
            document =>
              when(mockDocumentsService.getConsignmentLevelDocuments(any())).thenReturn(List(document))
              val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

              val helper                 = buildHelper(userAnswers, itemIndex)
              val result: SummaryListRow = helper.consignmentDocuments.head

              result.key.value mustEqual "Consignment Document 1"
              result.value.value mustEqual document.toString
          }
        }
      }

    }

    "document" - {
      import org.mockito.ArgumentMatchers.any
      import org.mockito.Mockito.when

      "must return None" - {
        "when document is undefined" in {
          when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(None)
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.document(documentIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when document is defined" in {
          forAll(arbitrary[Document]) {
            document =>
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))
              val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.document(documentIndex).get

              result.key.value mustEqual "Document 1"
              result.value.value mustEqual document.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex).url
              action.visuallyHiddenText.get mustEqual "document 1"
              action.id mustEqual "change-document-1"
          }
        }
      }
    }

    "addOrRemoveDocuments" - {
      "must return None" - {
        "when documents array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemoveDocuments
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when documents array is non-empty" in {
          val answers = emptyUserAnswers.setValue(DocumentSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemoveDocuments.get

          result.id mustEqual "add-or-remove-documents"
          result.text mustEqual "Add or remove documents"
          result.href mustEqual controllers.item.documents.routes.AddAnotherDocumentController.onPageLoad(answers.lrn, mode, itemIndex).url
        }
      }
    }

    "additionalReferenceYesNo" - {
      "must return None" - {
        "when AddAdditionalReferenceYesNo is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalReferenceYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when addAdditionalReferenceYesNo is defined" in {
          val answers = emptyUserAnswers.setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.additionalReferenceYesNo.get

          result.key.value mustEqual "Do you want to add an additional reference for this item?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddAdditionalReferenceYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add an additional reference for item 1"
          action.id mustEqual "change-add-additional-reference"
        }
      }
    }

    "additionalReference" - {
      "must return None" - {
        "when additionalReference is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalReference(additionalReferenceIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when additionalReference is defined" in {
          forAll(arbitrary[AdditionalReference], nonEmptyString) {
            (additionalReference, additionalReferenceNumber) =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)
                .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
                .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), additionalReferenceNumber)
              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.additionalReference(additionalReferenceIndex).get

              result.key.value mustEqual "Additional reference 1"
              result.value.value mustEqual s"$additionalReference - $additionalReferenceNumber"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex).url
              action.visuallyHiddenText.get mustEqual s"additional reference 1"
              action.id mustEqual "change-additional-reference-1"
          }
        }
      }
    }

    "addOrRemoveAdditionalReferences" - {
      "must return None" - {
        "when additional references array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemoveAdditionalReferences
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when additional references array is non-empty" in {
          val answers = emptyUserAnswers.setValue(AdditionalReferenceSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemoveAdditionalReferences.get

          result.id mustEqual "add-or-remove-additional-references"
          result.text mustEqual "Add or remove additional references"
          result.href mustEqual controllers.item.additionalReference.routes.AddAnotherAdditionalReferenceController
            .onPageLoad(answers.lrn, mode, itemIndex)
            .url

        }
      }
    }

    "additionalInformationYesNo" - {
      "must return None" - {
        "when AddAdditionalInformationYesNo is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalInformationYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when addAdditionalInformationYesNo is defined" in {
          val answers = emptyUserAnswers
            .setValue(AddAdditionalInformationYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.additionalInformationYesNo.get

          result.key.value mustEqual "Do you want to add any additional information for this item?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddAdditionalInformationYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual "if you want to add any additional information for item 1"
          action.id mustEqual "change-add-additional-information"
        }
      }
    }

    "additionalInformation" - {
      "must return None" - {
        "when additionalInformation is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalInformation(additionalInformationIndex)
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when additionalInformation is defined" in {
          forAll(nonEmptyString, arbitrary[AdditionalInformation]) {
            (additionalInformation, additionalInformationType) =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), additionalInformationType)
                .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), additionalInformation)
              val helper = buildHelper(userAnswers, itemIndex)
              val result = helper.additionalInformation(additionalInformationIndex).get

              result.key.value mustEqual "Additional information 1"
              result.value.value mustEqual s"$additionalInformationType - $additionalInformation"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalInformationIndex).url
              action.visuallyHiddenText.get mustEqual "additional information 1"
              action.id mustEqual "change-additional-information-1"
          }
        }
      }
    }

    "addOrRemoveAdditionalInformation" - {
      "must return None" - {
        "when additional information array is empty" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addOrRemoveAdditionalInformation
          result must not be defined
        }
      }

      "must return Some(Link)" - {
        "when additional information array is non-empty" in {
          val answers = emptyUserAnswers.setValue(AdditionalInformationSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = buildHelper(answers, itemIndex)
          val result  = helper.addOrRemoveAdditionalInformation.get

          result.id mustEqual "add-or-remove-additional-information"
          result.text mustEqual "Add or remove additional information"
          result.href mustEqual controllers.item.additionalInformation.routes.AddAnotherAdditionalInformationController
            .onPageLoad(answers.lrn, mode, itemIndex)
            .url
        }
      }
    }

    "consigneeAddEoriNumberYesNo" - {
      "must return None" - {
        "when consigneeAddEoriNumberYesNo is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consigneeAddEoriNumberYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when consigneeAddEoriNumberYesNo is defined" in {
          val answers = emptyUserAnswers.setValue(consignee.AddConsigneeEoriNumberYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.consigneeAddEoriNumberYesNo.get

          result.key.value mustEqual "Do you know the consignee’s EORI number or Trader Identification Number (TIN) for this item?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddConsigneeEoriNumberYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"if you know the consignee’s EORI number or Trader Identification Number (TIN) for item ${itemIndex.display}"
          action.id mustEqual "change-has-consignee-eori"
        }
      }

    }

    "consigneeIdentificationNumber" - {

      "must return None" - {
        "when consigneeIdentificationNumber is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consigneeIdentificationNumber
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when consigneeIdentificationNumber is defined" in {
          val answers = emptyUserAnswers.setValue(consignee.IdentificationNumberPage(itemIndex), "AB123")

          val helper = buildHelper(answers, itemIndex)
          val result = helper.consigneeIdentificationNumber.get

          result.key.value mustEqual "EORI number or Trader Identification Number (TIN)"
          result.value.value mustEqual "AB123"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual IdentificationNumberController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"consignee’s EORI number or Trader Identification Number (TIN) for item ${itemIndex.display}"
          action.id mustEqual "change-consignee-identification-number"
        }
      }

    }

    "consigneeName" - {

      "must return None" - {
        "when consigneeName is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consigneeName
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when consigneeName is defined" in {
          val answers = emptyUserAnswers.setValue(consignee.NamePage(itemIndex), "John Doe")

          val helper = buildHelper(answers, itemIndex)
          val result = helper.consigneeName.get

          result.key.value mustEqual "Name"
          result.value.value mustEqual "John Doe"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual NameController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"consignee’s name for item ${itemIndex.display}"
          action.id mustEqual "change-consignee-name"
        }
      }

    }

    "consigneeCountry" - {

      "must return None" - {
        "when consigneeCountry is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consigneeCountry
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when consigneeCountry is defined" in {
          val country = Country(CountryCode("GB"), "United Kingdom")
          val answers = emptyUserAnswers.setValue(consignee.CountryPage(itemIndex), country)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.consigneeCountry.get

          result.key.value mustEqual "Country"
          result.value.value mustEqual country.toString

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual CountryController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"consignee’s country for item ${itemIndex.display}"
          action.id mustEqual "change-consignee-country"
        }
      }

    }

    "consigneeAddress" - {

      "must return None" - {
        "when consigneeAddress is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.consigneeAddress
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when consigneeAddress is defined" in {
          val address = DynamicAddress("Number and street 1", "City 2", Some("AB1 1AB"))
          val answers = emptyUserAnswers.setValue(consignee.AddressPage(itemIndex), address)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.consigneeAddress.get

          result.key.value mustEqual "Consignee’s address"
          result.value.value mustEqual "Number and street 1<br>City 2<br>AB1 1AB"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddressController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"consignee’s address for item ${itemIndex.display}"
          action.id mustEqual "change-consignee-address"
        }
      }
    }

    "addTransportChargesYesNo" - {
      "must return None" - {
        "when addTransportChargesYesNo is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.addTransportChargesYesNo
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when addTransportChargesYesNo is defined" in {
          val answers = emptyUserAnswers.setValue(AddTransportChargesYesNoPage(itemIndex), true)

          val helper = buildHelper(answers, itemIndex)
          val result = helper.addTransportChargesYesNo.get

          result.key.value mustEqual "Do you want to add a method of payment for this item’s transport charges?"
          result.value.value mustEqual "Yes"

          val actions = result.actions.get.items
          actions.size mustEqual 1
          val action = actions.head
          action.content.value mustEqual "Change"
          action.href mustEqual AddTransportChargesYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustEqual s"if you want to add a method of payment for this item’s transport charges"
          action.id mustEqual "change-add-payment-method"
        }
      }

    }

    "transportCharges" - {
      "must return None" - {
        "when transportCharges is undefined" in {
          val helper = buildHelper(emptyUserAnswers, itemIndex)
          val result = helper.transportCharges
          result must not be defined
        }
      }

      "must return Some(Row)" - {
        "when transportCharges is defined" in {
          forAll(arbitrary[TransportChargesMethodOfPayment]) {
            paymentMethod =>
              val answers = emptyUserAnswers.setValue(TransportChargesMethodOfPaymentPage(itemIndex), paymentMethod)

              val helper = buildHelper(answers, itemIndex)
              val result = helper.transportCharges.get

              result.key.value mustEqual "Payment method"
              result.value.value mustEqual paymentMethod.toString

              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual TransportChargesMethodOfPaymentController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustEqual s"payment method for this item’s transport charges"
              action.id mustEqual "change-payment-method"
          }
        }
      }

    }
  }
}
