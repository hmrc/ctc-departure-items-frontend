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

package views.item.packages.index

import generators.Generators
import models.NormalMode
import models.reference.PackageType
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.packages.index.RemovePackageView

class RemovePackageViewSpec extends YesNoViewBehaviours with Generators {

  private val packageType = arbitrary[PackageType].sample.value
  private val insetText   = "test"

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[RemovePackageView]
      .apply(form, lrn, NormalMode, itemIndex, packageIndex, Some(insetText))(fakeRequest, messages)

  override val prefix: String = "item.packages.index.removePackage"

  behave like pageWithTitle(packageType)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Packages")

  behave like pageWithHeading(packageType)

  behave like pageWithRadioItems(args = Seq(packageType))

  behave like pageWithSubmitButton("Save and continue")

  behave like pageWithInsetText(insetText)
}
