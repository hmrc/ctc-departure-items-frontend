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

package utils.cyaHelpers

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.item.routes
import generators.Generators
import models.Index
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.*
import viewmodels.ListItem

class ItemsAnswersHelperSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "ItemsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of items" in {
          val userAnswers = emptyUserAnswers

          val helper = new ItemsAnswersHelper(userAnswers)
          helper.listItems mustEqual Nil

        }
      }

      "when user answers populated with complete items" in {
        val description1 = nonEmptyString.sample.value
        val description2 = nonEmptyString.sample.value

        val initialAnswers = emptyUserAnswers
          .setValue(DescriptionPage(Index(0)), description1)
          .setValue(DescriptionPage(Index(1)), description2)

        forAll(arbitraryItemsAnswers(initialAnswers)) {
          userAnswers =>
            val helper = new ItemsAnswersHelper(userAnswers)
            helper.listItems mustEqual Seq(
              Right(
                ListItem(
                  name = s"Item 1 - $description1",
                  changeUrl = controllers.item.routes.CheckYourAnswersController.onPageLoad(lrn, Index(0)).url,
                  removeUrl = Some(routes.RemoveItemController.onPageLoad(lrn, Index(0)).url)
                )
              ),
              Right(
                ListItem(
                  name = s"Item 2 - $description2",
                  changeUrl = controllers.item.routes.CheckYourAnswersController.onPageLoad(lrn, Index(1)).url,
                  removeUrl = Some(routes.RemoveItemController.onPageLoad(lrn, Index(1)).url)
                )
              )
            )

        }
      }
    }
  }

}
