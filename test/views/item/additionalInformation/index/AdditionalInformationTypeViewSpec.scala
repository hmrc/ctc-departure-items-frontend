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

import forms.SelectableFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.{NormalMode, SelectableList}
import models.reference.AdditionalInformation
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.item.additionalInformation.index.AdditionalInformationTypeView

class AdditionalInformationTypeViewSpec extends InputSelectViewBehaviours[AdditionalInformation] {

  override def form: Form[AdditionalInformation] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[AdditionalInformation]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalInformationTypeView].apply(form, lrn, values, NormalMode, itemIndex, additionalInformationIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[AdditionalInformation] = arbitraryAdditionalInformation

  override val prefix: String = "item.additionalInformation.index.additionalInformationType"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional information")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithSubmitButton("Save and continue")

  behave like pageWithHint("Enter the information name or code, like Export or 20300.")
}
