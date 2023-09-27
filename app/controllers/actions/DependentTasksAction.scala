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

import config.FrontendAppConfig
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DependentTasksActionImpl @Inject() (implicit val executionContext: ExecutionContext, config: FrontendAppConfig)
    extends DependentTasksAction
    with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {
    val incompleteTasks = config.dependentTasks.filterNot(
      request.userAnswers.tasks
        .get(_)
        .exists(
          tasks => tasks.isCompleted
        )
    )
    incompleteTasks match {
      case Nil =>
        Future.successful(None)
      case x =>
        logger.info(s"Incomplete tasks: ${x.mkString(", ")}. Redirecting to task list.")
        Future.successful(Some(Redirect(config.taskListUrl(request.userAnswers.lrn))))
    }
  }
}

trait DependentTasksAction extends ActionFilter[DataRequest]
