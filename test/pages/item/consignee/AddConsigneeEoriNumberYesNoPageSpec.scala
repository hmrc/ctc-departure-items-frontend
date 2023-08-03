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

package pages.item.consignee

import pages.behaviours.PageBehaviours
import pages.item.consignee

class AddConsigneeEoriNumberYesNoPageSpec extends PageBehaviours {

  "AddConsigneeEoriNumberYesNoPage" - {

    beRetrievable[Boolean](AddConsigneeEoriNumberYesNoPage(itemIndex))

    beSettable[Boolean](AddConsigneeEoriNumberYesNoPage(itemIndex))

    beRemovable[Boolean](AddConsigneeEoriNumberYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove IdentificationNumber" in {

          val userAnswers = emptyUserAnswers
            .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), true)
            .setValue(IdentificationNumberPage(itemIndex), "AB123")

          val result = userAnswers.setValue(consignee.AddConsigneeEoriNumberYesNoPage(itemIndex), false)

          result.get(IdentificationNumberPage(itemIndex)) must not be defined
        }
      }

      "when yes selected" - {
        "must do nothing" in {

          val userAnswers = emptyUserAnswers
            .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), true)
            .setValue(IdentificationNumberPage(itemIndex), "AB123")

          val result = userAnswers.setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), true)

          result.get(IdentificationNumberPage(itemIndex)) must be(defined)
        }
      }
    }
  }
}
