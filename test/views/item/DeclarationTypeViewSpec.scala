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

import config.TestConstants.{declarationTypeItemValues, declarationTypeValues}
import forms.EnumerableFormProvider
import models.{DeclarationTypeItemLevel, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.item.DeclarationTypeView

class DeclarationTypeViewSpec extends RadioViewBehaviours[DeclarationTypeItemLevel] {

  override def form: Form[DeclarationTypeItemLevel] = new EnumerableFormProvider()(prefix)(declarationTypeValues)

  override def applyView(form: Form[DeclarationTypeItemLevel]): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationTypeView].apply(form, lrn, values, NormalMode, itemIndex)(fakeRequest, messages)

  override val prefix: String = "item.declarationType"

  override def radioItems(fieldId: String, checkedValue: Option[DeclarationTypeItemLevel] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[DeclarationTypeItemLevel] = declarationTypeItemValues

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display}")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
