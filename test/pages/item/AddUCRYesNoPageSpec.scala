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

package pages.item

import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class AddUCRYesNoPageSpec extends PageBehaviours {

  "AddUCRYesNoPage" - {

    beRetrievable[Boolean](AddUCRYesNoPage(itemIndex))

    beSettable[Boolean](AddUCRYesNoPage(itemIndex))

    beRemovable[Boolean](AddUCRYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove UCR" in {
          forAll(arbitrary[String]) {
            UCR =>
              val userAnswers = emptyUserAnswers
                .setValue(AddUCRYesNoPage(itemIndex), true)
                .setValue(UniqueConsignmentReferencePage(itemIndex), UCR)

              val result = userAnswers.setValue(AddUCRYesNoPage(itemIndex), false)

              result.get(UniqueConsignmentReferencePage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            UCR =>
              val userAnswers = emptyUserAnswers
                .setValue(AddUCRYesNoPage(itemIndex), true)
                .setValue(UniqueConsignmentReferencePage(itemIndex), UCR)

              val result = userAnswers.setValue(AddUCRYesNoPage(itemIndex), true)

              result.get(UniqueConsignmentReferencePage(itemIndex)) must be(defined)
          }
        }
      }
    }
  }

}
