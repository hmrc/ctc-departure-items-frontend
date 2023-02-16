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

import models.DateTime
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

trait DateTimeInputViewBehaviours extends QuestionViewBehaviours[DateTime] with ScalaCheckPropertyChecks {

  // scalastyle:off method.length
  def pageWithDateTimeInput(): Unit =
    "page with date input" - {
      "when rendered" - {

        "must display day" in {
          assertRenderedById(doc, "dateDay")
        }

        "must display month" in {
          assertRenderedById(doc, "dateMonth")
        }

        "must display year" in {
          assertRenderedById(doc, "dateYear")
        }

        "must display hour" in {
          assertRenderedById(doc, "timeHour")
        }

        "must display minute" in {
          assertRenderedById(doc, "timeMinute")
        }
      }

      "when rendered with an error" - {

        behave like pageWithErrorSummary("date")
        behave like pageWithErrorSummary("time")

        "must show an error class on the inputs for date" in {
          val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day", "month", "year")))))

          val dayInput = docWithError.getElementById("dateDay")
          assert(dayInput.hasClass("govuk-input--error"))

          val monthInput = docWithError.getElementById("dateMonth")
          assert(monthInput.hasClass("govuk-input--error"))

          val yearInput = docWithError.getElementById("dateYear")
          assert(yearInput.hasClass("govuk-input--error"))
        }

        "must show an error class on the inputs for time" in {
          val docWithError = parseView(applyView(form.withError(FormError("time", errorMessage, Seq("hour", "minute")))))

          val hourInput = docWithError.getElementById("timeHour")
          assert(hourInput.hasClass("govuk-input--error"))

          val minuteInput = docWithError.getElementById("timeMinute")
          assert(minuteInput.hasClass("govuk-input--error"))
        }

        "must have correct href on error link" - {
          "when no args" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when error in day input" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when error in month input" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateMonth")
          }

          "when error in year input" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateYear")
          }

          "when error in hour input" in {
            val docWithError = parseView(applyView(form.withError(FormError("time", errorMessage, Seq("hour")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#timeHour")
          }

          "when error in minute input" in {
            val docWithError = parseView(applyView(form.withError(FormError("time", errorMessage, Seq("minute")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#timeMinute")
          }

          "when error in day and month inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day", "month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when error in day and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when error in month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateMonth")
          }

          "when error in day, month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day", "month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when error in hour and minute inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("time", errorMessage, Seq("hour", "minute")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#timeHour")
          }

          "when error in day, month, year, hour, minute inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq("day", "month", "year", "hour", "minute")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#dateDay")
          }

          "when date error has other args" in {
            forAll(arbitrary[String]) {
              arg =>
                val docWithError = parseView(applyView(form.withError(FormError("date", errorMessage, Seq(arg)))))
                val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
                assertElementContainsHref(link, s"#dateDay")
            }
          }

          "when time error has other args" in {
            forAll(arbitrary[String]) {
              arg =>
                val docWithError = parseView(applyView(form.withError(FormError("time", errorMessage, Seq(arg)))))
                val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
                assertElementContainsHref(link, s"#timeHour")
            }
          }
        }
      }
    }
  // scalastyle:on method.length
}
