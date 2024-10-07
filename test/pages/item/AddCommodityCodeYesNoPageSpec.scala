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

import pages.behaviours.PageBehaviours

class AddCommodityCodeYesNoPageSpec extends PageBehaviours {

  "AddCommodityCodeYesNoPage" - {

    beRetrievable[Boolean](AddCommodityCodeYesNoPage(itemIndex))

    beSettable[Boolean](AddCommodityCodeYesNoPage(itemIndex))

    beRemovable[Boolean](AddCommodityCodeYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove commodity code and nomenclature code" in {
          forAll(nonEmptyString, nonEmptyString) {
            (commodityCode, nomenclatureCode) =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCommodityCodeYesNoPage(itemIndex), true)
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)
                .setValue(CombinedNomenclatureCodePage(itemIndex), nomenclatureCode)

              val result = userAnswers.setValue(AddCommodityCodeYesNoPage(itemIndex), false)

              result.get(CommodityCodePage(itemIndex)) must not be defined
              result.get(AddCombinedNomenclatureCodeYesNoPage(itemIndex)) must not be defined
              result.get(CombinedNomenclatureCodePage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(nonEmptyString, nonEmptyString) {
            (commodityCode, nomenclatureCode) =>
              val userAnswers = emptyUserAnswers
                .setValue(AddCommodityCodeYesNoPage(itemIndex), true)
                .setValue(CommodityCodePage(itemIndex), commodityCode)
                .setValue(AddCombinedNomenclatureCodeYesNoPage(itemIndex), true)
                .setValue(CombinedNomenclatureCodePage(itemIndex), nomenclatureCode)

              val result = userAnswers.setValue(AddCommodityCodeYesNoPage(itemIndex), true)

              result.get(CommodityCodePage(itemIndex)) must be(defined)
              result.get(AddCombinedNomenclatureCodeYesNoPage(itemIndex)) must be(defined)
              result.get(CombinedNomenclatureCodePage(itemIndex)) must be(defined)
          }
        }
      }
    }
  }
}
