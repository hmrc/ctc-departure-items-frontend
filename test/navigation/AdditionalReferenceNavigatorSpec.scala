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

package navigation

import base.SpecBase
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class AdditionalReferenceNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Additional Reference Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new AdditionalReferenceNavigatorProviderImpl()
      val navigator         = navigatorProvider.apply(mode, itemIndex, additionalReferenceIndex)

      "when answers complete" - {
        "must redirect to add another additional reference page" in {
          forAll(arbitraryAdditionalReferenceAnswers(emptyUserAnswers, itemIndex, additionalReferenceIndex)) {
            answers =>
              navigator
                .nextPage(answers, None)
                .mustBe(controllers.item.additionalReference.routes.AddAnotherAdditionalReferenceController.onPageLoad(answers.lrn, mode, itemIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new AdditionalReferenceNavigatorProviderImpl()
      val navigator         = navigatorProvider.apply(mode, itemIndex, additionalReferenceIndex)

      "when answers complete" - {
        "must redirect to item answers" in {
          forAll(arbitraryItemAnswers(emptyUserAnswers, itemIndex)) {
            answers =>
              navigator
                .nextPage(answers, None)
                .mustBe(controllers.item.routes.CheckYourAnswersController.onPageLoad(answers.lrn, itemIndex))
          }
        }
      }
    }
  }
}
