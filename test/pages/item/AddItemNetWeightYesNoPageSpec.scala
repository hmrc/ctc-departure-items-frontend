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

package pages.item

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddItemNetWeightYesNoPageSpec extends PageBehaviours {

  "AddItemNetWeightYesNoPage" - {

    beRetrievable[Boolean](AddItemNetWeightYesNoPage(itemIndex))

    beSettable[Boolean](AddItemNetWeightYesNoPage(itemIndex))

    beRemovable[Boolean](AddItemNetWeightYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove ItemNetWeight" in {
          forAll(arbitrary[BigDecimal]) {
            NetWeight =>
              val userAnswers = emptyUserAnswers
                .setValue(AddItemNetWeightYesNoPage(itemIndex), true)
                .setValue(NetWeightPage(itemIndex), NetWeight)

              val result = userAnswers.setValue(AddItemNetWeightYesNoPage(itemIndex), false)

              result.get(NetWeightPage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[BigDecimal]) {
            NetWeight =>
              val userAnswers = emptyUserAnswers
                .setValue(AddItemNetWeightYesNoPage(itemIndex), true)
                .setValue(NetWeightPage(itemIndex), NetWeight)

              val result = userAnswers.setValue(AddItemNetWeightYesNoPage(itemIndex), true)

              result.get(NetWeightPage(itemIndex)) mustBe defined
          }
        }
      }
    }
  }
}
