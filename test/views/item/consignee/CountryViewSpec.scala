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

package views.item.consignee

import forms.SelectableFormProvider.CountryFormProvider
import models.reference.Country
import models.{NormalMode, SelectableList}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.item.consignee.CountryView

class CountryViewSpec extends InputSelectViewBehaviours[Country] {

  private val name = nonEmptyString.sample.value

  val formProvider                 = new CountryFormProvider()
  override val field: String       = formProvider.field
  override def form: Form[Country] = formProvider(prefix, SelectableList(values))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[CountryView].apply(form, lrn, values, NormalMode, itemIndex, name)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Country] = arbitraryCountry

  override val prefix: String = "item.consignee.country"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Consignee")

  behave like pageWithHeading(name)

  behave like pageWithSelect()

  behave like pageWithHint("Enter the country, like Italy or Spain.")

  behave like pageWithSubmitButton("Save and continue")
}
