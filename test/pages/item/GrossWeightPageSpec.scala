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

package pages.item

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class GrossWeightPageSpec extends PageBehaviours {

  "GrossWeightPage" - {

    beRetrievable[BigDecimal](GrossWeightPage(itemIndex))

    beSettable[BigDecimal](GrossWeightPage(itemIndex))

    beRemovable[BigDecimal](GrossWeightPage(itemIndex))

    "clean up" - {
      "when value is greater than 0" - {
        "must clean up net weight page" in {
          forAll(arbitrary[BigDecimal]) {
            value =>
              forAll(arbitrary[BigDecimal].retryUntil(_ != value), arbitrary[BigDecimal]) {
                (differentValue, netWeight) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(GrossWeightPage(itemIndex), value)
                    .setValue(NetWeightPage(itemIndex), netWeight)

                  val result = userAnswers.setValue(GrossWeightPage(itemIndex), differentValue)

                  result.get(NetWeightPage(itemIndex)) mustNot be(defined)
              }

          }
        }

        "must clean up GrossWeightBeforeYouContinuePage" in {
          val userAnswers = emptyUserAnswers.setValue(GrossWeightBeforeYouContinuePage(itemIndex), true)

          val result = userAnswers.setValue(GrossWeightPage(itemIndex), 1)

          result.get(GrossWeightBeforeYouContinuePage(itemIndex)) mustNot be(defined)
        }
      }

      "when value has not changed" - {
        "must not clean up net weight page" in {
          forAll(arbitrary[BigDecimal]) {
            value =>
              forAll(arbitrary[BigDecimal]) {
                netWeight =>
                  val userAnswers = emptyUserAnswers
                    .setValue(GrossWeightPage(itemIndex), value)
                    .setValue(NetWeightPage(itemIndex), netWeight)

                  val result = userAnswers.setValue(GrossWeightPage(itemIndex), value)

                  result.get(NetWeightPage(itemIndex)) mustBe defined
              }

          }
        }
      }

      "when value is 0" - {
        "must not clean up GrossWeightBeforeYouContinuePage" in {
          val userAnswers = emptyUserAnswers.setValue(GrossWeightBeforeYouContinuePage(itemIndex), true)

          val result = userAnswers.setValue(GrossWeightPage(itemIndex), 0)

          result.get(GrossWeightBeforeYouContinuePage(itemIndex)) mustBe defined
        }
      }
    }
  }
}
