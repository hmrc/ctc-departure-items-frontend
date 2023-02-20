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
import models.UnLocodeList
import models.reference.UnLocode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UnLocodesServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new UnLocodesService(mockRefDataConnector)

  val unLocode1: UnLocode = UnLocode("ADALV", "Andorra la Vella")
  val unLocode2: UnLocode = UnLocode("ADCAN", "Canillo")
  val unLocodes           = Seq(unLocode1, unLocode2)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "UnLocodesService" - {

    "getUnLocodes" - {
      "must return a list of sorted unLocodes" in {

        when(mockRefDataConnector.getUnLocodes()(any(), any()))
          .thenReturn(Future.successful(unLocodes))

        service.getUnLocodes().futureValue mustBe
          UnLocodeList(Seq(unLocode1, unLocode2))

        verify(mockRefDataConnector).getUnLocodes()(any(), any())
      }
    }
  }
}
