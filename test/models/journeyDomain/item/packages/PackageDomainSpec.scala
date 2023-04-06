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
import generators.Generators
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Package
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.packages.index._

class PackageDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Package Domain" - {

    "can be read from user answers" - {
      "when package type is Unpacked" in {
        forAll(arbitrary[Package](arbitraryUnpackedPackage), arbitrary[Int], nonEmptyString) {
          (`package`, numberOfPackages, shippingMark) =>
            val userAnswers = emptyUserAnswers
              .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
              .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
              .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
              .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

            val expectedResult = PackageDomain(
              `package` = `package`,
              numberOfPackages = Some(numberOfPackages),
              shippingMark = Some(shippingMark)
            )(itemIndex, packageIndex)

            val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
              PackageDomain.userAnswersReader(itemIndex, packageIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
        }
      }

      "when package type is Bulk" in {
        forAll(arbitrary[Package](arbitraryBulkPackage), nonEmptyString) {
          (`package`, shippingMark) =>
            val userAnswers = emptyUserAnswers
              .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
              .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
              .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

            val expectedResult = PackageDomain(
              `package` = `package`,
              numberOfPackages = None,
              shippingMark = Some(shippingMark)
            )(itemIndex, packageIndex)

            val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
              PackageDomain.userAnswersReader(itemIndex, packageIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
        }
      }

      "when package type is Other" in {
        forAll(arbitrary[Package](arbitraryOtherPackage), nonEmptyString) {
          (`package`, shippingMark) =>
            val userAnswers = emptyUserAnswers
              .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
              .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

            val expectedResult = PackageDomain(
              `package` = `package`,
              numberOfPackages = None,
              shippingMark = Some(shippingMark)
            )(itemIndex, packageIndex)

            val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
              PackageDomain.userAnswersReader(itemIndex, packageIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
        }
      }
    }

    "cannot be read from user answers" - {
      "when package type is not answered" in {
        val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
          PackageDomain.userAnswersReader(itemIndex, packageIndex)
        ).run(emptyUserAnswers)

        result.left.value.page mustBe PackageTypePage(itemIndex, packageIndex)
      }

      "when package type is Unpacked" - {
        "and number of packages is not answered" in {
          forAll(arbitrary[Package](arbitraryUnpackedPackage)) {
            `package` =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe NumberOfPackagesPage(itemIndex, packageIndex)
          }
        }

        "and add shipping mark yes/no is not answered" in {
          forAll(arbitrary[Package](arbitraryUnpackedPackage), arbitrary[Int]) {
            (`package`, numberOfPackages) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
          }
        }

        "and shipping mark is not answered" in {
          forAll(arbitrary[Package](arbitraryUnpackedPackage), arbitrary[Int]) {
            (`package`, numberOfPackages) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
                .setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
          }
        }
      }

      "when package type is Bulk" - {

        "and add shipping mark yes/no is not answered" in {
          forAll(arbitrary[Package](arbitraryBulkPackage)) {
            `package` =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AddShippingMarkYesNoPage(itemIndex, packageIndex)
          }
        }

        "and shipping mark is not answered" in {
          forAll(arbitrary[Package](arbitraryBulkPackage)) {
            `package` =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
          }
        }
      }

      "when package type is Other" - {

        "and shipping mark is not answered" in {
          forAll(arbitrary[Package](arbitraryOtherPackage)) {
            `package` =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, packageIndex), `package`)

              val result: EitherType[PackageDomain] = UserAnswersReader[PackageDomain](
                PackageDomain.userAnswersReader(itemIndex, packageIndex)
              ).run(userAnswers)

              result.left.value.page mustBe ShippingMarkPage(itemIndex, packageIndex)
          }
        }
      }
    }
  }

}
