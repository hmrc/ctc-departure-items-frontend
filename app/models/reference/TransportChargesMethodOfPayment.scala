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
import play.api.libs.functional.syntax.*
import play.api.libs.json.{__, Format, Json, Reads}

case class TransportChargesMethodOfPayment(method: String, description: String) extends Radioable[TransportChargesMethodOfPayment] {

  override val code: String = method

  override def toString: String = description

  override val messageKeyPrefix: String = "transportMethodOfPayment"
}

object TransportChargesMethodOfPayment extends DynamicEnumerableType[TransportChargesMethodOfPayment] {

  def reads(config: FrontendAppConfig): Reads[TransportChargesMethodOfPayment] =
    if (config.isPhase6Enabled) {
      (
        (__ \ "key").read[String] and
          (__ \ "value").read[String]
      )(TransportChargesMethodOfPayment.apply)
    } else {
      Json.reads[TransportChargesMethodOfPayment]
    }

  implicit val format: Format[TransportChargesMethodOfPayment] = Json.format[TransportChargesMethodOfPayment]

  implicit val order: Order[TransportChargesMethodOfPayment] = (x: TransportChargesMethodOfPayment, y: TransportChargesMethodOfPayment) =>
    (x, y).compareBy(_.method)
}
