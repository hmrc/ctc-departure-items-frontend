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

package models

import base.SpecBase
import generators.Generators
import models.reference.PackageType
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class PackagingSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Packaging" - {

    "for Remove Display" - {
      "must return quantity and type when quantity is defined" in {
        val numberOfPackages = intsLargerThanMaxValue.sample.get
        forAll(arbitrary[PackageType]) {
          packageType =>
            val packaging = new Packaging(packageType, Some(numberOfPackages))
            val result    = packaging.forRemoveDisplay
            result mustEqual s"${numberOfPackages.toString} * ${packageType.toString}"
        }
      }

      "must return type when quantity is not defined" in {
        forAll(arbitrary[PackageType]) {
          packageType =>
            val packaging = new Packaging(packageType, None)
            val result    = packaging.forRemoveDisplay
            result mustEqual s"${packageType.toString}"
        }
      }

    }
  }

}
