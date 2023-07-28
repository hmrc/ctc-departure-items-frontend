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

package views.item.consignee

import forms.DynamicAddressFormProvider
import generators.Generators
import models.{DynamicAddress, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DynamicAddressViewBehaviours
import views.html.item.consignee.AddressView

class AddressViewSpec extends DynamicAddressViewBehaviours with Generators {

  private val name = nonEmptyString.sample.value

  override def form: Form[DynamicAddress] = new DynamicAddressFormProvider()(prefix, isPostalCodeRequired, name)

  override def applyView(form: Form[DynamicAddress]): HtmlFormat.Appendable =
    injector.instanceOf[AddressView].apply(form, lrn, NormalMode, name, isPostalCodeRequired, itemIndex)(fakeRequest, messages)

  override val prefix: String = "item.consignee.address"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading(name)

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Consignee")

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Save and continue")
}
