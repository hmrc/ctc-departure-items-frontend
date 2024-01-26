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

import config.{FrontendAppConfig, PhaseConfig}
import models.journeyDomain._
import models.journeyDomain.OpsError.ReaderError
import models.journeyDomain.Stage.CompletingJourney
import models.journeyDomain.{JourneyDomainModel, ReaderSuccess, Stage}
import models.{CheckMode, Mode, Phase, UserAnswers}
import pages.Page
import play.api.Logging
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

import scala.annotation.tailrec

trait UserAnswersNavigator extends Navigator {

  implicit val config: FrontendAppConfig
  implicit val phaseConfig: PhaseConfig

  type T <: JourneyDomainModel

  implicit val reader: UserAnswersReader[T]

  val mode: Mode

  override def nextPage(userAnswers: UserAnswers, currentPage: Option[Page]): Call =
    UserAnswersNavigator.nextPage[T](userAnswers, currentPage, mode)
}

object UserAnswersNavigator extends Logging {

  def nextPage[T <: JourneyDomainModel](
    userAnswers: UserAnswers,
    currentPage: Option[Page],
    mode: Mode,
    stage: Stage = CompletingJourney
  )(implicit userAnswersReader: UserAnswersReader[T], appConfig: FrontendAppConfig, phaseConfig: PhaseConfig): Call =
    nextPage(
      currentPage,
      userAnswersReader.run(userAnswers),
      mode
    ).apply(userAnswers, stage, phaseConfig.phase).getOrElse {
      Call(GET, appConfig.notFoundUrl)
    }

  def nextPage[T <: JourneyDomainModel](
    currentPage: Option[Page],
    userAnswersReaderResult: EitherType[ReaderSuccess[T]],
    mode: Mode
  ): (UserAnswers, Stage, Phase) => Option[Call] = {
    @tailrec
    def rec(
      answeredPages: List[Page],
      exit: Boolean
    )(
      userAnswersReaderResult: (UserAnswers, Stage, Phase) => Option[Call]
    ): (UserAnswers, Stage, Phase) => Option[Call] =
      answeredPages match {
        case head :: _ if exit                          => (userAnswers, _, _) => head.route(userAnswers, mode)
        case head :: tail if currentPage.contains(head) => rec(tail, exit = true)(userAnswersReaderResult)
        case _ :: tail                                  => rec(tail, exit)(userAnswersReaderResult)
        case Nil                                        => userAnswersReaderResult
      }

    userAnswersReaderResult match {
      case Right(ReaderSuccess(t, _)) if mode == CheckMode =>
        t.routeIfCompleted(_, mode, _, _)
      case Right(ReaderSuccess(t, answeredPages)) =>
        rec(answeredPages.toList, exit = false) {
          t.routeIfCompleted(_, mode, _, _)
        }
      case Left(ReaderError(unansweredPage, answeredPages, _)) =>
        rec(answeredPages.toList, exit = false) {
          (userAnswers, _, _) => unansweredPage.route(userAnswers, mode)
        }
    }
  }
}
