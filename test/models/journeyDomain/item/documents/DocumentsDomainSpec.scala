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
import pages.item.documents.AddAnotherDocumentPage
import pages.sections.documents.DocumentsSection

class DocumentsDomainSpec extends SpecBase with Generators {

  "Documents" - {

    "can be parsed from UserAnswers" - {

      "when AddAnotherDocumentPage is false" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxTransportDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments)
          .foldLeft(emptyUserAnswers) {
            case (updatedUserAnswers, index) =>
              arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
          }
          .setValue(AddAnotherDocumentPage(itemIndex), false)

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value.value.length mustBe numberOfDocuments
        result.value.pages.last mustBe DocumentsSection(itemIndex)
      }

      "when no value set for AddAnotherDocumentPage" in {
        val numberOfDocuments = Gen.choose(1, frontendAppConfig.maxTransportDocuments).sample.value

        val userAnswers = (0 until numberOfDocuments).foldLeft(emptyUserAnswers) {
          case (updatedUserAnswers, index) =>
            arbitraryDocumentAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
        }

        val result = DocumentsDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

        result.value.value.value.length mustBe numberOfDocuments
        result.value.pages.last mustBe DocumentsSection(itemIndex)
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

        result.left.value.page mustBe AddAnotherDocumentPage(itemIndex)
        result.left.value.pages.last mustBe AddAnotherDocumentPage(itemIndex)
      }
    }
  }
}
