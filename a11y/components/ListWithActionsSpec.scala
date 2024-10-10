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
import viewmodels.ListItem
import views.html.components.ListWithActions
import views.html.templates.MainTemplate

class ListWithActionsSpec extends A11ySpecBase {

  "the 'list with actions' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[ListWithActions]

    val title = nonEmptyString.sample.value

    "pass accessibility checks" when {
      "ordinary list items" in {
        val listItems = listWithMaxLength[ListItem]().sample.value

        val content = template.apply(title = title, lrn = lrn) {
          component.apply(listItems).withHeading(title)
        }

        content.toString() must passAccessibilityChecks
      }

      "list items have no actions" in {
        val listItems = listWithMaxLength[ListItem]().sample.value
          .map(_.copy(changeUrl = None, removeUrl = None))

        val content = template.apply(title = title, lrn = lrn) {
          component.apply(listItems).withHeading(title)
        }

        content.toString() must passAccessibilityChecks
      }

      "empty list items" in {
        val content = template.apply(title = title, lrn = lrn) {
          component.apply(Nil).withHeading(title)
        }

        content.toString() must passAccessibilityChecks
      }
    }
  }
}
