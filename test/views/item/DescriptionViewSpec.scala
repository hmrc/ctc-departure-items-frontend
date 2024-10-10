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

import base.SpecBase
import forms.item.DescriptionFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CharacterCountViewBehaviours
import views.html.item.DescriptionView

class DescriptionViewSpec extends SpecBase with CharacterCountViewBehaviours {

  private val formProvider = new DescriptionFormProvider()(phaseConfig)

  override def form: Form[String] = formProvider(prefix, itemIndex.display)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[DescriptionView].apply(form, lrn, NormalMode, itemIndex)(fakeRequest, messages)

  override val prefix: String = "item.description"

  behave like pageWithTitle(args = itemIndex.display)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display}")

  behave like pageWithHeading(args = itemIndex.display)

  behave like pageWithContent("p", "This should be clear and detailed enough for anyone involved in the transit movement to understand its contents.")

  behave like pageWithCharacterCount(phaseConfig.maxItemDescriptionLength)

  behave like pageWithSubmitButton("Save and continue")
}
