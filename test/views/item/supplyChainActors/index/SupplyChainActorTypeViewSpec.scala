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

package views.item.supplyChainActors.index

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.SupplyChainActorType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.item.supplyChainActors.index.SupplyChainActorTypeView

class SupplyChainActorTypeViewSpec extends RadioViewBehaviours[SupplyChainActorType] {

  override def form: Form[SupplyChainActorType] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[SupplyChainActorType]): HtmlFormat.Appendable =
    injector.instanceOf[SupplyChainActorTypeView].apply(form, lrn, values, NormalMode, itemIndex, actorIndex)(fakeRequest, messages)

  override val prefix: String = "item.supplyChainActors.index.supplyChainActorType"

  override def radioItems(fieldId: String, checkedValue: Option[SupplyChainActorType] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[SupplyChainActorType] = Seq(
    SupplyChainActorType("FW", "Freight Forwarder"),
    SupplyChainActorType("WH", "Warehouse Keeper")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Supply chain actor")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
