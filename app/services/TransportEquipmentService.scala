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

package services

import models.{Index, RichJsArray, SelectableList, TransportEquipment, UserAnswers}
import pages.item.TransportEquipmentPage
import pages.sections.external.TransportEquipmentsSection

import javax.inject.Inject

class TransportEquipmentService @Inject() () {

  def getTransportEquipments(userAnswers: UserAnswers): SelectableList[TransportEquipment] = {
    val transportEquipments = equipmentsSection(userAnswers)
    SelectableList(transportEquipments)
  }

  def getTransportEquipment(userAnswers: UserAnswers, itemIndex: Index): Option[TransportEquipment] =
    for {
      uuid <- userAnswers.get(TransportEquipmentPage(itemIndex))
      transportEquipments = equipmentsSection(userAnswers)
      result <- transportEquipments.find(_.uuid == uuid)
    } yield result

  private def equipmentsSection(userAnswers: UserAnswers): Seq[TransportEquipment] = userAnswers
    .get(TransportEquipmentsSection)
    .map(_.validateAsAListOf[TransportEquipment])
    .getOrElse(Nil)
}
