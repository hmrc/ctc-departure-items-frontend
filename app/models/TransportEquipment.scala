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

case class TransportEquipment(number: Int, containerId: Option[String]) extends Selectable {

  def asString(implicit messages: Messages): String = containerId match {
    case Some(value) => messages("item.transportEquipment.withContainerId", number, value)
    case None        => messages("item.transportEquipment.withoutContainerId", number)
  }

  override def toSelectItem(selected: Boolean = false)(implicit messages: Messages): SelectItem = SelectItem(Some(value), this.asString, selected)

  override val value: String = s"$number"
}

object TransportEquipment {

  def equipmentReads(equipmentIndex: Int): Reads[TransportEquipment] =
    (__ \ "containerIdentificationNumber").readNullable[String].map {
      containerId =>
        TransportEquipment(equipmentIndex, containerId)
    }
}
