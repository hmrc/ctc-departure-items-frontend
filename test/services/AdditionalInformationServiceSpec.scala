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
import models.SelectableList
import models.reference.AdditionalInformation
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdditionalInformationServiceSpec extends SpecBase with BeforeAndAfterEach with Generators {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new AdditionalInformationService(mockRefDataConnector)

  private val additionalInformation1: AdditionalInformation = AdditionalInformation(
    code = "20100",
    description = "Export from one EFTA country subject to restriction or export from the Union subject to restriction"
  )

  private val additionalInformation2: AdditionalInformation = AdditionalInformation(
    code = "20300",
    description = "Export"
  )

  private val additionalInformation3: AdditionalInformation = AdditionalInformation(
    code = "30600",
    description = "In EXS, where negotiable bills of lading 'to order blank endorsed' are concerned and the consignee particulars are unknown."
  )

  private val additionalInformationTypes = Seq(additionalInformation1, additionalInformation2, additionalInformation3)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "AdditionalInformation" - {

    "getAdditionalInformationTypes" - {
      "must return a list of sorted additional information types, filtering out code 30600" in {

        when(mockRefDataConnector.getAdditionalInformationTypes()(any(), any()))
          .thenReturn(Future.successful(additionalInformationTypes))

        service.getAdditionalInformationTypes().futureValue mustBe
          SelectableList(Seq(additionalInformation2, additionalInformation1))

        verify(mockRefDataConnector).getAdditionalInformationTypes()(any(), any())
      }
    }
  }
}
