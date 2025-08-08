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

package utils.cyaHelpers.item.packages

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.item.packages.index.routes
import generators.Generators
import models.reference.PackageType
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.packages.index.{AddShippingMarkYesNoPage, NumberOfPackagesPage, PackageTypePage, ShippingMarkPage}
import viewmodels.ListItem

class PackageAnswersHelperSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "PackageAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)(messages, frontendAppConfig)
              helper.listItems mustEqual Nil
          }
        }
      }

      "when user answers populated with complete packages" - {
        "when packageType is not unpacked" in {
          forAll(arbitrary[Mode], arbitrary[PackageType](arbitraryBulkPackageType)) {
            (mode, packageType) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, Index(0)), packageType)
                .setValue(AddShippingMarkYesNoPage(itemIndex, Index(0)), true)
                .setValue(ShippingMarkPage(itemIndex, Index(0)), nonEmptyString.sample.value)
                .setValue(PackageTypePage(itemIndex, Index(1)), packageType)
                .setValue(AddShippingMarkYesNoPage(itemIndex, Index(1)), true)
                .setValue(ShippingMarkPage(itemIndex, Index(1)), nonEmptyString.sample.value)

              val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)(messages, frontendAppConfig)
              helper.listItems mustEqual Seq(
                Right(
                  ListItem(
                    name = s"1 * ${packageType.toString}",
                    changeUrl = routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemovePackageController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = s"1 * ${packageType.toString}",
                    changeUrl = routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemovePackageController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }

        "when packageType is unpacked" in {
          forAll(arbitrary[Mode], Gen.posNum[Int].sample.value, arbitrary[PackageType](arbitraryUnpackedPackageType)) {
            (mode, quantity, packageType) =>
              val userAnswers = emptyUserAnswers
                .setValue(PackageTypePage(itemIndex, Index(0)), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, Index(0)), quantity)
                .setValue(AddShippingMarkYesNoPage(itemIndex, Index(0)), true)
                .setValue(ShippingMarkPage(itemIndex, Index(0)), nonEmptyString.sample.value)
                .setValue(PackageTypePage(itemIndex, Index(1)), packageType)
                .setValue(NumberOfPackagesPage(itemIndex, Index(1)), quantity)
                .setValue(AddShippingMarkYesNoPage(itemIndex, Index(1)), true)
                .setValue(ShippingMarkPage(itemIndex, Index(1)), nonEmptyString.sample.value)

              val quantityString = String.format("%,d", quantity)

              val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)(messages, frontendAppConfig)
              helper.listItems mustEqual Seq(
                Right(
                  ListItem(
                    name = s"$quantityString * ${packageType.toString}",
                    changeUrl = routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                    removeUrl = Some(routes.RemovePackageController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                  )
                ),
                Right(
                  ListItem(
                    name = s"$quantityString * ${packageType.toString}",
                    changeUrl = routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url,
                    removeUrl = Some(routes.RemovePackageController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(1)).url)
                  )
                )
              )
          }
        }
      }
    }
  }

}
