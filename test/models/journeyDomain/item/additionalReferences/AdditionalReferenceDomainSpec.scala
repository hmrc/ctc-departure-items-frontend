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

package models.journeyDomain.item.additionalReferences

import base.SpecBase
import generators.Generators
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.AdditionalReference
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.additionalReference.index._

class AdditionalReferenceDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Additional Reference Domain" - {

    "can be read from user answers" - {

      "when all questions answered" in {
        forAll(arbitrary[AdditionalReference], nonEmptyString) {
          (`type`, number) =>
            val userAnswers = emptyUserAnswers
              .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), `type`)
              .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)
              .setValue(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex), number)

            val expectedResult = AdditionalReferenceDomain(
              `type` = `type`,
              number = Some(number)
            )(itemIndex, additionalReferenceIndex)

            val result: EitherType[AdditionalReferenceDomain] = UserAnswersReader[AdditionalReferenceDomain](
              AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
            ).run(userAnswers)

            result.value mustBe expectedResult
        }
      }
    }

    "can not be read from user answers" - {

      "when reference type unanswered" in {
        val result: EitherType[AdditionalReferenceDomain] = UserAnswersReader[AdditionalReferenceDomain](
          AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
        ).run(emptyUserAnswers)

        result.left.value.page mustBe AdditionalReferencePage(itemIndex, additionalReferenceIndex)
      }

      "when C651 or C658 reference" - {
        "and additional reference number unanswered" in {
          forAll(arbitrary[AdditionalReference](arbitraryC651OrC658AdditionalReference)) {
            `type` =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), `type`)

              val result: EitherType[AdditionalReferenceDomain] = UserAnswersReader[AdditionalReferenceDomain](
                AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)
          }
        }
      }

      "when not C651 or C658 reference" - {
        "and add additional reference number yes/no unanswered" in {
          forAll(arbitrary[AdditionalReference]) {
            `type` =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), `type`)

              val result: EitherType[AdditionalReferenceDomain] = UserAnswersReader[AdditionalReferenceDomain](
                AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex)
          }
        }

        "and additional reference number unanswered" in {
          forAll(arbitrary[AdditionalReference]) {
            `type` =>
              val userAnswers = emptyUserAnswers
                .setValue(AdditionalReferencePage(itemIndex, additionalReferenceIndex), `type`)
                .setValue(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex), true)

              val result: EitherType[AdditionalReferenceDomain] = UserAnswersReader[AdditionalReferenceDomain](
                AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex)
              ).run(userAnswers)

              result.left.value.page mustBe AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)
          }
        }
      }
    }
  }

}
