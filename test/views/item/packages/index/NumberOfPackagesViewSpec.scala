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

import base.SpecBase
import forms.IntFormProvider
import models.NormalMode
import models.reference.PackageType
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.item.packages.index.NumberOfPackagesView

class NumberOfPackagesViewSpec extends SpecBase with InputTextViewBehaviours[Int] {

  private val packageType = Arbitrary.arbitrary[PackageType].sample.get

  override def form: Form[Int] = new IntFormProvider()(prefix, 10, 0, Seq(packageType.toString))

  override def applyView(form: Form[Int]): HtmlFormat.Appendable =
    injector.instanceOf[NumberOfPackagesView].apply(form, lrn, NormalMode, itemIndex, packageIndex, packageType.toString)(fakeRequest, messages)

  private val maxInt = phaseConfig.maxNumberOfPackages

  implicit override val arbitraryT: Arbitrary[Int] = Arbitrary(maxInt)

  override val prefix: String = "item.packages.index.numberOfPackages"

  behave like pageWithTitle(packageType.toString)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Packages")

  behave like pageWithHeading(packageType.toString)

  behave like pageWithoutHint()

  behave like pageWithInsetText(packageType.toString)

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
