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

package components

import a11ySpecBase.A11ySpecBase
import forms.NameFormProvider
import org.scalacheck.Gen
import views.html.components.InputTextArea
import views.html.templates.MainTemplate

class InputTextAreaSpec extends A11ySpecBase {

  "the 'input text area' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputTextArea]

    val prefix     = Gen.alphaNumStr.sample.value
    val title      = nonEmptyString.sample.value
    val label      = nonEmptyString.sample.value
    val caption    = Gen.option(nonEmptyString).sample.value
    val inputClass = Gen.option(Gen.alphaNumStr).sample.value
    val hint       = Gen.option(nonEmptyString).sample.value
    val rows       = positiveInts.sample.value
    val form       = new NameFormProvider()(prefix)

    val content = template.apply(title, lrn = lrn) {
      component.apply(form("value"), label, caption, inputClass, hint, rows)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
