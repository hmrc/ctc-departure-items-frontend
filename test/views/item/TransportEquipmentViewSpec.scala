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

import forms.SelectableFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.{NormalMode, SelectableList, TransportEquipment}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.item.TransportEquipmentView

class TransportEquipmentViewSpec extends InputSelectViewBehaviours[TransportEquipment] {

  override def form: Form[TransportEquipment] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[TransportEquipment]): HtmlFormat.Appendable =
    injector.instanceOf[TransportEquipmentView].apply(form, lrn, values, NormalMode, itemIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[TransportEquipment] = arbitraryTransportEquipment

  override val prefix: String = "item.transportEquipment"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display}")

  behave like pageWithHeading()

  behave like pageWithContent("p", "Transport equipment can be anything used to transport goods. For example, a swap body, articulated lorry or trailer.")

  behave like pageWithoutHint()

  behave like pageWithSelect()

  behave like pageWithSubmitButton("Save and continue")
}
