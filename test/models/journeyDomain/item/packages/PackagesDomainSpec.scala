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

package models.journeyDomain.item.packages

import base.SpecBase
import generators.Generators
import models.Index
import org.scalacheck.Gen
import pages.item.packages.index.AddAnotherPackagePage

class PackagesDomainSpec extends SpecBase with Generators {

  "Packages" - {

    "can be parsed from UserAnswers" in {

      val numberOfPackages = Gen.choose(1, frontendAppConfig.maxPackages).sample.value

      val userAnswers = (0 until numberOfPackages).foldLeft(emptyUserAnswers) {
        case (updatedUserAnswers, index) =>
          arbitraryPackageAnswers(updatedUserAnswers, itemIndex, Index(index)).sample.value
      }

      val result = PackagesDomain.userAnswersReader(itemIndex).apply(Nil).run(userAnswers)

      result.value.value.value.length mustEqual numberOfPackages
      result.value.pages.last mustEqual AddAnotherPackagePage(itemIndex)
    }
  }
}
