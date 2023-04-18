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

package models.journeyDomain.item.packages

import cats.implicits._
import models.journeyDomain.Stage._
import models.journeyDomain._
import models.reference.PackageType
import models.{Index, Mode, PackingType, UserAnswers}
import pages.item.packages.index._
import play.api.mvc.Call

case class PackageDomain(
  `type`: PackageType,
  numberOfPackages: Option[Int],
  shippingMark: Option[String]
)(itemIndex: Index, packageIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = s"${numberOfPackages.getOrElse(1)} ${`type`}"

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.packages.index.routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex)
      case CompletingJourney =>
        controllers.item.packages.routes.AddAnotherPackageController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object PackageDomain {

  implicit def userAnswersReader(itemIndex: Index, packageIndex: Index): UserAnswersReader[PackageDomain] = {
    lazy val shippingMarkReads = AddShippingMarkYesNoPage(itemIndex, packageIndex)
      .filterOptionalDependent(identity)(ShippingMarkPage(itemIndex, packageIndex).reader)

    PackageTypePage(itemIndex, packageIndex).reader.flatMap {
      case value @ PackageType(_, _, PackingType.Unpacked) =>
        (
          UserAnswersReader(value),
          NumberOfPackagesPage(itemIndex, packageIndex).reader.map(Some(_)),
          shippingMarkReads
        ).tupled.map((PackageDomain.apply _).tupled).map(_(itemIndex, packageIndex))
      case value @ PackageType(_, _, PackingType.Bulk) =>
        (
          UserAnswersReader(value),
          UserAnswersReader(None),
          shippingMarkReads
        ).tupled.map((PackageDomain.apply _).tupled).map(_(itemIndex, packageIndex))
      case value @ PackageType(_, _, PackingType.Other) =>
        (
          UserAnswersReader(value),
          UserAnswersReader(None),
          ShippingMarkPage(itemIndex, packageIndex).reader.map(Some(_))
        ).tupled.map((PackageDomain.apply _).tupled).map(_(itemIndex, packageIndex))
    }
  }
}
