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

package pages.item.packages.index

import models.reference.PackageType
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class PackageTypePageSpec extends PageBehaviours {

  "PackageTypePage" - {

    beRetrievable[PackageType](PackageTypePage(itemIndex, packageIndex))

    beSettable[PackageType](PackageTypePage(itemIndex, packageIndex))

    beRemovable[PackageType](PackageTypePage(itemIndex, packageIndex))

    "cleanup" - {

      "when value changes" - {
        "must clean up subsequent pages" - {
          "when unpackedPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType)) {
              value =>
                forAll(arbitrary[PackageType].retryUntil(_ != value), Gen.posNum[Int].sample.value, arbitrary[String]) {
                  (differentValue, quantity, shippingMark) =>
                    val userAnswers = emptyUserAnswers
                      .setValue(PackageTypePage(itemIndex, packageIndex), value)
                      .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)
                      .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                      .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                    val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), differentValue)

                    result.get(NumberOfPackagesPage(itemIndex, packageIndex)) mustNot be(defined)
                    result.get(AddShippingMarkYesNoPage(itemIndex, packageIndex)) mustNot be(defined)
                    result.get(ShippingMarkPage(itemIndex, packageIndex)) mustNot be(defined)
                }
            }
          }

          "when bulkPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType)) {
              value =>
                forAll(arbitrary[PackageType].retryUntil(_ != value), arbitrary[String]) {
                  (differentValue, shippingMark) =>
                    val userAnswers = emptyUserAnswers
                      .setValue(PackageTypePage(itemIndex, packageIndex), value)
                      .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                      .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                    val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), differentValue)

                    result.get(AddShippingMarkYesNoPage(itemIndex, packageIndex)) mustNot be(defined)
                    result.get(ShippingMarkPage(itemIndex, packageIndex)) mustNot be(defined)
                }
            }
          }

          "when otherPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType)) {
              value =>
                forAll(arbitrary[PackageType].retryUntil(_ != value), arbitrary[String]) {
                  (differentValue, shippingMark) =>
                    val userAnswers = emptyUserAnswers
                      .setValue(PackageTypePage(itemIndex, packageIndex), value)
                      .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                    val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), differentValue)

                    result.get(ShippingMarkPage(itemIndex, packageIndex)) mustNot be(defined)
                }
            }
          }
        }
      }

      "when value has not changed" - {
        "must not clean up subsequent page" - {
          "when unpackedPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryUnpackedPackageType), Gen.posNum[Int].sample.value, arbitrary[String]) {
              (value, quantity, shippingMark) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), value)
                  .setValue(NumberOfPackagesPage(itemIndex, packageIndex), quantity)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                  .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), value)

                result.get(NumberOfPackagesPage(itemIndex, packageIndex)) must be(defined)
                result.get(ShippingMarkPage(itemIndex, packageIndex)) must be(defined)
            }
          }

          "when bulkPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryBulkPackageType), arbitrary[String]) {
              (value, shippingMark) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), value)
                  .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                  .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), value)

                result.get(ShippingMarkPage(itemIndex, packageIndex)) must be(defined)
            }
          }

          "when otherPackage Type" in {
            forAll(arbitrary[PackageType](arbitraryOtherPackageType), arbitrary[String]) {
              (value, shippingMark) =>
                val userAnswers = emptyUserAnswers
                  .setValue(PackageTypePage(itemIndex, packageIndex), value)
                  .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

                val result = userAnswers.setValue(PackageTypePage(itemIndex, packageIndex), value)

                result.get(ShippingMarkPage(itemIndex, packageIndex)) must be(defined)
            }
          }
        }
      }
    }
  }
}
