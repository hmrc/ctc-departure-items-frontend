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

package models

import play.api.libs.json.{JsError, JsString, JsSuccess, JsValue, Reads, Writes}

sealed trait TaskStatus {
  val jsonString: String
  def isCompleted: Boolean = this == TaskStatus.Completed
}

object TaskStatus {

  case object Completed extends TaskStatus {
    override val jsonString: String = "completed"
  }

  case object InProgress extends TaskStatus {
    override val jsonString: String = "in-progress"
  }

  case object NotStarted extends TaskStatus {
    override val jsonString: String = "not-started"
  }

  case object CannotStartYet extends TaskStatus {
    override val jsonString: String = "cannot-start-yet"
  }

  implicit val reads: Reads[TaskStatus] = (json: JsValue) => {
    json.validate[String].flatMap {
      case Completed.jsonString      => JsSuccess(Completed)
      case InProgress.jsonString     => JsSuccess(InProgress)
      case NotStarted.jsonString     => JsSuccess(NotStarted)
      case CannotStartYet.jsonString => JsSuccess(CannotStartYet)
      case x                         => JsError(s"$x is not a valid task status")
    }
  }

  implicit val writes: Writes[TaskStatus] = Writes {
    x => JsString(x.jsonString)
  }
}
