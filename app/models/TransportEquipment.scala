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

import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

import java.util.UUID
import play.api.libs.functional.syntax._

case class TransportEquipment(number: Int, containerId: Option[String], uuid: UUID) extends Selectable {

  override def toString: String = containerId match {
    case Some(value) => s"${number + 1} - $value"
    case None        => s"${number + 1}"
  }

  def asString(implicit messages: Messages): String = containerId match {
    case Some(value) => messages("item.transportEquipment.withContainerId", number, value)
    case None        => messages("item.transportEquipment.withoutContainerId", number)
  }

  override def toSelectItem(selected: Boolean = false)(implicit messages: Messages): SelectItem = SelectItem(Some(value), this.asString, selected)

  override val value: String = s"$number"
}

object TransportEquipment {

  implicit val equipmentReads: Int => Reads[TransportEquipment] = index =>
    ((__ \ "containerIdentificationNumber").readNullable[String] and
      (__ \ "uuid").read[UUID])
      .apply {
        (containerId, uuid) =>
          TransportEquipment(index, containerId, uuid)
      }
}
