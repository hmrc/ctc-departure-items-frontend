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

package viewmodels.item

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import services.DocumentsService
import viewmodels.item.ItemAnswersViewModel.ItemAnswersViewModelProvider

class ItemAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  implicit private val documentsService: DocumentsService = injector.instanceOf[DocumentsService]

  "apply" - {
    "must return all sections" in {
      forAll(arbitraryItemAnswers(emptyUserAnswers, itemIndex)) {
        answers =>
          val viewModelProvider = new ItemAnswersViewModelProvider()
          val sections          = viewModelProvider.apply(answers, itemIndex).sections

          sections.size mustBe 8

          sections.head.sectionTitle must not be defined
          sections.head.rows must not be empty
          sections.head.addAnotherLink must not be defined

          sections(1: Int).sectionTitle.get mustBe "Dangerous goods"

          sections(2: Int).sectionTitle.get mustBe "Measurement"
          sections(2: Int).addAnotherLink must not be defined

          sections(3: Int).sectionTitle.get mustBe "Packages"

          sections(4: Int).sectionTitle.get mustBe "Supply chain actors"

          sections(5: Int).sectionTitle.get mustBe "Documents"

          sections(6: Int).sectionTitle.get mustBe "Additional references"

          sections(7: Int).sectionTitle.get mustBe "Additional information"

      }
    }
  }
}
