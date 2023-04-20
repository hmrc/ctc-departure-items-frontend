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

import generators.Generators
import models.{Document, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.documents.index.RemoveDocumentView

class RemoveDocumentViewSpec extends YesNoViewBehaviours with Generators {

  private val document = arbitrary[Document].sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[RemoveDocumentView].apply(form, lrn, NormalMode, itemIndex, documentIndex, document)(fakeRequest, messages)

  override val prefix: String = "item.documents.index.removeDocument"

  behave like pageWithTitle(document)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Documents")

  behave like pageWithHeading(document)

  behave like pageWithRadioItems(args = Seq(document))

  behave like pageWithSubmitButton("Save and continue")
}
