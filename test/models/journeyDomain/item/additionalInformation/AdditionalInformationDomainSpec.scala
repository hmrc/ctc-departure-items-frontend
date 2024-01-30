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

package models.journeyDomain.item.additionalInformation

import base.SpecBase
import generators.Generators
import models.reference.AdditionalInformation
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.additionalInformation.index._

class AdditionalInformationDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Additional Information Domain" - {

    "can be read from user answers" - {

      "when all questions answered" in {
        forAll(arbitrary[AdditionalInformation], nonEmptyString) {
          (`type`, value) =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), `type`)
              .setValue(AdditionalInformationPage(itemIndex, additionalInformationIndex), value)

            val expectedResult = AdditionalInformationDomain(
              `type` = `type`,
              value = value
            )(itemIndex, additionalInformationIndex)

            val result = AdditionalInformationDomain.userAnswersReader(itemIndex, additionalInformationIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              AdditionalInformationTypePage(itemIndex, additionalInformationIndex),
              AdditionalInformationPage(itemIndex, additionalInformationIndex)
            )
        }
      }
    }

    "can not be read from user answers" - {

      "when additional information type unanswered" in {
        val result = AdditionalInformationDomain.userAnswersReader(itemIndex, additionalInformationIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe AdditionalInformationTypePage(itemIndex, additionalInformationIndex)
        result.left.value.pages mustBe Seq(
          AdditionalInformationTypePage(itemIndex, additionalInformationIndex)
        )
      }

      "when additional information value unanswered" in {
        forAll(arbitrary[AdditionalInformation]) {
          `type` =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalInformationTypePage(itemIndex, additionalInformationIndex), `type`)

            val result = AdditionalInformationDomain.userAnswersReader(itemIndex, additionalInformationIndex).apply(Nil).run(userAnswers)

            result.left.value.page mustBe AdditionalInformationPage(itemIndex, additionalInformationIndex)
            result.left.value.pages mustBe Seq(
              AdditionalInformationTypePage(itemIndex, additionalInformationIndex),
              AdditionalInformationPage(itemIndex, additionalInformationIndex)
            )
        }
      }
    }
  }
}
