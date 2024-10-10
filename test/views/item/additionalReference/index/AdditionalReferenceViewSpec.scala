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

package views.item.additionalReference.index

import forms.SelectableFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.{NormalMode, SelectableList}
import models.reference.AdditionalReference
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.item.additionalReference.index.AdditionalReferenceView

class AdditionalReferenceViewSpec extends InputSelectViewBehaviours[AdditionalReference] {

  override def form: Form[AdditionalReference] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[AdditionalReference]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalReferenceView].apply(form, lrn, values, NormalMode, itemIndex, additionalReferenceIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[AdditionalReference] = arbitraryAdditionalReference

  override val prefix: String = "item.additionalReference.index.additionalReference"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional reference")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithSubmitButton("Save and continue")

  behave like pageWithHint("Enter the reference name or code, like Carrier (AEO certificate number) or Y028.")
}
