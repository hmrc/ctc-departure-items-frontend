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

package views.item

import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.item.GrossWeightBeforeYouContinueView

class GrossWeightBeforeYouContinueViewSpec extends ViewBehaviours with Generators {

  private val mode: Mode        = arbitrary[Mode].sample.value
  override val itemIndex: Index = arbitrary[Index].sample.value

  override def view: HtmlFormat.Appendable =
    injector
      .instanceOf[GrossWeightBeforeYouContinueView]
      .apply(lrn, mode, itemIndex)(fakeRequest, messages)

  override val prefix: String = "item.gross.index.beforeYouContinue"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item $itemIndex - Measurement")

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "You entered 0 for the gross mass. This means if you need to enter 0 " +
      "for the number of packages then you must add at least another item with:"
  )

  behave like pageWithList("govuk-list--bullet",
                           "Gross mass having a value different from ‘0’ or " +
                             "the gross mass for the current item must be different from ‘0’."
  )

  behave like pageWithContent("p", "If not, the office of departure will reject this declaration.")

  behave like pageWithSubmitButton("Continue")
}
