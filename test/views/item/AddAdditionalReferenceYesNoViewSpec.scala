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

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.AddAdditionalReferenceYesNoView

class AddAdditionalReferenceYesNoViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddAdditionalReferenceYesNoView].apply(form, lrn, NormalMode, itemIndex)(fakeRequest, messages)

  override val prefix: String = "item.addAdditionalReferenceYesNo"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional reference")

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "This can be any other reference you want to declare, such as a certificate number, import licence or accompanying document."
  )

  behave like pageWithHint("Adding an additional reference is optional.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")

}
