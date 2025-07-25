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

class AddCombinedNomenclatureCodeYesNoPageSpec extends PageBehaviours {

  "AddCombinedNomenclatureCodeYesNoPage" - {

    beRetrievable[Boolean](AddCombinedNomenclatureCodeYesNoPage(itemIndex))

    beSettable[Boolean](AddCombinedNomenclatureCodeYesNoPage(itemIndex))

    beRemovable[Boolean](AddCombinedNomenclatureCodeYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove combined nomenclature code" in {
          forAll(arbitrary[String]) {
            code =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)
                .setValue(CombinedNomenclatureCodePage(itemIndex), code)

              val result = userAnswers.setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), false)

              result.get(CombinedNomenclatureCodePage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            code =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)
                .setValue(CombinedNomenclatureCodePage(itemIndex), code)

              val result = userAnswers.setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)

              result.get(CombinedNomenclatureCodePage(itemIndex)) mustBe defined
          }
        }
      }
    }
  }
}
