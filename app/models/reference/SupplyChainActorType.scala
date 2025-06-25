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

package models.reference

import cats.Order
import config.FrontendAppConfig
import models.{DynamicEnumerableType, Radioable}
import org.apache.commons.text.StringEscapeUtils
import play.api.libs.functional.syntax.*
import play.api.libs.json.{__, Format, Json, Reads}

case class SupplyChainActorType(role: String, description: String) extends Radioable[SupplyChainActorType] {

  val code: String = role

  override def toString: String = StringEscapeUtils.unescapeXml(description)

  override val messageKeyPrefix: String = "item.supplyChainActors.index.supplyChainActorType"

}

object SupplyChainActorType extends DynamicEnumerableType[SupplyChainActorType] {

  def reads(config: FrontendAppConfig): Reads[SupplyChainActorType] =
    if (config.isPhase6Enabled) {
      (
        (__ \ "key").read[String] and
          (__ \ "value").read[String]
      )(SupplyChainActorType.apply)
    } else {
      Json.reads[SupplyChainActorType]
    }

  implicit val format: Format[SupplyChainActorType] = Json.format[SupplyChainActorType]

  implicit val order: Order[SupplyChainActorType] = (x: SupplyChainActorType, y: SupplyChainActorType) => (x, y).compareBy(_.role)
}
