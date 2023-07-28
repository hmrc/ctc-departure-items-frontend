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

import models.{EnumerableType, Radioable, WithName}

sealed trait PaymentMethod extends Radioable[PaymentMethod] {
  val code: String

  override val messageKeyPrefix: String = PaymentMethod.messageKeyPrefix
}

object PaymentMethod extends EnumerableType[PaymentMethod] {

  case object Cash extends WithName("cash") with PaymentMethod {
    override val code: String = "A"
  }

  case object CreditCard extends WithName("creditCard") with PaymentMethod {
    override val code: String = "B"
  }

  case object Cheque extends WithName("cheque") with PaymentMethod {
    override val code: String = "C"
  }

  case object ElectronicCreditTransfer extends WithName("electronicCreditTransfer") with PaymentMethod {
    override val code: String = "D"
  }

  case object AccountHolderWithCarrier extends WithName("accountHolderWithCarrier") with PaymentMethod {
    override val code: String = "H"
  }

  case object NotPrePaid extends WithName("notPrePaid") with PaymentMethod {
    override val code: String = "Y"
  }

  case object Other extends WithName("other") with PaymentMethod {
    override val code: String = "Z"
  }

  val messageKeyPrefix: String = "equipment.paymentMethod"

  val values: Seq[PaymentMethod] = Seq(
    Cash,
    CreditCard,
    Cheque,
    ElectronicCreditTransfer,
    AccountHolderWithCarrier,
    NotPrePaid,
    Other
  )
}
