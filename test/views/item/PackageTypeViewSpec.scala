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

import forms.PackageTypeFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.PackageType
import models.PackageTypeList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.item.PackageTypeView

class PackageTypeViewSpec extends InputSelectViewBehaviours[PackageType] {

  private val arg = itemIndex.display.toString

  override def form: Form[PackageType] = new PackageTypeFormProvider()(prefix, PackageTypeList(values), Seq(arg))

  override def applyView(form: Form[PackageType]): HtmlFormat.Appendable =
    injector.instanceOf[PackageTypeView].apply(form, lrn, values, NormalMode, itemIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[PackageType] = arbitraryPackageType

  override val prefix: String = "item.packageType"

  behave like pageWithTitle(arg)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Items - Packages")

  behave like pageWithHeading(arg)

  behave like pageWithSelect()

  behave like pageWithHint("Enter the package or code, like cylinder or CY.")

  behave like pageWithSubmitButton("Save and continue")
}
