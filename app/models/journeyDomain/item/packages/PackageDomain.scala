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

import models.journeyDomain.*
import models.journeyDomain.Stage.*
import models.reference.PackageType
import models.{Index, Mode, PackingType, UserAnswers}
import pages.item.packages.index.*
import play.api.mvc.Call

case class PackageDomain(
  `type`: PackageType,
  numberOfPackages: Option[Int],
  shippingMark: Option[String]
)(itemIndex: Index, packageIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = s"${numberOfPackages.getOrElse(1)} * ${`type`}"

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

  implicit def userAnswersReader(itemIndex: Index, packageIndex: Index): Read[PackageDomain] = {

    lazy val shippingMarkReads = AddShippingMarkYesNoPage(itemIndex, packageIndex)
      .filterOptionalDependent(identity)(ShippingMarkPage(itemIndex, packageIndex).reader)

    def numberOfPackagesReads(isPackingTypeInCL182: Boolean, isPackingTypeInCL181: Boolean): Read[Option[Int]] =
      (isPackingTypeInCL182, isPackingTypeInCL181) match {
        case (true, _) | (false, false) =>
          NumberOfPackagesPage(itemIndex, packageIndex).reader.to {
            numberOfPackages =>
              lazy val reader = Read.apply(numberOfPackages).toOption
              numberOfPackages match {
                case 0 =>
                  BeforeYouContinuePage(itemIndex, packageIndex).reader.to {
                    _ => reader
                  }
                case _ => reader
              }
          }
        case _ =>
          UserAnswersReader.none
      }

    PackageTypePage(itemIndex, packageIndex).reader.to {
      case value @ PackageType(_, _, PackingType.Unpacked) =>
        (
          numberOfPackagesReads(isPackingTypeInCL182 = true, isPackingTypeInCL181 = false),
          shippingMarkReads
        ).map(PackageDomain.apply(value, _, _)(itemIndex, packageIndex))
      case value @ PackageType(_, _, PackingType.Bulk) =>
        (
          numberOfPackagesReads(isPackingTypeInCL182 = false, isPackingTypeInCL181 = true),
          shippingMarkReads
        ).map(PackageDomain.apply(value, _, _)(itemIndex, packageIndex))
      case value @ PackageType(_, _, PackingType.Other) =>
        (
          numberOfPackagesReads(isPackingTypeInCL182 = false, isPackingTypeInCL181 = false),
          ShippingMarkPage(itemIndex, packageIndex).reader.toOption
        ).map(PackageDomain.apply(value, _, _)(itemIndex, packageIndex))
    }
  }
}
