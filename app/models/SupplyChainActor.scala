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

package models

import models.reference.SupplyChainActorType
import pages.item.supplyChainActors.index.IdentificationNumberPage
import pages.item.supplyChainActors.index.SupplyChainActorTypePage
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Reads

case class SupplyChainActor(supplyChainActorType: SupplyChainActorType, identificationNumber: Option[String]) {

  def forRemoveDisplay: String = identificationNumber match {
    case Some(value) => s"${supplyChainActorType.toString} - $value"
    case None        => supplyChainActorType.toString
  }
}

object SupplyChainActor {

  def apply(userAnswers: UserAnswers, itemIndex: Index, actorIndex: Index): Option[SupplyChainActor] = {
    implicit val reads: Reads[SupplyChainActor] = (
      SupplyChainActorTypePage(itemIndex, actorIndex).path.read[SupplyChainActorType] and
        IdentificationNumberPage(itemIndex, actorIndex).path.readNullable[String]
    ).apply {
      (supplyChainActorType, identificationNumber) => SupplyChainActor(supplyChainActorType, identificationNumber)
    }
    userAnswers.data.asOpt[SupplyChainActor]
  }
}
