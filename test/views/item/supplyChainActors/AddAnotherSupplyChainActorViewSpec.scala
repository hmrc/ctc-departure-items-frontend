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

package views.item.supplyChainActors

import forms.AddAnotherFormProvider
import play.twirl.api.HtmlFormat
import views.behaviours.ListWithActionsViewBehaviours
import views.html.item.supplyChainActors.AddAnotherSupplyChainActorView

class AddAnotherSupplyChainActorViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxSupplyChainActors

  private def formProvider(viewModel: AddAnotherSupplyChainActorViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMore)

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[AddAnotherSupplyChainActorView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "item.supplyChainActors.addAnotherSupplyChainActor"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
