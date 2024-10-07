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

import forms.BigDecimalFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.item.SupplementaryUnitsView

class SupplementaryUnitsViewSpec extends InputTextViewBehaviours[BigDecimal] {

  override val prefix: String = "item.supplementaryUnits"

  private val decimalPlace: Int       = positiveInts.sample.value
  private val characterCount: Int     = positiveInts.sample.value
  override def form: Form[BigDecimal] = app.injector.instanceOf[BigDecimalFormProvider].apply(prefix, decimalPlace, characterCount)

  override def applyView(form: Form[BigDecimal]): HtmlFormat.Appendable =
    injector.instanceOf[SupplementaryUnitsView].apply(form, lrn, NormalMode, itemIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[BigDecimal] = Arbitrary(positiveBigDecimals)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Measurement")

  behave like pageWithHeading()

  behave like pageWithHint("This number can include up to 6 decimal places.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
