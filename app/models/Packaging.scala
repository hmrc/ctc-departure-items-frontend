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

import models.reference.PackageType
import play.api.libs.json.Reads
import pages.item.packages.index.PackageTypePage
import pages.item.packages.index.NumberOfPackagesPage
import play.api.libs.functional.syntax.toFunctionalBuilderOps

case class Packaging(`type`: PackageType, quantity: Option[BigInt]) {

  def forRemoveDisplay: String = quantity match {
    case Some(value) => s"$value * ${`type`.toString}"
    case None        => `type`.toString
  }
}

object Packaging {

  def apply(userAnswers: UserAnswers, itemIndex: Index, packageIndex: Index): Option[Packaging] = {
    implicit val reads: Reads[Packaging] = (
      PackageTypePage(itemIndex, packageIndex).path.read[PackageType] and
        NumberOfPackagesPage(itemIndex, packageIndex).path.readNullable[BigInt]
    ).apply {
      (packageType, quantity) => Packaging(packageType, quantity)
    }
    userAnswers.data.asOpt[Packaging]
  }
}
