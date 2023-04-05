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

import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.reference.PackageType
import models.{Index, Mode, UserAnswers}
import pages.item.packages.index.PackageTypePage
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

case class PackageDomain(
  `type`: PackageType
)(itemIndex: Index, packageIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = `type`.toString

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.packages.index.routes.PackageTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, packageIndex)
      case CompletingJourney => Call(GET, "#")
    }
  }
}

object PackageDomain {

  implicit def userAnswersReader(itemIndex: Index, packagesIndex: Index): UserAnswersReader[PackageDomain] =
    PackageTypePage(itemIndex, packagesIndex).reader.map(PackageDomain(_)(itemIndex, packagesIndex))
}
