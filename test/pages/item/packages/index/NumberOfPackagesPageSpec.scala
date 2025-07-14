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

package pages.item.packages.index

import pages.behaviours.PageBehaviours

class NumberOfPackagesPageSpec extends PageBehaviours {

  "NumberOfPackagesPage" - {

    beRetrievable[Int](NumberOfPackagesPage(itemIndex, packageIndex))

    beSettable[Int](NumberOfPackagesPage(itemIndex, packageIndex))

    beRemovable[Int](NumberOfPackagesPage(itemIndex, packageIndex))

    "cleanup" - {
      "when value is more than 0" - {
        "must clean up BeforeYouContinuePage" in {
          forAll(positiveInts) {
            numberOfPackages =>
              val userAnswers = emptyUserAnswers.setValue(BeforeYouContinuePage(itemIndex, packageIndex), true)

              val result = userAnswers.setValue(NumberOfPackagesPage(itemIndex, packageIndex), numberOfPackages)

              result.get(BeforeYouContinuePage(itemIndex, packageIndex)) must not be defined
          }
        }
      }

      "when value is 0" - {
        "must not clean up BeforeYouContinuePage" in {
          val userAnswers = emptyUserAnswers.setValue(BeforeYouContinuePage(itemIndex, packageIndex), true)

          val result = userAnswers.setValue(NumberOfPackagesPage(itemIndex, packageIndex), 0)

          result.get(BeforeYouContinuePage(itemIndex, packageIndex)) mustBe defined
        }
      }
    }
  }
}
