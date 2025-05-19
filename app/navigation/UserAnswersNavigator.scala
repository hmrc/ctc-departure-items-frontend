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

package navigation

import config.FrontendAppConfig
import models.journeyDomain.OpsError.ReaderError
import models.journeyDomain.Stage.CompletingJourney
import models.journeyDomain.*
import models.{CheckMode, Mode, UserAnswers}
import pages.Page
import play.api.Logging
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

import scala.annotation.tailrec

trait UserAnswersNavigator extends Navigator {

  implicit val config: FrontendAppConfig

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
  )(implicit userAnswersReader: UserAnswersReader[T], appConfig: FrontendAppConfig): Call =
    nextPage(
      currentPage,
      userAnswersReader.run(userAnswers),
      mode
    ).apply(userAnswers, stage).getOrElse {
      Call(GET, appConfig.notFoundUrl)
    }

  def nextPage[T <: JourneyDomainModel](
    currentPage: Option[Page],
    userAnswersReaderResult: EitherType[ReaderSuccess[T]],
    mode: Mode
  ): (UserAnswers, Stage) => Option[Call] = {

    def nextPage(
      answeredPages: List[Page]
    )(
      userAnswersReaderResult: (UserAnswers, Stage) => Option[Call]
    ): (UserAnswers, Stage) => Option[Call] =
      currentPage match {
        case Some(currentPage) =>
          @tailrec
          def rec(answeredPages: List[Page]): (UserAnswers, Stage) => Option[Call] =
            answeredPages match {
              case Nil                        => userAnswersReaderResult
              case `currentPage` :: next :: _ => (userAnswers, _) => next.route(userAnswers, mode)
              case _ :: tail                  => rec(tail)
            }
          rec(answeredPages)
        case None =>
          userAnswersReaderResult
      }

    userAnswersReaderResult match {
      case Right(ReaderSuccess(t, _)) if mode == CheckMode =>
        t.routeIfCompleted(_, mode, _)
      case Right(ReaderSuccess(t, answeredPages)) =>
        nextPage(answeredPages.toList) {
          t.routeIfCompleted(_, mode, _)
        }
      case Left(ReaderError(unansweredPage, answeredPages, _)) =>
        nextPage(answeredPages.toList) {
          (userAnswers, _) => unansweredPage.route(userAnswers, mode)
        }
    }
  }
}
