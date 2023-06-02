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

package utils.cyaHelpers.item

import base.SpecBase
import controllers.item.additionalInformation.index.routes._
import controllers.item.additionalReference.index.routes._
import controllers.item.dangerousGoods.index.routes.UNNumberController
import controllers.item.documents.index.routes.DocumentController
import controllers.item.packages.index.routes.PackageTypeController
import controllers.item.routes._
import generators.Generators
import models.reference.{AdditionalInformation, AdditionalReference, Country, PackageType}
import models.{CheckMode, DeclarationType, Document, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item._
import pages.item.additionalInformation.index.{AdditionalInformationPage, AdditionalInformationTypePage}
import pages.item.additionalReference.index.{AddAdditionalReferenceNumberYesNoPage, AdditionalReferenceNumberPage, AdditionalReferencePage}
import pages.item.dangerousGoods.index.UNNumberPage
import pages.item.documents.index.DocumentPage
import pages.item.packages.index.{AddShippingMarkYesNoPage, NumberOfPackagesPage, PackageTypePage, ShippingMarkPage}
import pages.sections.additionalInformation.AdditionalInformationSection
import pages.sections.additionalReference.AdditionalReferenceSection
import pages.sections.dangerousGoods.DangerousGoodsSection
import pages.sections.documents.DocumentSection
import pages.sections.packages.PackageSection
import play.api.libs.json.Json
import services.DocumentsService

class ItemAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mode: Mode = CheckMode

  "ItemAnswersHelper" - {

    "itemDescription" - {
      "must return None" - {
        "when DescriptionPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.itemDescription
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when DescriptionPage is defined" in {
          forAll(nonEmptyString) {
            description =>
              val answers = emptyUserAnswers.setValue(DescriptionPage(itemIndex), description)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.itemDescription.get

              result.key.value mustBe "Description"
              result.value.value mustBe description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe DescriptionController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "description for item 1"
              action.id mustBe "change-description"
          }
        }
      }
    }

    "transportEquipment" - {
      "must return None" - {
        "when TransportEquipmentPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.transportEquipment
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when TransportEquipmentPage is defined" in {
          val userAnswers = emptyUserAnswers
          forAll(positiveIntsMinMax(1: Int, 9999: Int)) {
            transportEquipment =>
              val answers = userAnswers.setValue(TransportEquipmentPage(itemIndex), transportEquipment)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.transportEquipment.get

              result.key.value mustBe "Transport equipment"
              result.value.value mustBe transportEquipment.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe TransportEquipmentController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "transport equipment for item 1"
              action.id mustBe "change-transport-equipment"
          }
        }
      }
    }

    "declarationType" - {
      "must return None" - {
        "when DeclarationTypePage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.declarationType
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when DeclarationTypePage is defined" in {
          val userAnswers = emptyUserAnswers
          forAll(Gen.oneOf(DeclarationType.itemValues)) {
            declarationType =>
              val answers = userAnswers.setValue(DeclarationTypePage(itemIndex), declarationType)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.declarationType.get

              result.key.value mustBe "Declaration type"
              val key = s"${DeclarationType.messageKeyPrefix}.$declarationType"
              messages.isDefinedAt(key) mustBe true
              result.value.value mustBe messages(key)
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe DeclarationTypeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "declaration type for item 1"
              action.id mustBe "change-declaration-type"
          }
        }
      }
    }

    "countryOfDispatch" - {
      "must return None" - {
        "when CountryOfDispatchPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.countryOfDispatch
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDispatchPage is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val answers = emptyUserAnswers.setValue(CountryOfDispatchPage(itemIndex), country)
              val helper  = new ItemAnswersHelper(answers, itemIndex)
              val result  = helper.countryOfDispatch.get

              result.key.value mustBe "Country of dispatch"
              result.value.value mustBe country.description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CountryOfDispatchController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "country of dispatch for item 1"
              action.id mustBe "change-country-of-dispatch"
          }
        }
      }
    }

    "countryOfDestination" - {
      "must return None" - {
        "when CountryOfDestinationPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.countryOfDestination
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDestinationPage is defined" in {
          forAll(arbitrary[Country]) {
            country =>
              val answers = emptyUserAnswers.setValue(CountryOfDestinationPage(itemIndex), country)
              val helper  = new ItemAnswersHelper(answers, itemIndex)
              val result  = helper.countryOfDestination.get

              result.key.value mustBe "Country of destination"
              result.value.value mustBe country.description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CountryOfDestinationController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "country of destination for item 1"
              action.id mustBe "change-country-of-destination"
          }
        }
      }
    }

    "ucrYesNo" - {
      "must return None" - {
        "when AddUCRYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.ucrYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddUCRYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddUCRYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.ucrYesNo.get

          result.key.value mustBe "Do you want to add a Unique Consignment Reference (UCR)?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddUCRYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add a Unique Consignment Reference (UCR) for item 1"
          action.id mustBe "change-add-ucr"
        }
      }
    }

    "uniqueConsignmentReference" - {
      "must return None" - {
        "when UniqueConsignmentReferencePage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.uniqueConsignmentReference
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when UniqueConsignmentReferencePage is defined" in {
          forAll(nonEmptyString) {
            ucr =>
              val answers = emptyUserAnswers.setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.uniqueConsignmentReference.get

              result.key.value mustBe "Unique Consignment Reference (UCR)"
              result.value.value mustBe ucr

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe UniqueConsignmentReferenceController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "Unique Consignment Reference (UCR) for item 1"
              action.id mustBe "change-ucr"
          }
        }
      }
    }

    "cusCodeYesNo" - {
      "must return None" - {
        "when AddCUSCodeYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.cusCodeYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddCUSCodeYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddCUSCodeYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.cusCodeYesNo.get

          result.key.value mustBe "Do you want to declare a Customs Union and Statistics (CUS) code?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddCUSCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to declare a Customs Union and Statistics (CUS) code for item 1"
          action.id mustBe "change-add-cus-code"
        }
      }
    }

    "customsUnionAndStatisticsCode" - {
      "must return None" - {
        "when CustomsUnionAndStatisticsCodePage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.customsUnionAndStatisticsCode
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when CustomsUnionAndStatisticsCodePage is defined" in {
          forAll(nonEmptyString) {
            cusCode =>
              val answers = emptyUserAnswers.setValue(CustomsUnionAndStatisticsCodePage(itemIndex), cusCode)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.customsUnionAndStatisticsCode.get

              result.key.value mustBe "Customs Union and Statistics (CUS) code"
              result.value.value mustBe cusCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CustomsUnionAndStatisticsCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "Customs Union and Statistics (CUS) code for item 1"
              action.id mustBe "change-cus-code"
          }
        }
      }
    }

    "commodityCodeYesNo" - {
      "must return None" - {
        "when AddCommodityCodeYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.commodityCodeYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddCommodityCodeYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddCommodityCodeYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.commodityCodeYesNo.get

          result.key.value mustBe "Do you want to add a commodity code?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddCommodityCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add a commodity code for item 1"
          action.id mustBe "change-add-commodity-code"
        }
      }
    }

    "commodityCode" - {
      "must return None" - {
        "when CommodityCodePage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.commodityCode
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when CommodityCodePage is defined" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val answers = emptyUserAnswers.setValue(CommodityCodePage(itemIndex), commodityCode)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.commodityCode.get

              result.key.value mustBe "Commodity code"
              result.value.value mustBe commodityCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CommodityCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "commodity code for item 1"
              action.id mustBe "change-commodity-code"
          }
        }
      }
    }

    "combinedNomenclatureCodeYesNo" - {
      "must return None" - {
        "when AddCombinedNomenclatureCodeYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.combinedNomenclatureCodeYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddCombinedNomenclatureCodeYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.combinedNomenclatureCodeYesNo.get

          result.key.value mustBe "Do you want to add a combined nomenclature code?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddCombinedNomenclatureCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add a combined nomenclature code for item 1"
          action.id mustBe "change-add-combined-nomenclature-code"
        }
      }
    }

    "combinedNomenclatureCode" - {
      "must return None" - {
        "when CombinedNomenclatureCodePage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.combinedNomenclatureCode
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when CombinedNomenclatureCodePage is defined" in {
          forAll(nonEmptyString) {
            combinedNomenclatureCode =>
              val answers = emptyUserAnswers.setValue(CombinedNomenclatureCodePage(itemIndex), combinedNomenclatureCode)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.combinedNomenclatureCode.get

              result.key.value mustBe "Combined nomenclature code"
              result.value.value mustBe combinedNomenclatureCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CombinedNomenclatureCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "combined nomenclature code for item 1"
              action.id mustBe "change-combined-nomenclature-code"
          }
        }
      }
    }

    "dangerousGoodsYesNo" - {
      "must return None" - {
        "when AddDangerousGoodsYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.dangerousGoodsYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddDangerousGoodsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddDangerousGoodsYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.dangerousGoodsYesNo.get

          result.key.value mustBe "Does the item contain any dangerous goods?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddDangerousGoodsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if the item 1 contains any dangerous goods"
          action.id mustBe "change-add-dangerous-goods"
        }
      }
    }

    "dangerousGoods" - {
      "must return None" - {
        "when dangerousGoods is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.dangerousGoods(dangerousGoodsIndex)
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when dangerousGoods is defined" in {
          forAll(nonEmptyString) {
            unNumber =>
              val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), unNumber)
              val helper      = new ItemAnswersHelper(userAnswers, itemIndex)
              val result      = helper.dangerousGoods(dangerousGoodsIndex).get

              result.key.value mustBe "UN number 1"
              result.value.value mustBe unNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, dangerousGoodsIndex).url
              action.visuallyHiddenText.get mustBe s"UN number 1"
              action.id mustBe "change-dangerous-goods-1"
          }
        }
      }
    }

    "addOrRemoveDangerousGoods" - {
      "must return None" - {
        "when dangerous goods array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
              val result = helper.addOrRemoveDangerousGoods
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when dangerous goods array is non-empty" in {
          val answers = emptyUserAnswers.setValue(DangerousGoodsSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = new ItemAnswersHelper(answers, itemIndex)
          val result  = helper.addOrRemoveDangerousGoods.get

          result.id mustBe "add-or-remove-dangerous-goods"
          result.text mustBe "Add or remove dangerous goods"
          result.href mustBe controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(answers.lrn, mode, itemIndex).url

        }
      }
    }

    "grossWeight" - {
      "must return None" - {
        "when GrossWeightPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.grossWeight
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when GrossWeightPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            grossWeight =>
              val answers = emptyUserAnswers.setValue(GrossWeightPage(itemIndex), grossWeight)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.grossWeight.get

              result.key.value mustBe "Gross weight"
              result.value.value mustBe grossWeight.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe GrossWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"gross weight of item 1"
              action.id mustBe "change-gross-weight"
          }
        }
      }
    }

    "itemNetWeightYesNo" - {
      "must return None" - {
        "when AddItemNetWeightYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.itemNetWeightYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddItemNetWeightYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddItemNetWeightYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.itemNetWeightYesNo.get

          result.key.value mustBe "Do you want to add the item’s net weight?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddItemNetWeightYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add the net weight of item 1"
          action.id mustBe "change-add-item-net-weight"
        }
      }
    }

    "netWeight" - {
      "must return None" - {
        "when NetWeightPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.netWeight
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when NetWeightPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            netWeight =>
              val answers = emptyUserAnswers.setValue(NetWeightPage(itemIndex), netWeight)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.netWeight.get

              result.key.value mustBe "Net weight"
              result.value.value mustBe netWeight.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe NetWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"net weight of item 1"
              action.id mustBe "change-net-weight"
          }
        }
      }
    }

    "supplementaryUnitsYesNo" - {
      "must return None" - {
        "when AddSupplementaryUnitsYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplementaryUnitsYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddSupplementaryUnitsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.supplementaryUnitsYesNo.get

          result.key.value mustBe "Do you want to add supplementary units?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddSupplementaryUnitsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add supplementary units for item 1"
          action.id mustBe "change-add-supplementary-units"
        }
      }
    }

    "supplementaryUnits" - {
      "must return None" - {
        "when SupplementaryUnitsPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.supplementaryUnits
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when SupplementaryUnitsPage is defined" in {
          forAll(arbitrary[BigDecimal]) {
            units =>
              val answers = emptyUserAnswers
                .setValue(SupplementaryUnitsPage(itemIndex), units)

              val helper = new ItemAnswersHelper(answers, itemIndex)
              val result = helper.supplementaryUnits.get

              result.key.value mustBe "Number of supplementary units"
              result.value.value mustBe units.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe SupplementaryUnitsController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"number of supplementary units for item 1"
              action.id mustBe "change-supplementary-units"
          }
        }
      }
    }

    "package" - {
      "must return None" - {
        "when package is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.`package`(packageIndex)
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when package is defined and number of packages is undefined" in {
          val packageType = arbitrary[PackageType](arbitraryOtherPackageType).sample.value

          val initialUserAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(ShippingMarkPage(itemIndex, packageIndex), nonEmptyString.sample.value)

          forAll(arbitraryPackageAnswers(initialUserAnswers, itemIndex, packageIndex)) {
            userAnswers =>
              val helper = new ItemAnswersHelper(userAnswers, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustBe "Package 1"
              result.value.value mustBe s"1 ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustBe s"package 1"
              action.id mustBe "change-package-1"
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
              val helper = new ItemAnswersHelper(userAnswers, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustBe "Package 1"
              val quantityString = String.format("%,d", quantity)
              result.value.value mustBe s"$quantityString ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustBe "package 1"
              action.id mustBe "change-package-1"
          }
        }
      }
    }

    "addOrRemovePackages" - {
      "must return None" - {
        "when packages array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
              val result = helper.addOrRemovePackages
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when packages array is non-empty" in {
          val answers = emptyUserAnswers.setValue(PackageSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = new ItemAnswersHelper(answers, itemIndex)
          val result  = helper.addOrRemovePackages.get

          result.id mustBe "add-or-remove-packages"
          result.text mustBe "Add or remove packages"
          result.href mustBe controllers.item.packages.routes.AddAnotherPackageController.onPageLoad(answers.lrn, mode, itemIndex).url
        }
      }
    }

    "documentYesNo" - {
      "must return None" - {
        "when AddDocumentsYesNoPage is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.documentsYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AddDocumentsYesNoPage is defined" in {
          val answers = emptyUserAnswers.setValue(AddDocumentsYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.documentsYesNo.get

          result.key.value mustBe "Do you want to add any documents for this item?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddDocumentsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add any documents for item 1"
          action.id mustBe "change-add-documents"
        }
      }
    }

    "document" - {
      import org.mockito.ArgumentMatchers.any
      import org.mockito.Mockito.when

      implicit val mockDocumentsService: DocumentsService = mock[DocumentsService]

      "must return None" - {
        "when document is undefined" in {
          when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(None)
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.document(documentIndex)
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when document is defined" in {
          forAll(arbitrary[Document]) {
            document =>
              when(mockDocumentsService.getDocument(any(), any(), any())).thenReturn(Some(document))
              val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document.uuid)

              val helper = new ItemAnswersHelper(userAnswers, itemIndex)
              val result = helper.document(documentIndex).get

              result.key.value mustBe "Document 1"
              result.value.value mustBe document.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex).url
              action.visuallyHiddenText.get mustBe "document 1"
              action.id mustBe "change-document-1"
          }
        }
      }
    }

    "addOrRemoveDocuments" - {
      "must return None" - {
        "when documents array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
              val result = helper.addOrRemoveDocuments
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when documents array is non-empty" in {
          val answers = emptyUserAnswers.setValue(DocumentSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = new ItemAnswersHelper(answers, itemIndex)
          val result  = helper.addOrRemoveDocuments.get

          result.id mustBe "add-or-remove-documents"
          result.text mustBe "Add or remove documents"
          result.href mustBe controllers.item.documents.routes.AddAnotherDocumentController.onPageLoad(answers.lrn, mode, itemIndex).url
        }
      }
    }

    "additionalReferenceYesNo" - {
      "must return None" - {
        "when AddAdditionalReferenceYesNo is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalReferenceYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when addAdditionalReferenceYesNo is defined" in {
          val answers = emptyUserAnswers.setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.additionalReferenceYesNo.get

          result.key.value mustBe "Do you want to add an additional reference for this item?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddAdditionalReferenceYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add an additional reference for item 1"
          action.id mustBe "change-add-additional-reference"
        }
      }
    }

    "additionalReference" - {
      "must return None" - {
        "when additionalReference is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalReference(additionalReferenceIndex)
          result mustBe None
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
              val helper = new ItemAnswersHelper(userAnswers, itemIndex)
              val result = helper.additionalReference(additionalReferenceIndex).get

              result.key.value mustBe "Additional reference 1"
              result.value.value mustBe s"$additionalReference - $additionalReferenceNumber"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex).url
              action.visuallyHiddenText.get mustBe s"additional reference 1"
              action.id mustBe "change-additional-reference-1"
          }
        }
      }
    }

    "addOrRemoveAdditionalReferences" - {
      "must return None" - {
        "when additional references array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
              val result = helper.addOrRemoveAdditionalReferences
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when additional references array is non-empty" in {
          val answers = emptyUserAnswers.setValue(AdditionalReferenceSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = new ItemAnswersHelper(answers, itemIndex)
          val result  = helper.addOrRemoveAdditionalReferences.get

          result.id mustBe "add-or-remove-additional-references"
          result.text mustBe "Add or remove additional references"
          result.href mustBe controllers.item.additionalReference.routes.AddAnotherAdditionalReferenceController
            .onPageLoad(answers.lrn, mode, itemIndex)
            .url

        }
      }
    }

    "additionalInformationYesNo" - {
      "must return None" - {
        "when AddAdditionalInformationYesNo is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalInformationYesNo
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when addAdditionalInformationYesNo is defined" in {
          val answers = emptyUserAnswers
            .setValue(AddAdditionalInformationYesNoPage(itemIndex), true)

          val helper = new ItemAnswersHelper(answers, itemIndex)
          val result = helper.additionalInformationYesNo.get

          result.key.value mustBe "Do you want to add any additional information for this item?"
          result.value.value mustBe "Yes"

          val actions = result.actions.get.items
          actions.size mustBe 1
          val action = actions.head
          action.content.value mustBe "Change"
          action.href mustBe AddAdditionalInformationYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
          action.visuallyHiddenText.get mustBe "if you want to add any additional information for item 1"
          action.id mustBe "change-add-additional-information"
        }
      }
    }

    "additionalInformation" - {
      "must return None" - {
        "when additionalInformation is undefined" in {
          val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
          val result = helper.additionalInformation(additionalInformationIndex)
          result mustBe None
        }
      }

      "must return Some(Row)" - {
        "when additionalInformation is defined" in {
          forAll(nonEmptyString, arbitrary[AdditionalInformation]) {
            (additionalInformation, additionalInformationType) =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), additionalInformationType)
                .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), additionalInformation)
              val helper = new ItemAnswersHelper(userAnswers, itemIndex)
              val result = helper.additionalInformation(additionalInformationIndex).get

              result.key.value mustBe "Additional information 1"
              result.value.value mustBe s"$additionalInformationType - $additionalInformation"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalInformationIndex).url
              action.visuallyHiddenText.get mustBe "additional information 1"
              action.id mustBe "change-additional-information-1"
          }
        }
      }
    }

    "addOrRemoveAdditionalInformation" - {
      "must return None" - {
        "when additional information array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, itemIndex)
              val result = helper.addOrRemoveAdditionalInformation
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when additional information array is non-empty" in {
          val answers = emptyUserAnswers.setValue(AdditionalInformationSection(Index(0), Index(0)), Json.obj("foo" -> "bar"))
          val helper  = new ItemAnswersHelper(answers, itemIndex)
          val result  = helper.addOrRemoveAdditionalInformation.get

          result.id mustBe "add-or-remove-additional-information"
          result.text mustBe "Add or remove additional information"
          result.href mustBe controllers.item.additionalInformation.routes.AddAnotherAdditionalInformationController
            .onPageLoad(answers.lrn, mode, itemIndex)
            .url
        }
      }
    }

  }
}
