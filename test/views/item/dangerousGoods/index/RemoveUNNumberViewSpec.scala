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

package views.item.dangerousGoods.index

import generators.Generators
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.dangerousGoods.index.RemoveUNNumberView

class RemoveUNNumberViewSpec extends YesNoViewBehaviours with Generators {

  private val UNNumber = nonEmptyString.sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[RemoveUNNumberView].apply(form, lrn, NormalMode, itemIndex, dangerousGoodsIndex, UNNumber)(fakeRequest, messages)

  override val prefix: String = "item.dangerousGoods.index.removeUNNumber"

  behave like pageWithTitle(UNNumber)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption(s"Item ${itemIndex.display} - Dangerous goods")

  behave like pageWithHeading(UNNumber)

  behave like pageWithRadioItems(args = Seq(UNNumber))

  behave like pageWithSubmitButton("Save and continue")

  behave like pageWithInsetText(UNNumber)
}
