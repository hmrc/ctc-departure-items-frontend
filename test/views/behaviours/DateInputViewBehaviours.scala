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

import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

import java.time.LocalDate

trait DateInputViewBehaviours extends QuestionViewBehaviours[LocalDate] with ScalaCheckPropertyChecks {

  // scalastyle:off method.length
  def pageWithDateInput(): Unit =
    "page with date input" - {
      "when rendered" - {

        "must display day" in {
          assertRenderedById(doc, "valueDay")
        }

        "must display month" in {
          assertRenderedById(doc, "valueMonth")
        }

        "must display year" in {
          assertRenderedById(doc, "valueYear")
        }
      }

      behave like pageWithErrorSummary()

      "when rendered with an error" - {

        "must show an error class on the inputs" in {
          val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))

          val dayInput = docWithError.getElementById("valueDay")
          assert(dayInput.hasClass("govuk-input--error"))

          val monthInput = docWithError.getElementById("valueMonth")
          assert(monthInput.hasClass("govuk-input--error"))

          val yearInput = docWithError.getElementById("valueYear")
          assert(yearInput.hasClass("govuk-input--error"))
        }

        "must have correct href on error link" - {
          "when no args" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueDay")
          }

          "when error in day input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueDay")
          }

          "when error in month input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueMonth")
          }

          "when error in year input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueYear")
          }

          "when error in day and month inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueDay")
          }

          "when error in day and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueDay")
          }

          "when error in month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueMonth")
          }

          "when error in day, month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#valueDay")
          }

          "when error has other args" in {
            forAll(arbitrary[String]) {
              arg =>
                val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq(arg)))))
                val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
                assertElementContainsHref(link, "#valueDay")
            }
          }
        }
      }
    }
  // scalastyle:on method.length
}
