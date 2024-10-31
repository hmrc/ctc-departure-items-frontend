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

package pages.item

import controllers.item.routes
import models.{Index, Mode, UserAnswers}
import pages.sections.ItemSection
import pages.{InferredPage, QuestionPage}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.util.UUID
import scala.util.Try

sealed abstract class BaseTransportEquipmentPage(itemIndex: Index) extends QuestionPage[UUID] {

  override def path: JsPath = ItemSection(itemIndex).path \ toString

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.TransportEquipmentController.onPageLoad(userAnswers.lrn, mode, itemIndex))

  def cleanup(userAnswers: UserAnswers): Try[UserAnswers]

  override def cleanup(value: Option[UUID], userAnswers: UserAnswers): Try[UserAnswers] = value match {
    case Some(_) => cleanup(userAnswers)
    case None    => super.cleanup(value, userAnswers)
  }
}

case class TransportEquipmentPage(itemIndex: Index) extends BaseTransportEquipmentPage(itemIndex) {
  override def toString: String = "transportEquipment"

  override def cleanup(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.remove(InferredTransportEquipmentPage(itemIndex))
}

// TODO - remove InferredTransportEquipmentPage and update submission logic 30 days after CTCP-5979 goes live
case class InferredTransportEquipmentPage(itemIndex: Index) extends BaseTransportEquipmentPage(itemIndex) with InferredPage[UUID] {
  override def toString: String = "inferredTransportEquipment"

  override def cleanup(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.remove(TransportEquipmentPage(itemIndex))
}
