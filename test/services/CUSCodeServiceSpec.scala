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
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.reference.CUSCode
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CUSCodeServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new CUSCodeService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "CUSCodeService" - {
    "must return true when CUSCode exists" in {

      val cusCodeItem = CUSCode("0010001-6")
      val cusCodeList = Seq(cusCodeItem)

      val cusCode = "0010001-6"

      when(mockRefDataConnector.getCUSCode(anyString())(any(), any()))
        .thenReturn(Future.successful(cusCodeList))

      service.doesCUSCodeExist(cusCode).futureValue mustBe true
      verify(mockRefDataConnector).getCUSCode(ArgumentMatchers.eq(cusCode))(any(), any())
    }

    "must return false when CUSCode does not exist" in {

      val cusCode = "0010001-6"

      when(mockRefDataConnector.getCUSCode(anyString())(any(), any()))
        .thenReturn(Future.successful(Nil))

      service.doesCUSCodeExist(cusCode).futureValue mustBe false
      verify(mockRefDataConnector).getCUSCode(ArgumentMatchers.eq(cusCode))(any(), any())
    }

    "must return false when CUSCode does not exist in reference data" in {

      val cusCode = "0010001-6"

      when(mockRefDataConnector.getCUSCode(anyString())(any(), any()))
        .thenReturn(Future.failed(new NoReferenceDataFoundException))

      service.doesCUSCodeExist(cusCode).futureValue mustBe false
      verify(mockRefDataConnector).getCUSCode(ArgumentMatchers.eq(cusCode))(any(), any())
    }
  }
}
