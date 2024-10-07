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

package viewmodels.item.packages

import config.{FrontendAppConfig, PhaseConfig}
import controllers.item.packages.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.cyaHelpers.item.packages.PackageAnswersHelper
import viewmodels.{AddAnotherViewModel, ListItem}

import javax.inject.Inject

case class AddAnotherPackageViewModel(
  override val listItems: Seq[ListItem],
  onSubmitCall: Call
) extends AddAnotherViewModel {

  override val prefix: String = "item.packages.addAnotherPackage"

  override def allowMore(implicit config: FrontendAppConfig): Boolean = count < config.maxPackages
}

object AddAnotherPackageViewModel {

  class AddAnotherPackageViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, itemIndex: Index)(implicit
      messages: Messages,
      config: FrontendAppConfig,
      phaseConfig: PhaseConfig
    ): AddAnotherPackageViewModel = {
      val helper = new PackageAnswersHelper(userAnswers, mode, itemIndex)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherPackageViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherPackageController.onSubmit(userAnswers.lrn, mode, itemIndex)
      )
    }
  }
}
