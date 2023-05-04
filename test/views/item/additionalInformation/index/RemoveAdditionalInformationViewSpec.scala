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

package views.item.additionalInformation.index

import generators.Generators
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.additionalInformation.index.RemoveAdditionalInformationView

class RemoveAdditionalInformationViewSpec extends YesNoViewBehaviours with Generators {

  private val additionalInformationType = arbitraryAdditionalInformation.arbitrary.sample.get

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[RemoveAdditionalInformationView]
      .apply(form, lrn, NormalMode, itemIndex, additionalInformationIndex, additionalInformationType.toString)(fakeRequest, messages)

  override val prefix: String = "item.additionalInformation.index.removeAdditionalInformation"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional information")

  behave like pageWithHeading()

  behave like pageWithInsetText(additionalInformationType.toString)

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
