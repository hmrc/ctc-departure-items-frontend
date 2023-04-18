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

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddShippingMarkYesNoPageSpec extends PageBehaviours {

  "AddShippingMarkYesNoPage" - {

    beRetrievable[Boolean](AddShippingMarkYesNoPage(itemIndex, packageIndex))

    beSettable[Boolean](AddShippingMarkYesNoPage(itemIndex, packageIndex))

    beRemovable[Boolean](AddShippingMarkYesNoPage(itemIndex, packageIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove shipping mark" in {
          forAll(arbitrary[String]) {
            shippingMark =>
              val userAnswers = emptyUserAnswers
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val result = userAnswers.setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), false)

              result.get(ShippingMarkPage(itemIndex, packageIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            shippingMark =>
              val userAnswers = emptyUserAnswers
                .setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)
                .setValue(ShippingMarkPage(itemIndex, packageIndex), shippingMark)

              val result = userAnswers.setValue(AddShippingMarkYesNoPage(itemIndex, packageIndex), true)

              result.get(ShippingMarkPage(itemIndex, packageIndex)) must be(defined)
          }
        }
      }
    }
  }
}
