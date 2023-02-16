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

package views.behaviours

import models.PostalCodeAddress

trait PostalCodeAddressViewBehaviours extends QuestionViewBehaviours[PostalCodeAddress] {

  val fields = Seq("streetNumber", "postalCode", "country")

  def pageWithAddressInput(): Unit =
    "page with an address input" - {

      "when rendered" - {

        for (field <- fields) {
          s"must contain an input for $field" in {
            assertRenderedById(doc, field)
          }

          s"must contain a label for the field '$field'" in {
            val labels = doc.getElementsByAttributeValue("for", field)
            labels.size mustBe 1
            assertElementContainsText(labels.first(), messages(s"$prefix.$field"))
          }
        }

        behave like pageWithoutErrorSummary()
      }

      for (field <- fields)
        s"when rendered with an error for field '$field'" - {

          behave like pageWithErrorSummary(field)

          s"must show an error in the label for field '$field'" in {
            val formGroupError = getElementByClass(docWithError(field), "govuk-form-group--error")
            formGroupError.getElementsByClass("govuk-label").first().attr("for") mustBe field
            formGroupError.getElementsByClass("govuk-error-message").first().id() mustBe s"$field-error"
          }
        }
    }
}
