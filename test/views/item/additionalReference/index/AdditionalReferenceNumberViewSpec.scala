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

package views.item.additionalReference.index

import forms.Constants.maxAdditionalReferenceNumLength
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CharacterCountViewBehaviours
import views.html.item.additionalReference.index.AdditionalReferenceNumberView

class AdditionalReferenceNumberViewSpec extends CharacterCountViewBehaviours {

  override val prefix: String = "item.additionalReference.index.additionalReferenceNumber"

  override def form: Form[String] = new AdditionalReferenceNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalReferenceNumberView].apply(form, lrn, NormalMode, itemIndex, additionalReferenceIndex)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional reference")

  behave like pageWithHeading()

  behave like pageWithHint(s"You can enter up to $maxAdditionalReferenceNumLength characters")

  behave like pageWithCharacterCount(maxAdditionalReferenceNumLength)

  behave like pageWithSubmitButton("Save and continue")
}
