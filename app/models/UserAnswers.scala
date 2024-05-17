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

import config.Constants.AdditionalInformation.Type30600
import pages.QuestionPage
import pages.external.ConsignmentAdditionalInformationTypePage
import pages.sections.external.{ConsignmentAdditionalInformationListSection, ConsignmentAdditionalInformationSection}
import play.api.libs.json._
import queries.{Gettable, Removable}

import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  lrn: LocalReferenceNumber,
  eoriNumber: EoriNumber,
  status: SubmissionState,
  data: JsObject = Json.obj(),
  tasks: Map[String, TaskStatus] = Map()
) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def getArraySize(array: Gettable[JsArray]): Int = get(array).map(_.value.size).getOrElse(0)

  def set[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A], reads: Reads[A]): Try[UserAnswers] = {
    lazy val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    lazy val cleanup: JsObject => Try[UserAnswers] = d => {
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }

    get(page) match {
      case Some(`value`) => Success(this)
      case _             => updatedData flatMap cleanup
    }
  }

  def remove[A](page: Removable[A]): Try[UserAnswers] = {
    val updatedData    = data.removeObject(page.path).getOrElse(data)
    val updatedAnswers = copy(data = updatedData)
    page.cleanup(None, updatedAnswers)
  }

  def updateTask(section: String, status: TaskStatus): UserAnswers = {
    val tasks = this.tasks.updated(section, status)
    this.copy(tasks = tasks)
  }

  def removeConsignmentAdditionalInformation(isCountryOfDestinationInCL009: Boolean): UserAnswers =
    if (isCountryOfDestinationInCL009) {
      val numberOfAdditionalInformation = this.getArraySize(ConsignmentAdditionalInformationListSection)
      (0 until numberOfAdditionalInformation).map(Index(_)).foldRight(this) {
        (index, acc) =>
          acc.get(ConsignmentAdditionalInformationTypePage(index)) match {
            case Some(`Type30600`) =>
              acc.remove(ConsignmentAdditionalInformationSection(index)).getOrElse(acc)
            case _ =>
              acc
          }
      }
    } else {
      this
    }
}

object UserAnswers {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[UserAnswers] =
    (
      (__ \ "lrn").read[LocalReferenceNumber] and
        (__ \ "eoriNumber").read[EoriNumber] and
        (__ \ "isSubmitted").read[SubmissionState] and
        (__ \ "data").read[JsObject] and
        (__ \ "tasks").read[Map[String, TaskStatus]]
    )(UserAnswers.apply _)

  implicit lazy val writes: Writes[UserAnswers] =
    (
      (__ \ "lrn").write[LocalReferenceNumber] and
        (__ \ "eoriNumber").write[EoriNumber] and
        (__ \ "isSubmitted").write[SubmissionState] and
        (__ \ "data").write[JsObject] and
        (__ \ "tasks").write[Map[String, TaskStatus]]
    )(unlift(UserAnswers.unapply))
}
