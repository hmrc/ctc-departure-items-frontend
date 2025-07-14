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
import models.reference.PackageType
import models.{PackingType, SelectableList}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PackagesServiceSpec extends SpecBase {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val service = new PackagesService(mockRefDataConnector)

  "PackagesService" - {

    "getPackageTypes" - {
      "must fetch package types and combine results" in {
        val other    = PackageType("MW", "Receptacle, plastic wrapped", PackingType.Other)
        val bulk     = PackageType("VL", "Bulk, liquid", PackingType.Bulk)
        val unpacked = PackageType("NE", "Unpacked or unpackaged", PackingType.Unpacked)

        when(mockRefDataConnector.getPackageTypes()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(other))))

        when(mockRefDataConnector.getPackageTypesBulk()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(bulk))))

        when(mockRefDataConnector.getPackageTypesUnpacked()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(unpacked))))

        val result = service.getPackageTypes().futureValue

        result mustEqual SelectableList(Seq(other, unpacked, bulk))
      }

      "must ensure Other packing type not given preference when there are duplicates" in {
        val other1   = PackageType("MW", "Receptacle, plastic wrapped", PackingType.Other)
        val other2   = PackageType("VL", "Bulk, liquid", PackingType.Other)
        val other3   = PackageType("NE", "Unpacked or unpackaged", PackingType.Other)
        val bulk     = PackageType("VL", "Bulk, liquid", PackingType.Bulk)
        val unpacked = PackageType("NE", "Unpacked or unpackaged", PackingType.Unpacked)

        when(mockRefDataConnector.getPackageTypes()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(other1, other2, other3))))

        when(mockRefDataConnector.getPackageTypesBulk()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(bulk))))

        when(mockRefDataConnector.getPackageTypesUnpacked()(any(), any()))
          .thenReturn(Future.successful(Right(NonEmptySet.of(unpacked))))

        val result = service.getPackageTypes().futureValue

        result mustEqual SelectableList(Seq(other1, unpacked, bulk))
      }
    }
  }
}
