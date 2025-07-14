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

class AddSupplementaryUnitsYesNoPageSpec extends PageBehaviours {

  "AddSupplementaryUnitsYesNoPage" - {

    beRetrievable[Boolean](AddSupplementaryUnitsYesNoPage(itemIndex))

    beSettable[Boolean](AddSupplementaryUnitsYesNoPage(itemIndex))

    beRemovable[Boolean](AddSupplementaryUnitsYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove supplementary units" in {
          forAll(arbitrary[BigDecimal]) {
            units =>
              val userAnswers = emptyUserAnswers
                .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)
                .setValue(SupplementaryUnitsPage(itemIndex), units)

              val result = userAnswers.setValue(AddSupplementaryUnitsYesNoPage(itemIndex), false)

              result.get(SupplementaryUnitsPage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[BigDecimal]) {
            units =>
              val userAnswers = emptyUserAnswers
                .setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)
                .setValue(SupplementaryUnitsPage(itemIndex), units)

              val result = userAnswers.setValue(AddSupplementaryUnitsYesNoPage(itemIndex), true)

              result.get(SupplementaryUnitsPage(itemIndex)) mustBe defined
          }
        }
      }
    }
  }
}
