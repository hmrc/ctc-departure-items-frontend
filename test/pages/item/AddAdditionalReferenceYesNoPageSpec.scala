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
import pages.sections.additionalReference.AdditionalReferencesSection
import play.api.libs.json.{JsArray, Json}

class AddAdditionalReferenceYesNoPageSpec extends PageBehaviours {

  "AddAdditionalReferenceYesNoPage" - {

    beRetrievable[Boolean](AddAdditionalReferenceYesNoPage(itemIndex))

    beSettable[Boolean](AddAdditionalReferenceYesNoPage(itemIndex))

    beRemovable[Boolean](AddAdditionalReferenceYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove additional references" in {
          val userAnswers = emptyUserAnswers
            .setValue(AdditionalReferencesSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddAdditionalReferenceYesNoPage(itemIndex), false)

          result.get(AdditionalReferencesSection(itemIndex)) must not be defined
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          val userAnswers = emptyUserAnswers
            .setValue(AdditionalReferencesSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddAdditionalReferenceYesNoPage(itemIndex), true)

          result.get(AdditionalReferencesSection(itemIndex)) mustBe defined
        }
      }
    }
  }
}
