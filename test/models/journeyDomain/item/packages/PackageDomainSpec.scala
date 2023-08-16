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

package models.journeyDomain.item.packages

import base.SpecBase
import config.PhaseConfig
import generators.Generators
import models.Phase
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.PackageType
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.packages.index._

class PackageDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Package Domain" - {
    val mockTransitionPhaseConfig = mock[PhaseConfig]
    when(mockTransitionPhaseConfig.phase).thenReturn(Phase.Transition)

    val mockPostTransitionPhaseConfig = mock[PhaseConfig]
    when(mockPostTransitionPhaseConfig.phase).thenReturn(Phase.PostTransition)

    "can be read from user answers" - {

      "when in transition" - {

        "when package type is unpacked" in {

          forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int], nonEmptyString) {
            (packageType, numberOfPackages, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = Some(numberOfPackages),
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }

        }

        "when package type is Bulk" in {
          forAll(arbitrary[PackageType](arbitraryBulkPackageType), nonEmptyString) {
            (packageType, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = None,
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when package type is Other" in {
          forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[Int], nonEmptyString) {
            (packageType, numberOfPackages, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = Some(numberOfPackages),
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "when in post-transition" - {
        "when package type is Unpacked" in {
          forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int], nonEmptyString) {
            (packageType, numberOfPackages, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = Some(numberOfPackages),
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when package type is Bulk" in {
          forAll(arbitrary[PackageType](arbitraryBulkPackageType), nonEmptyString) {
            (packageType, shippingMark) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = None,
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when package type is Other" in {
          forAll(arbitrary[PackageType](arbitraryOtherPackageType), nonEmptyString, arbitrary[Int]) {
            (packageType, shippingMark, numberOfPackages) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val expectedResult = PackageDomain(
                `type` = packageType,
                numberOfPackages = Some(numberOfPackages),
                shippingMark = Some(shippingMark)
              )(itemIndex, packageIndex)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }
    }

    "cannot be read from user answers" - {

      "when in transition" - {
        "when package type is not answered" in {
          val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
            PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe PackageTypePage(itemIndex, packageIndex)
        }

        "when package type is Unpacked" - {
          "and number of packages is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe NumberOfPackagesPage(itemIndex, packageIndex)
            }
          }

          "and add shipping mark yes/no is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }

        "when package type is Bulk" - {

          "and add shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }

        "when package type is Other" - {

          "and number of packages is not answered" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe NumberOfPackagesPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }
      }

      "when in post-transition" - {
        "when package type is not answered" in {
          val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
            PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
          ).run(emptyUserAnswers)

          result.left.value.page mustBe PackageTypePage(itemIndex, packageIndex)
        }

        "when package type is Unpacked" - {
          "and number of packages is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe NumberOfPackagesPage(itemIndex, packageIndex)
            }
          }

          "and add shipping mark yes/no is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }

        "when package type is Bulk" - {

          "and add shipping mark yes/no is not answered" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }

        "when package type is Other" - {

          "and number of packages is not answered" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType)) {
              packageType =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe NumberOfPackagesPage(itemIndex, packageIndex)
            }
          }

          "and shipping mark is not answered" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[Int]) {
              (packageType, numberOfPackages) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), packageType)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)

                val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                  PackageDomain.userAnswersReader(itemIndex, packageIndex)(mockPostTransitionPhaseConfig)
                ).run(userAnswers)

                result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
            }
          }
        }
      }
    }
  }
}
