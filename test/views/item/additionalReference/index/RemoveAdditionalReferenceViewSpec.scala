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

package views.item.additionalReference.index

import generators.Generators
import models.NormalMode
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.additionalReference.index.RemoveAdditionalReferenceView

class RemoveAdditionalReferenceViewSpec extends YesNoViewBehaviours with Generators {

  private val additionalReference = arbitrary[AdditionalReferenceDomain].sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[RemoveAdditionalReferenceView]
      .apply(form, lrn, NormalMode, itemIndex, additionalReferenceIndex, additionalReference.toString)(fakeRequest, messages)

  override val prefix: String = "item.additionalReference.index.removeAdditionalReference"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional reference")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithInsetText(additionalReference.toString)

  behave like pageWithSubmitButton("Save and continue")
}
