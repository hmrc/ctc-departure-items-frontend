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

import cats.data.ReaderT
import models.TaskStatus.{Completed, InProgress}
import models.UserAnswers
import models.journeyDomain.{ItemsDomain, UserAnswersReader, WriterError}
import models.requests.MandatoryDataRequest
import navigation.UserAnswersNavigator
import pages.QuestionPage
import play.api.libs.json.Format
import play.api.mvc.Results.Redirect
import play.api.mvc.{Call, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object controllers {

  type EitherType[A]        = Either[WriterError, A]
  type UserAnswersWriter[A] = ReaderT[EitherType, UserAnswers, A]
  type Write[A]             = (QuestionPage[A], UserAnswers)

  implicit class SettableOps[A](page: QuestionPage[A]) {

    def writeToUserAnswers(value: A)(implicit format: Format[A]): UserAnswersWriter[Write[A]] =
      ReaderT[EitherType, UserAnswers, Write[A]](
        userAnswers =>
          userAnswers.set[A](page, value) match {
            case Success(userAnswers) => Right((page, userAnswers))
            case Failure(exception)   => Left(WriterError(page, Some(s"Failed to write $value to page ${page.path} with exception: ${exception.toString}")))
          }
      )

    def removeFromUserAnswers(): UserAnswersWriter[Write[A]] =
      ReaderT[EitherType, UserAnswers, Write[A]] {
        userAnswers =>
          userAnswers.remove(page) match {
            case Success(value)     => Right((page, value))
            case Failure(exception) => Left(WriterError(page, Some(s"Failed to remove ${page.path} with exception: ${exception.toString}")))
          }
      }
  }

  implicit class SettableOpsRunner[A](userAnswersWriter: UserAnswersWriter[Write[A]]) {

    def updateTask(): UserAnswersWriter[Write[A]] =
      userAnswersWriter.flatMapF {
        case (page, userAnswers) =>
          page.path.path.headOption.map(_.toJsonString) match {
            case Some(section) =>
              val status = UserAnswersReader[ItemsDomain].run(userAnswers) match {
                case Left(_)  => InProgress
                case Right(_) => Completed
              }
              Right((page, userAnswers.updateTask(section, status)))
            case None =>
              Left(WriterError(page, Some(s"Failed to find section in JSON path ${page.path}")))
          }
      }

    def writeToSession(
      userAnswers: UserAnswers
    )(implicit sessionRepository: SessionRepository, executionContext: ExecutionContext, hc: HeaderCarrier): Future[Write[A]] =
      userAnswersWriter.run(userAnswers) match {
        case Left(opsError) => Future.failed(new Exception(s"${opsError.toString}"))
        case Right(value) =>
          sessionRepository
            .set(value._2)
            .map(
              _ => value
            )
      }

    def writeToSession()(implicit
      dataRequest: MandatoryDataRequest[_],
      sessionRepository: SessionRepository,
      ex: ExecutionContext,
      hc: HeaderCarrier
    ): Future[Write[A]] = writeToSession(dataRequest.userAnswers)
  }

  implicit class NavigatorOps[A](write: Future[Write[A]]) {

    def navigate()(implicit navigator: UserAnswersNavigator, executionContext: ExecutionContext): Future[Result] =
      navigate {
        case (_, userAnswers) => navigator.nextPage(userAnswers)
      }

    def navigateTo(call: Call)(implicit executionContext: ExecutionContext): Future[Result] =
      navigate {
        _ => call
      }

    def navigateTo(url: String)(implicit executionContext: ExecutionContext): Future[Result] =
      write.map {
        _ => Redirect(url)
      }

    private def navigate(result: Write[A] => Call)(implicit executionContext: ExecutionContext): Future[Result] =
      write.map {
        w => Redirect(result(w))
      }
  }
}
