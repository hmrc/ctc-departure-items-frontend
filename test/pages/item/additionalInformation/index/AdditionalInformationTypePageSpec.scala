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

package pages.item.additionalInformation.index

import models.reference.AdditionalInformation
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AdditionalInformationTypePageSpec extends PageBehaviours {

  "AdditionalInformationTypePage" - {

    beRetrievable[AdditionalInformation](AdditionalInformationTypePage(itemIndex, additionalInformationIndex))

    beSettable[AdditionalInformation](AdditionalInformationTypePage(itemIndex, additionalInformationIndex))

    beRemovable[AdditionalInformation](AdditionalInformationTypePage(itemIndex, additionalInformationIndex))

    "when value changes" - {
      "must clean up additional information page" in {
        forAll(arbitrary[AdditionalInformation]) {
          value =>
            forAll(arbitrary[AdditionalInformation].retryUntil(_ != value), nonEmptyString) {
              (differentValue, additionalInformation) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), value)
                  .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), additionalInformation)

                val result = userAnswers.setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), differentValue)

                result.get(AdditionalInformationPage(itemIndex, additionalInformationIndex)) mustNot be(defined)
            }
        }
      }
    }

    "when value has not changed" - {
      "must not clean up additional information page" in {
        forAll(arbitrary[AdditionalInformation], nonEmptyString) {
          (value, additionalInformation) =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), value)
              .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), additionalInformation)

            val result = userAnswers.setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), value)

            result.get(AdditionalInformationPage(itemIndex, additionalInformationIndex)) must be(defined)
        }
      }
    }
  }
}
