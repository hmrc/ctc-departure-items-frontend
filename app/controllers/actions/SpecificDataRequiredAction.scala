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

package controllers.actions

import models.UserAnswers
import models.requests._
import play.api.Logging
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import queries.Gettable
import shapeless.syntax.std.tuple._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// scalastyle:off no.whitespace.after.left.bracket
class SpecificDataRequiredActionImpl @Inject() (implicit val ec: ExecutionContext) extends SpecificDataRequiredActionProvider {

  override def getFirst[T1](
    pages: Gettable[T1]*
  )(implicit rds: Reads[T1]): ActionRefiner[
    DataRequest,
    SpecificDataRequestProvider1[T1]#SpecificDataRequest
  ] = new SpecificDataRequiredAction1(pages: _*)

  override def getSecond[T1, T2](
    page: Gettable[T2]
  )(implicit rds: Reads[T2]): ActionRefiner[
    SpecificDataRequestProvider1[T1]#SpecificDataRequest,
    SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest
  ] = new SpecificDataRequiredAction2(page)

  override def getThird[T1, T2, T3](
    page: Gettable[T3]
  )(implicit rds: Reads[T3]): ActionRefiner[
    SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest,
    SpecificDataRequestProvider3[T1, T2, T3]#SpecificDataRequest
  ] = new SpecificDataRequiredAction3(page)
}

trait SpecificDataRequiredActionProvider {

  def apply[T1](
    pages: Gettable[T1]*
  )(implicit rds: Reads[T1]): ActionRefiner[DataRequest, SpecificDataRequestProvider1[T1]#SpecificDataRequest] = getFirst(pages: _*)

  def getFirst[T1](
    pages: Gettable[T1]*
  )(implicit rds: Reads[T1]): ActionRefiner[
    DataRequest,
    SpecificDataRequestProvider1[T1]#SpecificDataRequest
  ]

  def getSecond[T1, T2](
    page: Gettable[T2]
  )(implicit rds: Reads[T2]): ActionRefiner[
    SpecificDataRequestProvider1[T1]#SpecificDataRequest,
    SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest
  ]

  def getThird[T1, T2, T3](
    page: Gettable[T3]
  )(implicit rds: Reads[T3]): ActionRefiner[
    SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest,
    SpecificDataRequestProvider3[T1, T2, T3]#SpecificDataRequest
  ]
}

trait SpecificDataRequiredAction extends Logging {

  lazy val defaultRedirect: Result = Redirect(controllers.routes.SessionExpiredController.onPageLoad())

  def getPage[T, R](userAnswers: UserAnswers, page: Gettable[T])(block: T => R)(implicit rds: Reads[T]): Future[Either[Result, R]] =
    Future.successful {
      userAnswers.get(page) match {
        case Some(value) =>
          Right(block(value))
        case None =>
          logger.warn(s"${page.path} is missing from user answers. Redirecting to session expired.")
          Left(defaultRedirect)
      }
    }
}

class SpecificDataRequiredAction1[T1](
  pages: Gettable[T1]*
)(implicit val executionContext: ExecutionContext, rds: Reads[T1])
    extends ActionRefiner[
      DataRequest,
      SpecificDataRequestProvider1[T1]#SpecificDataRequest
    ]
    with SpecificDataRequiredAction {

  override protected def refine[A](
    request: DataRequest[A]
  ): Future[Either[Result, SpecificDataRequestProvider1[T1]#SpecificDataRequest[A]]] = {
    def rec(pages: Seq[Gettable[T1]]): Future[Either[Result, SpecificDataRequestProvider1[T1]#SpecificDataRequest[A]]] =
      pages match {
        case Nil =>
          Future.successful(Left(defaultRedirect))
        case _ =>
          getPage(request.userAnswers, pages.head) {
            value =>
              new SpecificDataRequestProvider1[T1].SpecificDataRequest(
                request = request,
                eoriNumber = request.eoriNumber,
                userAnswers = request.userAnswers,
                arg = value
              )
          }.flatMap {
            case Left(_) => rec(pages.tail)
            case x       => Future.successful(x)
          }
      }

    rec(pages)
  }
}

class SpecificDataRequiredAction2[T1, T2](
  page: Gettable[T2]
)(implicit val executionContext: ExecutionContext, rds: Reads[T2])
    extends ActionRefiner[
      SpecificDataRequestProvider1[T1]#SpecificDataRequest,
      SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest
    ]
    with SpecificDataRequiredAction {

  override protected def refine[A](
    request: SpecificDataRequestProvider1[T1]#SpecificDataRequest[A]
  ): Future[Either[Result, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest[A]]] =
    getPage(request.userAnswers, page) {
      value =>
        new SpecificDataRequestProvider2[T1, T2].SpecificDataRequest(
          request = request,
          eoriNumber = request.eoriNumber,
          userAnswers = request.userAnswers,
          arg = (request.arg, value)
        )
    }
}

class SpecificDataRequiredAction3[T1, T2, T3](
  page: Gettable[T3]
)(implicit val executionContext: ExecutionContext, rds: Reads[T3])
    extends ActionRefiner[
      SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest,
      SpecificDataRequestProvider3[T1, T2, T3]#SpecificDataRequest
    ]
    with SpecificDataRequiredAction {

  override protected def refine[A](
    request: SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest[A]
  ): Future[Either[Result, SpecificDataRequestProvider3[T1, T2, T3]#SpecificDataRequest[A]]] =
    getPage(request.userAnswers, page) {
      value =>
        new SpecificDataRequestProvider3[T1, T2, T3].SpecificDataRequest(
          request = request,
          eoriNumber = request.eoriNumber,
          userAnswers = request.userAnswers,
          arg = request.arg :+ value
        )
    }

}
// scalastyle:on no.whitespace.after.left.bracket
