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

package views.items

import forms.EnumerableFormProvider
import models.NormalMode
import models.items.DeclarationType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.items.DeclarationTypeView

class DeclarationTypeViewSpec extends RadioViewBehaviours[DeclarationType] {

  override def form: Form[DeclarationType] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[DeclarationType]): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationTypeView].apply(form, lrn, DeclarationType.radioItems, NormalMode, itemIndex)(fakeRequest, messages)

  override val prefix: String = "items.declarationType"

  override def radioItems(fieldId: String, checkedValue: Option[DeclarationType] = None): Seq[RadioItem] =
    DeclarationType.radioItems(fieldId, checkedValue)

  override def values: Seq[DeclarationType] = DeclarationType.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
