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

package views.item.additionalReference.index

import base.SpecBase
import forms.item.additionalReference.AdditionalReferenceNumberFormProvider
import generators.Generators
import models.NormalMode
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.item.additionalReference.AdditionalReferenceNumberViewModel
import views.behaviours.CharacterCountViewBehaviours
import views.html.item.additionalReference.index.AdditionalReferenceNumberView

class AdditionalReferenceNumberViewSpec extends SpecBase with CharacterCountViewBehaviours with Generators {

  override val prefix: String = "item.additionalReference.index.additionalReferenceNumber"

  private val viewModel = arbitrary[AdditionalReferenceNumberViewModel].sample.value

  private val formProvider = new AdditionalReferenceNumberFormProvider()(phaseConfig)

  override def form: Form[String] = formProvider(prefix, viewModel.otherAdditionalReferenceNumbers, isDocumentInCL234 = false, phaseConfig.phase)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector
      .instanceOf[AdditionalReferenceNumberView]
      .apply(form, lrn, NormalMode, itemIndex, additionalReferenceIndex, viewModel.isReferenceNumberRequired)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Additional reference")

  behave like pageWithHeading()

  behave like pageWithHint(s"You can enter up to ${phaseConfig.maxAdditionalReferenceNumLength} characters")

  behave like pageWithCharacterCount(phaseConfig.maxAdditionalReferenceNumLength)

  behave like pageWithSubmitButton("Save and continue")

  private val content = "You need to enter a reference number as you have already added this type of additional reference."

  "when reference number required" - {
    "must render paragraph" - {
      val view = injector
        .instanceOf[AdditionalReferenceNumberView]
        .apply(form, lrn, NormalMode, itemIndex, additionalReferenceIndex, isRequired = true)(fakeRequest, messages)

      val doc = parseView(view)

      behave like pageWithContent(
        doc = doc,
        tag = "p",
        expectedText = content
      )
    }
  }

  "when reference number not required" - {
    "must not render paragraph" - {
      val view = injector
        .instanceOf[AdditionalReferenceNumberView]
        .apply(form, lrn, NormalMode, itemIndex, additionalReferenceIndex, isRequired = false)(fakeRequest, messages)

      val doc = parseView(view)

      behave like pageWithoutContent(
        doc = doc,
        tag = "p",
        expectedText = content
      )
    }
  }
}
