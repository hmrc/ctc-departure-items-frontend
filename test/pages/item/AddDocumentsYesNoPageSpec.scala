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
import pages.sections.documents.DocumentsSection
import play.api.libs.json.{JsArray, Json}

class AddDocumentsYesNoPageSpec extends PageBehaviours {

  "AddDocumentsYesNoPage" - {

    beRetrievable[Boolean](AddDocumentsYesNoPage(itemIndex))

    beSettable[Boolean](AddDocumentsYesNoPage(itemIndex))

    beRemovable[Boolean](AddDocumentsYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove documents and inferred" in {
          val userAnswers = emptyUserAnswers
            .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
            .setValue(DocumentsSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddDocumentsYesNoPage(itemIndex), false)

          result.get(InferredAddDocumentsYesNoPage(itemIndex)) must not be defined
          result.get(DocumentsSection(itemIndex)) must not be defined
        }
      }

      "when yes selected" - {
        "must remove inferred" in {
          val userAnswers = emptyUserAnswers
            .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
            .setValue(DocumentsSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddDocumentsYesNoPage(itemIndex), true)

          result.get(InferredAddDocumentsYesNoPage(itemIndex)) must not be defined
          result.get(DocumentsSection(itemIndex)) mustBe defined
        }
      }
    }
  }
}

class InferredAddDocumentsYesNoPageSpec extends PageBehaviours {

  "InferredAddDocumentsYesNoPage" - {

    beRetrievable[Boolean](InferredAddDocumentsYesNoPage(itemIndex))

    beSettable[Boolean](InferredAddDocumentsYesNoPage(itemIndex))

    beRemovable[Boolean](InferredAddDocumentsYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove documents and non-inferred" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddDocumentsYesNoPage(itemIndex), true)
            .setValue(DocumentsSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(InferredAddDocumentsYesNoPage(itemIndex), false)

          result.get(AddDocumentsYesNoPage(itemIndex)) must not be defined
          result.get(DocumentsSection(itemIndex)) must not be defined
        }
      }

      "when yes selected" - {
        "must remove non-inferred" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddDocumentsYesNoPage(itemIndex), true)
            .setValue(DocumentsSection(itemIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(InferredAddDocumentsYesNoPage(itemIndex), true)

          result.get(AddDocumentsYesNoPage(itemIndex)) must not be defined
          result.get(DocumentsSection(itemIndex)) mustBe defined
        }
      }
    }
  }
}
