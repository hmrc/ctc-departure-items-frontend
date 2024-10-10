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

package views.item.packages.index

import base.SpecBase
import forms.item.packages.ShippingMarkFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CharacterCountViewBehaviours
import views.html.item.packages.index.ShippingMarkView

class ShippingMarkViewSpec extends SpecBase with CharacterCountViewBehaviours {

  override val prefix: String = "item.packages.index.shippingMark"

  private val formProvider = new ShippingMarkFormProvider()(phaseConfig)

  override def form: Form[String] = formProvider(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[ShippingMarkView].apply(form, lrn, NormalMode, itemIndex, packageIndex)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Packages")

  behave like pageWithHeading()

  behave like pageWithCharacterCount(phaseConfig.maxShippingMarkLength)

  behave like pageWithSubmitButton("Save and continue")
}
