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

package views.item

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.BigDecimalFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary
import play.api.Application
import play.api.data.Form
import play.api.test.Helpers.running
import play.twirl.api.HtmlFormat
import viewmodels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.item.GrossWeightView

class GrossWeightViewSpec extends SpecBase with InputTextViewBehaviours[BigDecimal] with AppWithDefaultMockFixtures {

  override val prefix: String = "item.grossWeight"

  private def formProvider(app: Application): Form[BigDecimal] =
    app.injector.instanceOf[BigDecimalFormProvider].apply(prefix)

  override def form: Form[BigDecimal] = formProvider(app)

  override def applyView(form: Form[BigDecimal]): HtmlFormat.Appendable =
    applyView(app, form)

  private def applyView(app: Application): HtmlFormat.Appendable =
    applyView(app, formProvider(app))

  private def applyView(app: Application, form: Form[BigDecimal]): HtmlFormat.Appendable =
    app.injector.instanceOf[GrossWeightView].apply(form, lrn, NormalMode, itemIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[BigDecimal] = Arbitrary(positiveBigDecimals)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Measurement")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This is the combined weight of the item’s goods and packaging.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")

  "when during transition" - {
    val app = transitionApplicationBuilder().build()
    running(app) {
      val doc = parseView(applyView(app))
      behave like pageWithHint(doc, "Enter the weight in kilograms (kg), up to 3 decimal places.")
    }
  }

  "when post transition" - {
    val app = postTransitionApplicationBuilder().build()
    running(app) {
      val doc = parseView(applyView(app))
      behave like pageWithHint(doc, "Enter the weight in kilograms (kg), up to 6 decimal places.")
    }
  }
}
