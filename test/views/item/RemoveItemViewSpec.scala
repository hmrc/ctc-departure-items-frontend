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

package views.item

import generators.Generators
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.RemoveItemView

class RemoveItemViewSpec extends YesNoViewBehaviours with Generators {
  private val itemDescription = nonEmptyString.sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[RemoveItemView]
      .apply(form, lrn, itemIndex, itemDescription)(fakeRequest, messages)

  override val prefix: String = "item.removeItem"

  behave like pageWithTitle(itemIndex.display)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display}")

  behave like pageWithHeading(itemIndex.display)

  behave like pageWithInsetText(itemDescription)

  behave like pageWithRadioItems(args = Seq(itemIndex.display))

  behave like pageWithSubmitButton("Save and continue")
}
