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

import forms.NetWeightFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.item.NetWeightView

class NetWeightViewSpec extends InputTextViewBehaviours[BigDecimal] {

  private val decimalPlace: Int       = positiveInts.sample.value
  private val characterCount: Int     = positiveInts.sample.value
  override def form: Form[BigDecimal] = new NetWeightFormProvider()(prefix, grossWeight, decimalPlace, characterCount)

  override def applyView(form: Form[BigDecimal]): HtmlFormat.Appendable =
    injector.instanceOf[NetWeightView].apply(form, lrn, NormalMode, itemIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[BigDecimal] = Arbitrary(positiveBigDecimals)

  private val grossWeight = arbitraryT.arbitrary.sample.value

  override val prefix: String = "item.netWeight"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Measurement")

  behave like pageWithHeading()

  behave like pageWithHint(s"Enter the weight in kilograms (kg), up to ${phaseConfig.decimalPlaces} decimal places.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
