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
import pages.sections.dangerousGoods.DangerousGoodsListSection
import play.api.libs.json.{JsArray, Json}

class AddDangerousGoodsYesNoPageSpec extends PageBehaviours {

  "AddDangerousGoodsYesNoPage" - {

    beRetrievable[Boolean](AddDangerousGoodsYesNoPage(itemIndex))

    beSettable[Boolean](AddDangerousGoodsYesNoPage(itemIndex))

    beRemovable[Boolean](AddDangerousGoodsYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove dangerous goods list" in {
          val userAnswers = emptyUserAnswers
            .setValue(DangerousGoodsListSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddDangerousGoodsYesNoPage(itemIndex), false)

          result.get(DangerousGoodsListSection(itemIndex)) must not be defined
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          val userAnswers = emptyUserAnswers
            .setValue(DangerousGoodsListSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddDangerousGoodsYesNoPage(itemIndex), true)

          result.get(DangerousGoodsListSection(itemIndex)) must be(defined)
        }
      }
    }
  }
}
