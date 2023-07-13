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

package models.item

import models.{EnumerableType, Radioable, WithName}

sealed trait TransportMethodOfPayment extends Radioable[TransportMethodOfPayment] {
  override val messageKeyPrefix: String = TransportMethodOfPayment.messageKeyPrefix
}

object TransportMethodOfPayment extends EnumerableType[TransportMethodOfPayment] {

  case object PaymentInCash extends WithName("payment in cash") with TransportMethodOfPayment
  case object Option2 extends WithName("option2") with TransportMethodOfPayment

  val messageKeyPrefix: String = "item.transportMethodOfPayment"

  val values: Seq[TransportMethodOfPayment] = Seq(
    PaymentInCash,
    Option2
  )
}
