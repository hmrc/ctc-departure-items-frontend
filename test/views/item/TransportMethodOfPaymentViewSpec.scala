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

import forms.EnumerableFormProvider
import generators.Generators
import models.NormalMode
import models.reference.TransportChargesMethodOfPayment
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.EnumerableViewBehaviours
import views.html.item.TransportMethodOfPaymentView

class TransportMethodOfPaymentViewSpec extends EnumerableViewBehaviours[TransportChargesMethodOfPayment] with Generators {
  private val mop1                                         = arbitrary[TransportChargesMethodOfPayment].sample.value
  private val mop2                                         = arbitrary[TransportChargesMethodOfPayment].sample.value
  private val mops                                         = Seq(mop1, mop2)
  override def form: Form[TransportChargesMethodOfPayment] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[TransportChargesMethodOfPayment]): HtmlFormat.Appendable =
    injector.instanceOf[TransportMethodOfPaymentView].apply(form, lrn, values, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "item.transportMethodOfPayment"

  override def radioItems(fieldId: String, checkedValue: Option[TransportChargesMethodOfPayment] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[TransportChargesMethodOfPayment] = mops

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
