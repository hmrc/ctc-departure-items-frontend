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

package views.item.documents.index

import forms.DocumentFormProvider
import models.{Document, DocumentList, NormalMode}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.item.documents.index.DocumentView

class DocumentViewSpec extends InputSelectViewBehaviours[Document] {

  override def form: Form[Document] = new DocumentFormProvider()(prefix, DocumentList(values))

  override def applyView(form: Form[Document]): HtmlFormat.Appendable =
    injector.instanceOf[DocumentView].apply(form, lrn, values, NormalMode, itemIndex, documentIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Document] = arbitraryDocument

  override val prefix: String = "item.documents.index.document"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Documents")

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithSelect()

  behave like pageWithSubmitButton("Save and continue")
}
