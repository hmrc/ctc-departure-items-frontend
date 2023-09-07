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
import config.PhaseConfig
import generators.Generators
import models._
import org.mockito.Mockito.when
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DangerousGoodsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Dangerous Goods Navigator" - {
    val mockTransitionPhaseConfig = mock[PhaseConfig]
    when(mockTransitionPhaseConfig.phase).thenReturn(Phase.Transition)

    val mockPostTransitionPhaseConfig = mock[PhaseConfig]
    when(mockPostTransitionPhaseConfig.phase).thenReturn(Phase.PostTransition)

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "and when in transition" - {
          val transitionNavigatorProvider = new DangerousGoodsNavigatorProviderImpl()(frontendAppConfig, mockTransitionPhaseConfig)
          val transitionNavigator         = transitionNavigatorProvider.apply(mode, itemIndex, dangerousGoodsIndex)
          "must redirect to gross weight page" in {
            forAll(arbitraryDangerousGoodsAnswers(emptyUserAnswers, itemIndex, dangerousGoodsIndex)) {
              answers =>
                transitionNavigator
                  .nextPage(answers)
                  .mustBe(controllers.item.routes.GrossWeightController.onPageLoad(answers.lrn, mode, itemIndex))
            }
          }
        }
        "and when in post-transition" - {
          val postTransitionNavigatorProvider = new DangerousGoodsNavigatorProviderImpl()(frontendAppConfig, mockPostTransitionPhaseConfig)
          val postTransitionNavigator         = postTransitionNavigatorProvider.apply(mode, itemIndex, dangerousGoodsIndex)
          "must redirect to add another dangerous goods page" in {
            forAll(arbitraryDangerousGoodsAnswers(emptyUserAnswers, itemIndex, dangerousGoodsIndex)) {
              answers =>
                postTransitionNavigator
                  .nextPage(answers)
                  .mustBe(controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(answers.lrn, mode, itemIndex))
            }
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new DangerousGoodsNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, itemIndex, dangerousGoodsIndex)

      "when answers complete" - {
        "must redirect to item answers" ignore {
          forAll(arbitraryItemAnswers(emptyUserAnswers, itemIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(???)
          }
        }
      }
    }
  }
}
