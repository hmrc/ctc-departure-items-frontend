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

import base.SpecBase
import cats.data.NonEmptySet
import connectors.ReferenceDataConnector
import models.reference.SupplyChainActorType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SupplyChainActorTypesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new SupplyChainActorTypesService(mockRefDataConnector)

  private val supplyChainActorType1 = SupplyChainActorType("WH", "Warehouse Keeper")
  private val supplyChainActorType2 = SupplyChainActorType("MF", "Manufacturer")
  private val supplyChainActorType3 = SupplyChainActorType("FW", "Freight Forwarder")
  private val supplyChainActorType4 = SupplyChainActorType("CS", "Consolidator")

  private val supplyChainActorTypes =
    NonEmptySet.of(supplyChainActorType1, supplyChainActorType2, supplyChainActorType3, supplyChainActorType4)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "TransportModeCodesService" - {

    "getTransportModeCodes" - {
      "must return a list of sorted inland modes" in {
        when(mockRefDataConnector.getSupplyChainActorTypes()(any(), any()))
          .thenReturn(Future.successful(supplyChainActorTypes))

        service.getSupplyChainActorTypes().futureValue mustBe
          Seq(supplyChainActorType4, supplyChainActorType3, supplyChainActorType2, supplyChainActorType1)

        verify(mockRefDataConnector).getSupplyChainActorTypes()(any(), any())
      }
    }
  }
}
