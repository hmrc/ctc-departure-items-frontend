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

package models.journeyDomain.item

import base.SpecBase
import generators.Generators
import models.DeclarationType
import models.journeyDomain.item.dangerousGoods.{DangerousGoodsDomain, DangerousGoodsListDomain}
import models.journeyDomain.item.packages.{PackageDomain, PackagesDomain}
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.{Country, PackageType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external._
import pages.item._
import pages.item.dangerousGoods.index.UNNumberPage
import pages.item.packages.index._

class ItemDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Item Domain" - {

    "can be read from user answers" - {}

    "userAnswersReader" - {
      "can not be read from user answers" - {
        "when item description page is unanswered" in {
          val result: EitherType[ItemDomain] =
            UserAnswersReader[ItemDomain](
              ItemDomain.userAnswersReader(itemIndex)
            ).run(emptyUserAnswers)

          result.left.value.page mustBe DescriptionPage(itemIndex)
        }
      }
    }

    "declarationTypeReader" - {
      "can be read from user answers" - {
        "when declaration type is not T" in {
          forAll(arbitrary[DeclarationType](arbitraryNonTDeclarationType)) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, declarationType)

              val expectedResult = None

              val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
                ItemDomain.declarationTypeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when declaration type is T" in {
          forAll(arbitrary[DeclarationType]) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, DeclarationType.T)
                .setValue(DeclarationTypePage(itemIndex), declarationType)

              val expectedResult = Some(declarationType)

              val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
                ItemDomain.declarationTypeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {
        "when transit operation declaration type is T" - {
          "and declaration type is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransitOperationDeclarationTypePage, DeclarationType.T)

            val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
              ItemDomain.declarationTypeReader(itemIndex)
            ).run(userAnswers)

            result.left.value.page mustBe DeclarationTypePage(itemIndex)
          }
        }
      }
    }

    "countryOfDispatchReader" - {
      "can be read from user answers" - {
        "when transit operation declaration type is not TIR" in {
          forAll(arbitrary[DeclarationType](arbitraryNonTIRDeclarationType)) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, declarationType)

              val expectedResult = None

              val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                ItemDomain.countryOfDispatchReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when transit operation declaration type is TIR" - {
          "and consignment country of dispatch is defined" in {
            forAll(arbitrary[Country]) {
              country =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)
                  .setValue(ConsignmentCountryOfDispatchPage, country)

                val expectedResult = None

                val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                  ItemDomain.countryOfDispatchReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and consignment country of dispatch is undefined" in {
            forAll(arbitrary[Country]) {
              country =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)
                  .setValue(CountryOfDispatchPage(itemIndex), country)

                val expectedResult = Some(country)

                val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                  ItemDomain.countryOfDispatchReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }
      }

      "can not be read from user answers" - {
        "when transit operation declaration type is TIR" - {
          "and consignment country of dispatch is undefined" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)

            val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
              ItemDomain.countryOfDispatchReader(itemIndex)
            ).run(userAnswers)

            result.left.value.page mustBe CountryOfDispatchPage(itemIndex)
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
                .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)
                .setValue(ConsignmentCountryOfDestinationPage, country)

              val expectedResult = None

              val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                ItemDomain.countryOfDestinationReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when consignment country of destination is undefined" in {
          forAll(arbitrary[Country]) {
            country =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)
                .setValue(CountryOfDestinationPage(itemIndex), country)

              val expectedResult = Some(country)

              val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                ItemDomain.countryOfDestinationReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {
        "when consignment country of destination is undefined" in {
          val userAnswers = emptyUserAnswers
            .setValue(TransitOperationDeclarationTypePage, DeclarationType.TIR)

          val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
            ItemDomain.countryOfDestinationReader(itemIndex)
          ).run(userAnswers)

          result.left.value.page mustBe CountryOfDestinationPage(itemIndex)
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

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.ucrReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when consignment UCR is undefined" in {
          forAll(nonEmptyString) {
            ucr =>
              val userAnswers = emptyUserAnswers
                .setValue(UniqueConsignmentReferencePage(itemIndex), ucr)

              val expectedResult = Some(ucr)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.ucrReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "cannot be read from user answers" - {
        "when consignment UCR is undefined" - {
          "and UCR page is unanswered" in {
            val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
              ItemDomain.ucrReader(itemIndex)
            ).run(emptyUserAnswers)

            result.left.value.page mustBe UniqueConsignmentReferencePage(itemIndex)
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

          val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
            ItemDomain.cusCodeReader(itemIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when CUS code is answered" in {
          forAll(nonEmptyString) {
            cusCode =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCUSCodeYesNoPage(itemIndex), true)
                .setValue(CustomsUnionAndStatisticsCodePage(itemIndex), cusCode)

              val expectedResult = Some(cusCode)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.cusCodeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {
        "when add CUS code yes/no is unanswered" in {
          val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
            ItemDomain.cusCodeReader(itemIndex)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe AddCUSCodeYesNoPage(itemIndex)
        }

        "when CUS code is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddCUSCodeYesNoPage(itemIndex), true)

          val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
            ItemDomain.cusCodeReader(itemIndex)
          ).run(userAnswers)

          result.left.value.page mustBe CustomsUnionAndStatisticsCodePage(itemIndex)
        }
      }
    }

    "commodityCodeReader" - {

      "can be read from user answers" - {
        "when TIR Carnet reference number is defined" - {
          "and commodity code has been provided" in {
            forAll(nonEmptyString, nonEmptyString) {
              (tirReference, commodityCode) =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), true)
                  .setValue(CommodityCodePage(itemIndex), commodityCode)

                val expectedResult = Some(commodityCode)

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  ItemDomain.commodityCodeReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and commodity code has not been provided" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), false)

                val expectedResult = None

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  ItemDomain.commodityCodeReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }

        "when TIR Carnet reference number is undefined" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)

              val expectedResult = Some(commodityCode)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.commodityCodeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {

        "when TIR Carnet reference number is defined" - {
          "and add commodity code yes/no is unanswered" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  ItemDomain.commodityCodeReader(itemIndex)
                ).run(userAnswers)

                result.left.value.page mustBe AddCommodityCodeYesNoPage(itemIndex)
            }
          }

          "and commodity code is unanswered" in {
            forAll(nonEmptyString) {
              tirReference =>
                val userAnswers = emptyUserAnswers
                  .setValue(TransitOperationTIRCarnetNumberPage, tirReference)
                  .setValue(AddCommodityCodeYesNoPage(itemIndex), true)

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  ItemDomain.commodityCodeReader(itemIndex)
                ).run(userAnswers)

                result.left.value.page mustBe CommodityCodePage(itemIndex)
            }
          }
        }

        "when TIR Carnet reference number is undefined" - {
          "and commodity code is unanswered" in {
            val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
              ItemDomain.commodityCodeReader(itemIndex)
            ).run(emptyUserAnswers)

            result.left.value.page mustBe CommodityCodePage(itemIndex)
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

            val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
              ItemDomain.combinedNomenclatureCodeReader(itemIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
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

                val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                  ItemDomain.combinedNomenclatureCodeReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }

        "when commodity code is undefined" in {
          val expectedResult = None

          val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
            ItemDomain.combinedNomenclatureCodeReader(itemIndex)
          ).run(emptyUserAnswers)

          result.value mustBe expectedResult
        }
      }

      "can not be read from user answers" - {

        "when add combined nomenclature code yes/no is unanswered" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(CustomsOfficeOfDepartureInCL112Page, false)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.combinedNomenclatureCodeReader(itemIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AddCombinedNomenclatureCodeYesNoPage(itemIndex)
          }
        }

        "when combined nomenclature code is unanswered" in {
          forAll(nonEmptyString) {
            commodityCode =>
              val userAnswers = emptyUserAnswers
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(CustomsOfficeOfDepartureInCL112Page, false)
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

              val result: EitherType[Option[String]] = UserAnswersReader[Option[String]](
                ItemDomain.combinedNomenclatureCodeReader(itemIndex)
              ).run(userAnswers)

              result.left.value.page mustBe CombinedNomenclatureCodePage(itemIndex)
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
                )
              )

              val result: EitherType[Option[DangerousGoodsListDomain]] = UserAnswersReader[Option[DangerousGoodsListDomain]](
                ItemDomain.dangerousGoodsReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when dangerous goods not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddDangerousGoodsYesNoPage(itemIndex), false)

          val expectedResult = None

          val result: EitherType[Option[DangerousGoodsListDomain]] = UserAnswersReader[Option[DangerousGoodsListDomain]](
            ItemDomain.dangerousGoodsReader(itemIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "can not be read from user answers" - {
        "when add dangerous goods yes/no is unanswered" in {
          val result: EitherType[Option[DangerousGoodsListDomain]] = UserAnswersReader[Option[DangerousGoodsListDomain]](
            ItemDomain.dangerousGoodsReader(itemIndex)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe AddDangerousGoodsYesNoPage(itemIndex)
        }
      }
    }

    "grossWeightReader" - {

      "can not be read from user answers" - {
        "when gross weight is unanswered" in {
          val result: EitherType[ItemDomain] = UserAnswersReader[ItemDomain](
            ItemDomain.userAnswersReader(itemIndex)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe DescriptionPage(itemIndex)
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

                val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
                  ItemDomain.netWeightReader(itemIndex)
                ).run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and reduced indicator is 1" in {
            val userAnswers = emptyUserAnswers
              .setValue(ApprovedOperatorPage, true)
              .setValue(AddItemNetWeightYesNoPage(itemIndex), true)

            val expectedResult = None

            val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
              ItemDomain.netWeightReader(itemIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
          }
        }
        "when net weight is not defined" in {
          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)
            .setValue(AddItemNetWeightYesNoPage(itemIndex), false)

          val expectedResult = None

          val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
            ItemDomain.netWeightReader(itemIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

      }

      "can not be read from user answers" - {

        "when add net weight is unanswered" in {

          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)

          val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
            ItemDomain.netWeightReader(itemIndex)
          ).run(userAnswers)

          result.left.value.page mustBe AddItemNetWeightYesNoPage(itemIndex)

        }

        "when net weight is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(ApprovedOperatorPage, false)
            .setValue(AddItemNetWeightYesNoPage(itemIndex), true)

          val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
            ItemDomain.netWeightReader(itemIndex)
          ).run(userAnswers)

          result.left.value.page mustBe NetWeightPage(itemIndex)

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

              val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
                ItemDomain.supplementaryUnitsReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when supplementary units not added" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), false)

          val expectedResult = None

          val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
            ItemDomain.supplementaryUnitsReader(itemIndex)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "can not be read from user answers" - {
        "when add supplementary units yes/no is unanswered" in {
          val result: EitherType[Option[BigDecimal]] = UserAnswersReader[Option[BigDecimal]](
            ItemDomain.supplementaryUnitsReader(itemIndex)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe AddSupplementaryUnitsYesNoPage(itemIndex)
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
                )

              val result: EitherType[PackagesDomain] = UserAnswersReader[PackagesDomain](
                ItemDomain.packagesReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
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
                )

              val result: EitherType[PackagesDomain] = UserAnswersReader[PackagesDomain](
                ItemDomain.packagesReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when other packageType added" in {
          forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[String]) {
            (packageType, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
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
                )

              val result: EitherType[PackagesDomain] = UserAnswersReader[PackagesDomain](
                ItemDomain.packagesReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {
        "when packages is not added" in {
          val result: EitherType[PackagesDomain] = UserAnswersReader[PackagesDomain](
            ItemDomain.packagesReader(itemIndex)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe PackageTypePage(itemIndex, packageIndex)
        }
      }
    }
  }

}
