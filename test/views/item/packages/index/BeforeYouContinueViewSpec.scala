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

package views.item.packages.index

import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.item.packages.index.BeforeYouContinueView
import models.Index

class BeforeYouContinueViewSpec extends ViewBehaviours with Generators {

  private val mode: Mode           = arbitrary[Mode].sample.value
  override val itemIndex: Index    = arbitrary[Index].sample.value
  override val packageIndex: Index = arbitrary[Index].sample.value

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[BeforeYouContinueView].apply(lrn, mode, itemIndex, packageIndex)(fakeRequest, messages)

  override val prefix: String = "item.packages.index.beforeYouContinue"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item $itemIndex - Packages")

  behave like pageWithHeading()

  behave like pageWithContent("p", "You entered 0 for the number of packages. This means you must add another item with:")

  behave like pageWithList("govuk-list--bullet", s"the same shipping mark as item $itemIndex", "at least 1 package")

  behave like pageWithContent("p", "If not, the office of departure will reject this declaration.")

  behave like pageWithSubmitButton("Continue")
}
