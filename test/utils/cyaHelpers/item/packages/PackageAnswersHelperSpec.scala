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

package utils.cyaHelpers.item.packages

import base.SpecBase
import controllers.item.packages.index.routes
import generators.Generators
import models.reference.PackageType
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.packages.index.{PackageTypePage, ShippingMarkPage}
import viewmodels.ListItem

class PackageAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "PackageAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete packages" in {
        forAll(arbitrary[Mode], arbitrary[PackageType](arbitraryOtherPackage)) {
          (mode, `package`) =>
            val userAnswers = emptyUserAnswers
              .setValue(PackageTypePage(itemIndex, Index(0)), `package`)
              .setValue(ShippingMarkPage(itemIndex, Index(0)), nonEmptyString.sample.value)
              .setValue(PackageTypePage(itemIndex, Index(1)), `package`)
              .setValue(ShippingMarkPage(itemIndex, Index(1)), nonEmptyString.sample.value)

            val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)
            helper.listItems mustBe Seq(
              Right(
                ListItem(
                  name = `package`.toString,
                  changeUrl = routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url,
                  removeUrl = Some(routes.RemovePackageController.onPageLoad(userAnswers.lrn, mode, itemIndex, Index(0)).url)
                )
              ),
              Right(
                ListItem(
                  name = `package`.toString,
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
