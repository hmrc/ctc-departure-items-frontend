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

package models.journeyDomain.item

import base.SpecBase
import generators.Generators
import models.DeclarationType
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.external.TransitOperationDeclarationTypePage
import pages.item.{DeclarationTypePage, DescriptionPage}

class ItemDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val description = Gen.alphaNumStr.sample.value

  "Item Domain" - {

    "can be read from user answers" - {}

    "userAnswersReader" - {
      "can not be read from user answers" - {
        "when item description page is unanswered" in {
          val result: EitherType[ItemDomain] =
            UserAnswersReader[ItemDomain](
              ItemDomain.userAnswersReader(itemIndex)
            ).run(emptyUserAnswers)

          result.left.value.page mustBe DescriptionPage(itemIndex)
        }
      }
    }

    "declarationTypeReader" - {
      "can be read from user answers" - {
        "when declaration type is not T" in {
          forAll(arbitrary[DeclarationType](arbitraryNonTDeclarationType)) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, declarationType)

              val expectedResult = None

              val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
                ItemDomain.declarationTypeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "when declaration type is T" in {
          forAll(arbitrary[DeclarationType]) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, DeclarationType.T)
                .setValue(DeclarationTypePage(itemIndex), declarationType)

              val expectedResult = Some(declarationType)

              val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
                ItemDomain.declarationTypeReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "can not be read from user answers" - {
        "when transit operation declaration type is T" - {
          "and declaration type is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(TransitOperationDeclarationTypePage, DeclarationType.T)

            val result: EitherType[Option[DeclarationType]] = UserAnswersReader[Option[DeclarationType]](
              ItemDomain.declarationTypeReader(itemIndex)
            ).run(userAnswers)

            result.left.value.page mustBe DeclarationTypePage(itemIndex)
          }
        }
      }
    }

    "countryOfDispatchReader" - {
      "can be read from user answers" - {
        "when transit operation declaration type is not TIR" in {
          forAll(arbitrary[DeclarationType](arbitraryNonTIRDeclarationType)) {
            declarationType =>
              val userAnswers = emptyUserAnswers
                .setValue(TransitOperationDeclarationTypePage, declarationType)

              val expectedResult = None

              val result: EitherType[Option[Country]] = UserAnswersReader[Option[Country]](
                ItemDomain.countryOfDispatchReader(itemIndex)
              ).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }
    }
  }

}
