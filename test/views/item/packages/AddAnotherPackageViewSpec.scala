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

package views.item.packages

import forms.AddAnotherFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.item.packages.AddAnotherPackageViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.item.packages.AddAnotherPackageView

class AddAnotherPackageViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxPackages

  private def formProvider(viewModel: AddAnotherPackageViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMore)

  private val viewModel            = arbitrary[AddAnotherPackageViewModel].sample.value
  private val notMaxedOutViewModel = viewModel.copy(listItems = listItems)
  private val maxedOutViewModel    = viewModel.copy(listItems = maxedOutListItems)

  override def form: Form[Boolean] = formProvider(notMaxedOutViewModel)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherPackageView]
      .apply(form, lrn, notMaxedOutViewModel, itemIndex)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherPackageView]
      .apply(formProvider(maxedOutViewModel), lrn, maxedOutViewModel, itemIndex)(fakeRequest, messages, frontendAppConfig)

  override val prefix: String = "item.packages.addAnotherPackage"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Packages")

  behave like pageWithMoreItemsAllowed(notMaxedOutViewModel.count)()

  behave like pageWithItemsMaxedOut(maxedOutViewModel.count)

  behave like pageWithSubmitButton("Save and continue")
}
