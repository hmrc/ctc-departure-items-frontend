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

package models.journeyDomain.item.documents

import base.SpecBase
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.item.documents.index.{DocumentInProgressPage, DocumentPage}

import java.util.UUID

class DocumentDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Document Domain" - {

    "can be read from user answers" - {
      "when document is answered" in {
        forAll(arbitrary[UUID]) {
          document =>
            val userAnswers = emptyUserAnswers
              .setValue(DocumentPage(itemIndex, documentIndex), document)

            val expectedResult = DocumentDomain(
              document = document
            )(itemIndex, documentIndex)

            val result = DocumentDomain.userAnswersReader(itemIndex, documentIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              DocumentPage(itemIndex, documentIndex)
            )
        }
      }
    }

    "cannot be read from user answers" - {
      "when document is in progress (i.e. user redirected back to documents section)" in {
        val userAnswers = emptyUserAnswers
          .setValue(DocumentInProgressPage(itemIndex, documentIndex), true)

        val result = DocumentDomain.userAnswersReader(itemIndex, packageIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe DocumentInProgressPage(itemIndex, documentIndex)
        result.left.value.pages mustBe Seq(
          DocumentInProgressPage(itemIndex, documentIndex)
        )
      }

      "when document is not answered" in {
        val result = DocumentDomain.userAnswersReader(itemIndex, packageIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe DocumentPage(itemIndex, documentIndex)
        result.left.value.pages mustBe Seq(
          DocumentPage(itemIndex, documentIndex)
        )
      }
    }
  }
}
