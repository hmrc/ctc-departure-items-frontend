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

package navigation

import models.journeyDomain.Stage.CompletingJourney
import models.journeyDomain.{JourneyDomainModel, ReaderError, Stage, UserAnswersReader}
import models.{Mode, UserAnswers}
import play.api.Logging
import play.api.mvc.Call

trait UserAnswersNavigator extends Navigator {

  type T <: JourneyDomainModel

  implicit val reader: UserAnswersReader[T]

  val mode: Mode

  override def nextPage(userAnswers: UserAnswers): Call =
    UserAnswersNavigator.nextPage[T](userAnswers, mode)
}

object UserAnswersNavigator extends Logging {

  def nextPage[T <: JourneyDomainModel](
    userAnswers: UserAnswers,
    mode: Mode,
    stage: Stage = CompletingJourney
  )(implicit userAnswersReader: UserAnswersReader[T]): Call = {
    lazy val errorCall = controllers.routes.ErrorController.notFound()

    userAnswersReader.run(userAnswers) match {
      case Left(ReaderError(page, _)) =>
        page.route(userAnswers, mode).getOrElse {
          logger.debug(s"Route not defined for page ${page.path}")
          errorCall
        }
      case Right(x) =>
        x.routeIfCompleted(userAnswers, mode, stage).getOrElse {
          logger.debug(s"Completed route not defined for model $x")
          errorCall
        }
    }
  }
}
