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
import connectors.ReferenceDataConnector
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.reference.HSCode
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HSCodeServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val service                                      = new HSCodeService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "HSCodeService" - {

    val code = "010121"

    "must return true when HSCode exists" in {
      when(mockRefDataConnector.getHSCode(anyString())(any(), any()))
        .thenReturn(Future.successful(Right(HSCode(code))))

      service.doesHSCodeExist(code).futureValue mustEqual true
      verify(mockRefDataConnector).getHSCode(ArgumentMatchers.eq(code))(any(), any())
    }

    "must return false when HSCode does not exist in reference data" in {
      when(mockRefDataConnector.getHSCode(anyString())(any(), any()))
        .thenReturn(Future.successful(Left(new NoReferenceDataFoundException(""))))

      service.doesHSCodeExist(code).futureValue mustEqual false
      verify(mockRefDataConnector).getHSCode(ArgumentMatchers.eq(code))(any(), any())
    }
  }
}
