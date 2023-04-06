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
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external._
import pages.item._

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
  }

}
