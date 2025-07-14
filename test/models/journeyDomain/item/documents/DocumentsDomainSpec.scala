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
import models.Index
import org.scalacheck.Gen
import pages.item.InferredAddDocumentsYesNoPage
import pages.item.documents.AddAnotherDocumentPage

class DocumentsDomainSpec extends SpecBase with Generators {

  "Documents" - {

    "can be parsed from UserAnswers" - {

      "when InferredAddDocumentsYesNoPage is true and documents is empty" in {
        val userAnswers = emptyUserAnswers
          .setValue(InferredAddDocumentsYesNoPage(itemIndex), true)
          .setValue(AddAnotherDocumentPage(itemIndex), false)

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value.value.length mustEqual 0
        result.value.pages mustEqual Seq(
          AddAnotherDocumentPage(itemIndex)
        )
      }

      "when AddAnotherDocumentPage is false" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxTransportDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments)
          .foldLeft(emptyUserAnswers) {
            case (updatedUserAnswers, index) =>
              arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
          }
          .setValue(AddAnotherDocumentPage(itemIndex), false)

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value.value.length mustEqual numberOfDocuments
        result.value.pages.last mustEqual AddAnotherDocumentPage(itemIndex)
      }

      "when no value set for AddAnotherDocumentPage" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxTransportDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments).foldLeft(emptyUserAnswers) {
          case (updatedUserAnswers, index) =>
            arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
        }

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value.value.length mustEqual numberOfDocuments
        result.value.pages.last mustEqual AddAnotherDocumentPage(itemIndex)
      }
    }

    "can not be parsed from user answers" - {
      "when AddAnotherDocumentPage is true" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxTransportDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments)
          .foldLeft(emptyUserAnswers) {
            case (updatedUserAnswers, index) =>
              arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
          }
          .setValue(AddAnotherDocumentPage(itemIndex), true)

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustEqual AddAnotherDocumentPage(itemIndex)
        result.left.value.pages.last mustEqual AddAnotherDocumentPage(itemIndex)
      }
    }
  }
}
