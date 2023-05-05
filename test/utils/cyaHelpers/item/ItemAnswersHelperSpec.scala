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
import controllers.item.dangerousGoods.index.routes.UNNumberController
import controllers.item.documents.index.routes.DocumentController
import controllers.item.packages.index.routes.PackageTypeController
import controllers.item.additionalReference.index.routes.AdditionalReferenceController
import controllers.item.routes._
import generators.Generators
import models.reference.{AdditionalReference, Country, PackageType}
import models.{DeclarationType, Document, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item._
import pages.item.additionalReference.index.AdditionalReferencePage
import pages.item.dangerousGoods.index.UNNumberPage
import pages.item.documents.index.DocumentPage
import pages.item.packages.index.{AddShippingMarkYesNoPage, NumberOfPackagesPage, PackageTypePage, ShippingMarkPage}

class ItemAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {
  "ItemAnswersHelper" - {

    "itemDescription" - {
      "must return None" - {
        "when DescriptionPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.itemDescription
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when DescriptionPage is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, description) =>
              val answers = emptyUserAnswers
                .setValue(DescriptionPage(itemIndex), description)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.itemDescription.get

              result.key.value mustBe "Description"
              result.value.value mustBe description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe DescriptionController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "description for item 1"
              action.id mustBe "change-description-1"
          }
        }
      }
    }

    "declarationType" - {
      "must return None" - {
        "when DeclarationTypePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.declarationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when DeclarationTypePage is defined" in {
          val userAnswers = emptyUserAnswers
          forAll(arbitrary[Mode], Gen.oneOf(DeclarationType.itemValues)) {
            (mode, declarationType) =>
              val answers = userAnswers
                .setValue(DeclarationTypePage(itemIndex), declarationType)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
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
              action.id mustBe "change-declaration-type-1"
          }
        }
      }
    }

    "countryOfDispatch" - {
      "must return None" - {
        "when CountryOfDispatchPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.countryOfDispatch
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDispatchPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Country]) {
            (mode, country) =>
              val answers = emptyUserAnswers
                .setValue(CountryOfDispatchPage(itemIndex), country)
              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.countryOfDispatch.get

              result.key.value mustBe "Country of dispatch"
              result.value.value mustBe country.description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CountryOfDispatchController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "country of dispatch for item 1"
              action.id mustBe "change-country-of-dispatch-1"
          }
        }
      }
    }

    "countryOfDestination" - {
      "must return None" - {
        "when CountryOfDestinationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.countryOfDestination
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDestinationPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Country]) {
            (mode, country) =>
              val answers = emptyUserAnswers
                .setValue(CountryOfDestinationPage(itemIndex), country)
              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.countryOfDestination.get

              result.key.value mustBe "Country of destination"
              result.value.value mustBe country.description

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CountryOfDestinationController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "country of destination for item 1"
              action.id mustBe "change-country-of-destination-1"
          }
        }
      }
    }

    "ucrYesNo" - {
      "must return None" - {
        "when AddUCRYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.ucrYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddUCRYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddUCRYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.ucrYesNo.get

              result.key.value mustBe "Do you want to add a Unique Consignment Reference (UCR)?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddUCRYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add a Unique Consignment Reference (UCR) for item 1"
              action.id mustBe "change-add-ucr-1"
          }
        }
      }
    }

    "uniqueConsignmentReference" - {
      "must return None" - {
        "when UniqueConsignmentReferencePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.uniqueConsignmentReference
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when UniqueConsignmentReferencePage is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, ucr) =>
              val answers = emptyUserAnswers
                .setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.uniqueConsignmentReference.get

              result.key.value mustBe "Unique Consignment Reference (UCR)"
              result.value.value mustBe ucr

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe UniqueConsignmentReferenceController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "Unique Consignment Reference (UCR) for item 1"
              action.id mustBe "change-ucr-1"
          }
        }
      }
    }

    "cusCodeYesNo" - {
      "must return None" - {
        "when AddCUSCodeYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.cusCodeYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddCUSCodeYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddCUSCodeYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.cusCodeYesNo.get

              result.key.value mustBe "Do you want to declare a Customs Union and Statistics (CUS) code?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddCUSCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to declare a Customs Union and Statistics (CUS) code for item 1"
              action.id mustBe "change-add-cus-code-1"
          }
        }
      }
    }

    "customsUnionAndStatisticsCode" - {
      "must return None" - {
        "when CustomsUnionAndStatisticsCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.customsUnionAndStatisticsCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CustomsUnionAndStatisticsCodePage is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, cusCode) =>
              val answers = emptyUserAnswers
                .setValue(CustomsUnionAndStatisticsCodePage(itemIndex), cusCode)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.customsUnionAndStatisticsCode.get

              result.key.value mustBe "Customs Union and Statistics (CUS) code"
              result.value.value mustBe cusCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CustomsUnionAndStatisticsCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "Customs Union and Statistics (CUS) code for item 1"
              action.id mustBe "change-cus-code-1"
          }
        }
      }
    }

    "commodityCodeYesNo" - {
      "must return None" - {
        "when AddCommodityCodeYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.commodityCodeYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddCommodityCodeYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddCommodityCodeYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.commodityCodeYesNo.get

              result.key.value mustBe "Do you want to add a commodity code?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddCommodityCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add a commodity code for item 1"
              action.id mustBe "change-add-commodity-code-1"
          }
        }
      }
    }

    "commodityCode" - {
      "must return None" - {
        "when CommodityCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.commodityCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CommodityCodePage is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, commodityCode) =>
              val answers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.commodityCode.get

              result.key.value mustBe "Commodity code"
              result.value.value mustBe commodityCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CommodityCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "commodity code for item 1"
              action.id mustBe "change-commodity-code-1"
          }
        }
      }
    }

    "combinedNomenclatureCodeYesNo" - {
      "must return None" - {
        "when AddCombinedNomenclatureCodeYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.combinedNomenclatureCodeYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddCombinedNomenclatureCodeYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.combinedNomenclatureCodeYesNo.get

              result.key.value mustBe "Do you want to add a combined nomenclature code?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddCombinedNomenclatureCodeYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add a combined nomenclature code for item 1"
              action.id mustBe "change-add-combined-nomenclature-code-1"
          }
        }
      }
    }

    "combinedNomenclatureCode" - {
      "must return None" - {
        "when CombinedNomenclatureCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.combinedNomenclatureCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CombinedNomenclatureCodePage is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, combinedNomenclatureCode) =>
              val answers = emptyUserAnswers
                .setValue(CombinedNomenclatureCodePage(itemIndex), combinedNomenclatureCode)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.combinedNomenclatureCode.get

              result.key.value mustBe "Combined nomenclature code"
              result.value.value mustBe combinedNomenclatureCode

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe CombinedNomenclatureCodeController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "combined nomenclature code for item 1"
              action.id mustBe "change-combined-nomenclature-code-1"
          }
        }
      }
    }

    "dangerousGoodsYesNo" - {
      "must return None" - {
        "when AddDangerousGoodsYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.dangerousGoodsYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddDangerousGoodsYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddDangerousGoodsYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.dangerousGoodsYesNo.get

              result.key.value mustBe "Does the item contain any dangerous goods?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddDangerousGoodsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if the item 1 contains any dangerous goods"
              action.id mustBe "change-add-dangerous-goods-1"
          }
        }
      }
    }

    "dangerousGoods" - {
      "must return None" - {
        "when dangerousGoods is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.dangerousGoods(dangerousGoodsIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when dangerousGoods is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, unNumber) =>
              val userAnswers = emptyUserAnswers.setValue(UNNumberPage(itemIndex, dangerousGoodsIndex), unNumber)
              val helper      = new ItemAnswersHelper(userAnswers, mode, itemIndex)
              val result      = helper.dangerousGoods(dangerousGoodsIndex).get

              result.key.value mustBe "UN number 1"
              result.value.value mustBe unNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe UNNumberController.onPageLoad(userAnswers.lrn, mode, itemIndex, dangerousGoodsIndex).url
              action.visuallyHiddenText.get mustBe s"UN number 1 - $unNumber"
              action.id mustBe "change-dangerous-goods-1"
          }
        }
      }
    }

    "grossWeight" - {
      "must return None" - {
        "when GrossWeightPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.grossWeight
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when GrossWeightPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[BigDecimal]) {
            (mode, grossWeight) =>
              val answers = emptyUserAnswers
                .setValue(GrossWeightPage(itemIndex), grossWeight)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.grossWeight.get

              result.key.value mustBe "Gross weight"
              result.value.value mustBe grossWeight.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe GrossWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"gross weight of item 1"
              action.id mustBe "change-gross-weight-1"
          }
        }
      }
    }

    "itemNetWeightYesNo" - {
      "must return None" - {
        "when AddItemNetWeightYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.itemNetWeightYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddItemNetWeightYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddItemNetWeightYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.itemNetWeightYesNo.get

              result.key.value mustBe "Do you want to add the item’s net weight?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddItemNetWeightYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add the net weight of item 1"
              action.id mustBe "change-add-item-net-weight-1"
          }
        }
      }
    }

    "netWeight" - {
      "must return None" - {
        "when NetWeightPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.netWeight
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when NetWeightPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[BigDecimal]) {
            (mode, netWeight) =>
              val answers = emptyUserAnswers
                .setValue(NetWeightPage(itemIndex), netWeight)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.netWeight.get

              result.key.value mustBe "Net weight"
              result.value.value mustBe netWeight.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe NetWeightController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"net weight of item 1"
              action.id mustBe "change-net-weight-1"
          }
        }
      }
    }

    "supplementaryUnitsYesNo" - {
      "must return None" - {
        "when AddSupplementaryUnitsYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.supplementaryUnitsYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddSupplementaryUnitsYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.supplementaryUnitsYesNo.get

              result.key.value mustBe "Do you want to add supplementary units?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddSupplementaryUnitsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add supplementary units for item 1"
              action.id mustBe "change-add-supplementary-units-1"
          }
        }
      }
    }

    "supplementaryUnits" - {
      "must return None" - {
        "when SupplementaryUnitsPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.supplementaryUnits
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when SupplementaryUnitsPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[BigDecimal]) {
            (mode, units) =>
              val answers = emptyUserAnswers
                .setValue(SupplementaryUnitsPage(itemIndex), units)

              val helper = new ItemAnswersHelper(answers, mode, itemIndex)
              val result = helper.supplementaryUnits.get

              result.key.value mustBe "Number of supplementary units"
              result.value.value mustBe units.toString()

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe SupplementaryUnitsController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe s"number of supplementary units for item 1"
              action.id mustBe "change-supplementary-units-1"
          }
        }
      }
    }

    "package" - {
      "must return None" - {
        "when package is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.`package`(packageIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when package is defined and number of packages is undefined" in {
          val packageType = arbitrary[PackageType](arbitraryOtherPackageType).sample.value

          val initialUserAnswers = emptyUserAnswers
            .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
            .setValue(ShippingMarkPage(itemIndex, packageIndex), nonEmptyString.sample.value)

          forAll(arbitrary[Mode], arbitraryPackageAnswers(initialUserAnswers, itemIndex, packageIndex)) {
            (mode, userAnswers) =>
              val helper = new ItemAnswersHelper(userAnswers, mode, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustBe "Package 1"
              result.value.value mustBe s"1 ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustBe s"1 ${packageType.toString}"
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

          forAll(arbitrary[Mode], arbitraryPackageAnswers(initialUserAnswers, itemIndex, packageIndex)) {
            (mode, userAnswers) =>
              val helper = new ItemAnswersHelper(userAnswers, mode, itemIndex)
              val result = helper.`package`(packageIndex).get

              result.key.value mustBe "Package 1"
              val quantityString = String.format("%,d", quantity)
              result.value.value mustBe s"$quantityString ${packageType.toString}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex).url
              action.visuallyHiddenText.get mustBe s"$quantityString ${packageType.toString}"
              action.id mustBe "change-package-1"
          }
        }
      }
    }

    "documentYesNo" - {
      "must return None" - {
        "when AddDocumentsYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.documentsYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddDocumentsYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddDocumentsYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.documentsYesNo.get

              result.key.value mustBe "Do you want to add any documents for this item?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddDocumentsYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add any documents for item 1"
              action.id mustBe "change-add-documents-1"
          }
        }
      }
    }

    "document" - {
      "must return None" - {
        "when document is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.document(documentIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when document is defined" in {
          forAll(arbitrary[Mode], arbitrary[Document]) {
            (mode, document) =>
              val userAnswers = emptyUserAnswers.setValue(DocumentPage(itemIndex, documentIndex), document)
              val helper      = new ItemAnswersHelper(userAnswers, mode, itemIndex)
              val result      = helper.document(documentIndex).get

              result.key.value mustBe document.toString
              result.value.value mustBe document.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex).url
              action.visuallyHiddenText.get mustBe document.toString
              action.id mustBe "change-document-1"
          }
        }
      }
    }

    "additionalReferenceYesNo" - {
      "must return None" - {
        "when AddAdditionalReferenceYesNo is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.additionalReferenceYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addAdditionalReferenceYesNo is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)

              val helper = new ItemAnswersHelper(answers, mode, index)
              val result = helper.additionalReferenceYesNo.get

              result.key.value mustBe "Do you want to add an additional reference for this item?"
              result.value.value mustBe "Yes"

              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AddAdditionalReferenceYesNoController.onPageLoad(answers.lrn, mode, itemIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add an additional reference for item 1"
              action.id mustBe "change-add-additional-reference-1"
          }
        }
      }
    }

    "additionalReference" - {
      "must return None" - {
        "when additionalReference is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ItemAnswersHelper(emptyUserAnswers, mode, itemIndex)
              val result = helper.additionalReference(additionalReferenceIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when additionalReference is defined" in {
          forAll(arbitrary[Mode], arbitrary[AdditionalReference]) {
            (mode, additionalReference) =>
              val userAnswers = emptyUserAnswers.setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), additionalReference)
              val helper      = new ItemAnswersHelper(userAnswers, mode, itemIndex)
              val result      = helper.additionalReference(additionalReferenceIndex).get
              println(result)

              result.key.value mustBe "Additional reference 1"
              result.value.value mustBe additionalReference.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex).url
              action.visuallyHiddenText.get mustBe s"additional reference 1 - $additionalReference"
              action.id mustBe "change-additional-reference-1"
          }
        }
      }
    }
  }
}
