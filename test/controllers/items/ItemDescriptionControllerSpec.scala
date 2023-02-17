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

package controllers.items

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.items.ItemDescriptionFormProvider
import models.NormalMode
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.items.ItemDescriptionView

class ItemDescriptionControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val itemDescriptionRoute = routes.ItemDescriptionController.onPageLoad(lrn, NormalMode, itemIndex).url
  private val formProvider              = new ItemDescriptionFormProvider()
  private val form                      = formProvider("items.itemDescription")
  private val mode                      = NormalMode

  "ItemDescription Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, itemDescriptionRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ItemDescriptionView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex)(request, messages).toString
    }
  }
}
