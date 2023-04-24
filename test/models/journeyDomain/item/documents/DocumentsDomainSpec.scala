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

package models.journeyDomain.item.documents

import base.SpecBase
import generators.Generators
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.item.documents.DocumentsInProgressPage

class DocumentsDomainSpec extends SpecBase with Generators {

  "Documents" - {

    "can be parsed from UserAnswers" - {

      "when documents not in progress" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments)
          .foldLeft(emptyUserAnswers)({
            case (updatedUserAnswers, index) =>
              arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
          })
          .setValue(DocumentsInProgressPage(itemIndex), false)

        val result: EitherType[DocumentsDomain] = UserAnswersReader[DocumentsDomain](
          DocumentsDomain.userAnswersReader(itemIndex)
        ).run(userAnswers)

        result.value.value.length mustBe numberOfDocuments
      }

      "when no value set for DocumentsInProgressPage" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments).foldLeft(emptyUserAnswers)({
          case (updatedUserAnswers, index) =>
            arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
        })

        val result: EitherType[DocumentsDomain] = UserAnswersReader[DocumentsDomain](
          DocumentsDomain.userAnswersReader(itemIndex)
        ).run(userAnswers)

        result.value.value.length mustBe numberOfDocuments
      }
    }

    "can not be parsed from user answers" - {
      "when documents in progress" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments)
          .foldLeft(emptyUserAnswers)({
            case (updatedUserAnswers, index) =>
              arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
          })
          .setValue(DocumentsInProgressPage(itemIndex), true)

        val result: EitherType[DocumentsDomain] = UserAnswersReader[DocumentsDomain](
          DocumentsDomain.userAnswersReader(itemIndex)
        ).run(userAnswers)

        result.left.value.page mustBe DocumentsInProgressPage(itemIndex)
      }
    }
  }
}
