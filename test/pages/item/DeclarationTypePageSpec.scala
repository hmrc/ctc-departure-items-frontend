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

import models.DeclarationTypeItemLevel
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.item.documents.AddAnotherDocumentPage
import pages.sections.documents.DocumentsSection
import play.api.libs.json.{JsArray, Json}

class DeclarationTypePageSpec extends PageBehaviours {

  "DeclarationTypePage" - {

    beRetrievable[DeclarationTypeItemLevel](DeclarationTypePage(itemIndex))

    beSettable[DeclarationTypeItemLevel](DeclarationTypePage(itemIndex))

    beRemovable[DeclarationTypeItemLevel](DeclarationTypePage(itemIndex))

    "cleanup" - {
      "when answer changes" - {
        "must cleanup" - {
          "when add documents yes/no is inferred" in {
            forAll(arbitrary[DeclarationTypeItemLevel]) {
              dt1 =>
                forAll(arbitrary[DeclarationTypeItemLevel].retryUntil(_ != dt1)) {
                  dt2 =>
                    val userAnswers = emptyUserAnswers
                      .setValue(DeclarationTypePage(index), dt1)
                      .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
                      .setValue(DocumentsSection(index), JsArray(Seq(Json.obj("foo" -> "bar"))))
                      .setValue(AddAnotherDocumentPage(index), false)

                    val result = userAnswers.setValue(DeclarationTypePage(index), dt2)

                    result.get(InferredAddDocumentsYesNoPage(index)) must not be defined
                    result.get(AddDocumentsYesNoPage(index)) must not be defined
                    result.get(DocumentsSection(index)) must not be defined
                    result.get(AddAnotherDocumentPage(index)) must not be defined
                }
            }
          }

          "when add documents yes/no is not inferred" in {
            forAll(arbitrary[DeclarationTypeItemLevel]) {
              dt1 =>
                forAll(arbitrary[DeclarationTypeItemLevel].retryUntil(_ != dt1)) {
                  dt2 =>
                    val userAnswers = emptyUserAnswers
                      .setValue(DeclarationTypePage(index), dt1)
                      .setValue(AddDocumentsYesNoPage(itemIndex), true)
                      .setValue(DocumentsSection(index), JsArray(Seq(Json.obj("foo" -> "bar"))))
                      .setValue(AddAnotherDocumentPage(index), false)

                    val result = userAnswers.setValue(DeclarationTypePage(index), dt2)

                    result.get(InferredAddDocumentsYesNoPage(index)) must not be defined
                    result.get(AddDocumentsYesNoPage(index)) must not be defined
                    result.get(DocumentsSection(index)) must not be defined
                    result.get(AddAnotherDocumentPage(index)) must not be defined
                }
            }
          }
        }
      }

      "when answer doesn't changes" - {
        "must not cleanup" in {
          forAll(arbitrary[DeclarationTypeItemLevel]) {
            dt =>
              val userAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage(index), dt)
                .setValue(AddDocumentsYesNoPage(itemIndex), true)
                .setValue(DocumentsSection(index), JsArray(Seq(Json.obj("foo" -> "bar"))))
                .setValue(AddAnotherDocumentPage(index), false)

              val result = userAnswers.setValue(DeclarationTypePage(index), dt)

              result.get(AddDocumentsYesNoPage(index)) mustBe defined
              result.get(DocumentsSection(index)) mustBe defined
              result.get(AddAnotherDocumentPage(index)) mustBe defined
          }
        }
      }
    }
  }
}
