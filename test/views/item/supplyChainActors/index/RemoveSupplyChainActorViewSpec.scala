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

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.supplyChainActors.index.RemoveSupplyChainActorView

class RemoveSupplyChainActorViewSpec extends YesNoViewBehaviours {

  private val insetText = "test"

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[RemoveSupplyChainActorView].apply(form, lrn, NormalMode, itemIndex, actorIndex, insetText)(fakeRequest, messages)

  override val prefix: String = "item.supplyChainActors.index.removeSupplyChainActor"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Supply chain actor")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithInsetText(insetText)

  behave like pageWithSubmitButton("Save and continue")
}
