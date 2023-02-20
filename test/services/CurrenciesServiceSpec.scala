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

package services

import base.SpecBase
import connectors.ReferenceDataConnector
import generators.Generators
import models.CurrencyCodeList
import models.reference.CurrencyCode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CurrenciesServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new CurrenciesService(mockRefDataConnector)

  val currencyCode1: CurrencyCode      = CurrencyCode("CHF", Some("Swiss Franc"))
  val currencyCode2: CurrencyCode      = CurrencyCode("GBP", Some("Sterling"))
  val currencyCodes: Seq[CurrencyCode] = Seq(currencyCode1, currencyCode2)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CurrencyService" - {

    "getCurrencyCodes" - {
      "must return a list of sorted currency codes" in {

        when(mockRefDataConnector.getCurrencyCodes()(any(), any()))
          .thenReturn(Future.successful(currencyCodes))

        service.getCurrencyCodes().futureValue mustBe
          CurrencyCodeList(Seq(currencyCode1, currencyCode2))

        verify(mockRefDataConnector).getCurrencyCodes()(any(), any())
      }
    }
  }
}
