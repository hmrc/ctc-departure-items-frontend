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

package views.item.documents

import controllers.item.documents.routes
import forms.AddAnotherFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.ListItem
import viewmodels.item.documents.AddAnotherDocumentViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.item.documents.AddAnotherDocumentView

class AddAnotherDocumentViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxDocuments

  private def formProvider(viewModel: AddAnotherDocumentViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMore)

  private val viewModel            = arbitrary[AddAnotherDocumentViewModel].sample.value
  private val notMaxedOutViewModel = viewModel.copy(listItems = listItems, consignmentLevelDocumentsListItems = Nil)
  private val maxedOutViewModel    = viewModel.copy(listItems = maxedOutListItems, consignmentLevelDocumentsListItems = Nil)

  override def form: Form[Boolean] = formProvider(notMaxedOutViewModel)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherDocumentView]
      .apply(form, lrn, notMaxedOutViewModel, itemIndex)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherDocumentView]
      .apply(formProvider(maxedOutViewModel), lrn, maxedOutViewModel, itemIndex)(fakeRequest, messages, frontendAppConfig)

  override val prefix: String = "item.documents.addAnotherDocument"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Documents")

  behave like pageWithMoreItemsAllowed(notMaxedOutViewModel.count)()

  behave like pageWithItemsMaxedOut(maxedOutViewModel.count)

  behave like pageWithSubmitButton("Save and continue")

  "when no documents available to attach to item" - {
    val viewModel = notMaxedOutViewModel.copy(documents = Nil)
    val view = injector
      .instanceOf[AddAnotherDocumentView]
      .apply(formProvider(viewModel), lrn, viewModel, itemIndex)(fakeRequest, messages, frontendAppConfig)
    val doc = parseView(view)

    behave like pageWithoutRadioItems(doc)

    behave like pageWithContent(doc = doc, tag = "p", expectedText = "You can only attach another document if you have added it in your Documents section.")

    behave like pageWithLink(
      doc = doc,
      id = "documents",
      expectedText = "Go to your Documents section to add another document",
      expectedHref = routes.AddAnotherDocumentController.redirectToDocuments(lrn, itemIndex).url
    )
  }

  "when there are consignment level documents" - {
    val listItems = listWithMaxLength[ListItem]().sample.value

    val view = injector
      .instanceOf[AddAnotherDocumentView]
      .apply(form, lrn, viewModel.copy(listItems = Nil, consignmentLevelDocumentsListItems = listItems), itemIndex)(fakeRequest, messages, frontendAppConfig)

    val doc = parseView(view)

    behave like pageWithListWithActions(doc, listItems)

    behave like pageWithContent(doc, "p", "You attached the documents above in your Documents section.")
  }

  "when there are no consignment level documents" - {
    val view = injector
      .instanceOf[AddAnotherDocumentView]
      .apply(form, lrn, viewModel.copy(consignmentLevelDocumentsListItems = Nil), itemIndex)(fakeRequest, messages, frontendAppConfig)

    val doc = parseView(view)

    behave like pageWithoutContent(doc, "p", "You attached the documents above in your Documents section.")
  }
}
