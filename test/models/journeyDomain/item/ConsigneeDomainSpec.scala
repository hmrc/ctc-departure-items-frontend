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
import models.DynamicAddress
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.consignee._

class ConsigneeDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Consignee Domain" - {

    "can be read from user answers" - {
      "when identification number defined" in {
        forAll(nonEmptyString) {
          identificationNumber =>
            val userAnswers = emptyUserAnswers
              .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), true)
              .setValue(IdentificationNumberPage(itemIndex), identificationNumber)

            val expectedResult = ConsigneeDomainWithIdentificationNumber(
              identificationNumber = identificationNumber
            )(itemIndex)

            val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              AddConsigneeEoriNumberYesNoPage(itemIndex),
              IdentificationNumberPage(itemIndex)
            )
        }
      }

      "when identification number undefined" in {
        forAll(nonEmptyString, arbitrary[Country], arbitrary[DynamicAddress]) {
          (name, country, address) =>
            val userAnswers = emptyUserAnswers
              .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), false)
              .setValue(NamePage(itemIndex), name)
              .setValue(CountryPage(itemIndex), country)
              .setValue(AddressPage(itemIndex), address)

            val expectedResult = ConsigneeDomainWithNameAndAddress(
              name = name,
              country = country,
              address = address
            )(itemIndex)

            val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              AddConsigneeEoriNumberYesNoPage(itemIndex),
              NamePage(itemIndex),
              CountryPage(itemIndex),
              AddressPage(itemIndex)
            )
        }
      }
    }

    "can not be read from user answers" - {
      "when add EORI/TIN yes/no unanswered" in {
        val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe AddConsigneeEoriNumberYesNoPage(itemIndex)
        result.left.value.pages mustBe Seq(
          AddConsigneeEoriNumberYesNoPage(itemIndex)
        )
      }

      "when EORI/TIN unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), true)

        val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe IdentificationNumberPage(itemIndex)
        result.left.value.pages mustBe Seq(
          AddConsigneeEoriNumberYesNoPage(itemIndex),
          IdentificationNumberPage(itemIndex)
        )
      }

      "when name unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), false)

        val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe NamePage(itemIndex)
        result.left.value.pages mustBe Seq(
          AddConsigneeEoriNumberYesNoPage(itemIndex),
          NamePage(itemIndex)
        )
      }

      "when country unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), false)
          .setValue(NamePage(itemIndex), nonEmptyString.sample.value)

        val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe CountryPage(itemIndex)
        result.left.value.pages mustBe Seq(
          AddConsigneeEoriNumberYesNoPage(itemIndex),
          NamePage(itemIndex),
          CountryPage(itemIndex)
        )
      }

      "when address unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddConsigneeEoriNumberYesNoPage(itemIndex), false)
          .setValue(NamePage(itemIndex), nonEmptyString.sample.value)
          .setValue(CountryPage(itemIndex), arbitrary[Country].sample.value)

        val result = ConsigneeDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe AddressPage(itemIndex)
        result.left.value.pages mustBe Seq(
          AddConsigneeEoriNumberYesNoPage(itemIndex),
          NamePage(itemIndex),
          CountryPage(itemIndex),
          AddressPage(itemIndex)
        )
      }
    }
  }
}
