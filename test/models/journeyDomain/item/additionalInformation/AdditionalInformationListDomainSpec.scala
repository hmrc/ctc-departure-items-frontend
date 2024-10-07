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

package models.journeyDomain.item.additionalInformation

import base.SpecBase
import generators.Generators
import models.Index
import org.scalacheck.Gen
import pages.sections.additionalInformation.AdditionalInformationListSection

class AdditionalInformationListDomainSpec extends SpecBase with Generators {

  "AdditionalInformationList" - {

    "can be parsed from UserAnswers" in {

      val numberOfAdditionalInformation = Gen.choose(1, frontendAppConfig.maxAdditionalInformation).sample.value

      val userAnswers = (0 until numberOfAdditionalInformation).foldLeft(emptyUserAnswers) {
        case (updatedUserAnswers, index) =>
          arbitraryAdditionalInformationAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
      }

      val result = AdditionalInformationListDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

      result.value.value.value.length mustBe numberOfAdditionalInformation
      result.value.pages.last mustBe AdditionalInformationListSection(itemIndex)
    }
  }
}
