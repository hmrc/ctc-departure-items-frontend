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

package navigation

import base.SpecBase
import generators.Generators
import models.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DangerousGoodsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Dangerous Goods Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        val navigatorProvider = new DangerousGoodsNavigatorProviderImpl()(frontendAppConfig)
        val navigator         = navigatorProvider.apply(mode, itemIndex, dangerousGoodsIndex)
        "must redirect to add another dangerous goods page" in {
          forAll(arbitraryDangerousGoodsAnswers(emptyUserAnswers, itemIndex, dangerousGoodsIndex)) {
            answers =>
              navigator
                .nextPage(answers, None)
                .mustBe(controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(answers.lrn, mode, itemIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new DangerousGoodsNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, itemIndex, dangerousGoodsIndex)

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
