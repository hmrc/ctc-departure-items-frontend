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

package pages.item.supplyChainActors.index

import models.reference.TransportChargesMethodOfPayment
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.item.{AddTransportChargesYesNoPage, TransportChargesMethodOfPaymentPage}

class AddTransportChargesYesNoPageSpec extends PageBehaviours {

  "AddUCRYesNoPage" - {

    beRetrievable[Boolean](AddTransportChargesYesNoPage(itemIndex))

    beSettable[Boolean](AddTransportChargesYesNoPage(itemIndex))

    beRemovable[Boolean](AddTransportChargesYesNoPage(itemIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove payment method" in {
          forAll(arbitrary[TransportChargesMethodOfPayment]) {
            paymentMethod =>
              val userAnswers = emptyUserAnswers
                .setValue(AddTransportChargesYesNoPage(itemIndex), true)
                .setValue(TransportChargesMethodOfPaymentPage(itemIndex), paymentMethod)

              val result = userAnswers.setValue(AddTransportChargesYesNoPage(itemIndex), false)

              result.get(TransportChargesMethodOfPaymentPage(itemIndex)) must not be defined
          }
        }
      }

      "when yes selected" - {
        "must do nothing" in {
          forAll(arbitrary[TransportChargesMethodOfPayment]) {
            paymentMethod =>
              val userAnswers = emptyUserAnswers
                .setValue(AddTransportChargesYesNoPage(itemIndex), true)
                .setValue(TransportChargesMethodOfPaymentPage(itemIndex), paymentMethod)

              val result = userAnswers.setValue(AddTransportChargesYesNoPage(itemIndex), true)

              result.get(TransportChargesMethodOfPaymentPage(itemIndex)) mustBe defined
          }
        }
      }
    }
  }

}
