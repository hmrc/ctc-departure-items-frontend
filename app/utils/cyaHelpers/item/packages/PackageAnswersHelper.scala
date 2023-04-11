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

package utils.cyaHelpers.item.packages

import config.FrontendAppConfig
import controllers.item.packages.index.routes
import models.journeyDomain.item.packages.PackageDomain
import models.{Index, Mode, UserAnswers}
import pages.item.packages.index.PackageTypePage
import pages.sections.packages.PackagesSection
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewmodels.ListItem

class PackageAnswersHelper(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(PackagesSection(itemIndex)) {
      packageIndex =>
        buildListItem[PackageDomain](
          nameWhenComplete = _.toString,
          nameWhenInProgress = userAnswers.get(PackageTypePage(itemIndex, packageIndex)).map(_.toString),
          removeRoute = Some(routes.RemovePackageController.onPageLoad(lrn, mode, itemIndex, packageIndex))
        )(PackageDomain.userAnswersReader(itemIndex, packageIndex))
    }

}
