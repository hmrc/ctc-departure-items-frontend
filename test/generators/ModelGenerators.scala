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

package generators

import config.Constants.AdditionalInformation.*
import config.Constants.AdditionalReference.*
import config.Constants.DeclarationType.*
import models.*
import models.AddressLine.{Country as _, *}
import models.DocumentType.{Previous, Support, Transport}
import models.LockCheck.{LockCheckFailure, Locked, Unlocked}
import models.reference.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.*

import java.util.UUID

trait ModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryMethodOfPayment: Arbitrary[TransportChargesMethodOfPayment] =
    Arbitrary {
      for {
        code        <- nonEmptyString
        description <- nonEmptyString
      } yield TransportChargesMethodOfPayment(code, description)
    }

  implicit lazy val arbitrarySupplyChainActorType: Arbitrary[SupplyChainActorType] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("CS", "FW", "MF", "WH")
        description <- nonEmptyString
      } yield SupplyChainActorType(code, description)
    }

  implicit lazy val arbitraryDocumentType: Arbitrary[DocumentType] =
    Arbitrary {
      Gen.oneOf(DocumentType.values)
    }

  implicit lazy val arbitraryDeclarationTypeItemLevel: Arbitrary[DeclarationTypeItemLevel] =
    Arbitrary {
      for {
        code        <- Gen.oneOf(T1, T2, T2F)
        description <- nonEmptyString
      } yield DeclarationTypeItemLevel(
        code = code,
        description = description
      )
    }

  lazy val arbitraryT2OrT2FDeclarationType: Arbitrary[DeclarationTypeItemLevel] =
    Arbitrary {
      for {
        code        <- Gen.oneOf(T2, T2F)
        description <- nonEmptyString
      } yield DeclarationTypeItemLevel(
        code = code,
        description = description
      )
    }

  lazy val arbitraryT1DeclarationType: Arbitrary[DeclarationTypeItemLevel] =
    Arbitrary {
      for {
        code        <- Gen.const(T1)
        description <- nonEmptyString
      } yield DeclarationTypeItemLevel(
        code = code,
        description = description
      )
    }

  lazy val arbitraryConsignmentDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T", "T1", "T2", "T2F", "TIR")
    }

  lazy val arbitraryNonTConsignmentDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T1", "T2", "T2F", "TIR")
    }

  lazy val arbitraryNonTDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T1", "T2", "T2F", "TIR")
    }

  lazy val arbitraryNonTIRDeclarationType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("T", "T1", "T2", "T2F")
    }

  implicit lazy val arbitraryTransportEquipment: Arbitrary[TransportEquipment] =
    Arbitrary {
      for {
        number      <- positiveIntsMinMax(0: Int, 9998: Int)
        containerId <- Gen.option(nonEmptyString)
        uuid        <- arbitrary[UUID]
      } yield TransportEquipment(number, containerId, uuid)
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen
        .pick(2, 'A' to 'Z')
        .map(
          code => CountryCode(code.mkString)
        )
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- nonEmptyString
      } yield Country(code, name)
    }

  implicit lazy val arbitraryCUSCode: Arbitrary[CUSCode] =
    Arbitrary {
      for {
        code <- nonEmptyString
      } yield CUSCode(code)
    }

  implicit lazy val arbitraryHSCode: Arbitrary[HSCode] =
    Arbitrary {
      for {
        code <- nonEmptyString
      } yield HSCode(code)
    }

  implicit lazy val arbitraryDocument: Arbitrary[Document] =
    Arbitrary {
      for {
        attachToAllItems <- arbitrary[Boolean]
        documentType     <- arbitrary[DocumentType]
        code             <- nonEmptyString
        description      <- Gen.option(nonEmptyString)
        referenceNumber  <- nonEmptyString
        uuid             <- arbitrary[UUID]
      } yield Document(attachToAllItems, documentType, code, description, referenceNumber, uuid)
    }

  lazy val arbitrarySupportingDocument: Arbitrary[Document] =
    Arbitrary {
      for {
        attachToAllItems <- arbitrary[Boolean]
        code             <- nonEmptyString
        description      <- Gen.option(nonEmptyString)
        referenceNumber  <- nonEmptyString
        uuid             <- arbitrary[UUID]
      } yield Document(attachToAllItems, Support, code, description, referenceNumber, uuid)
    }

  lazy val arbitraryTransportDocument: Arbitrary[Document] =
    Arbitrary {
      for {
        attachToAllItems <- arbitrary[Boolean]
        code             <- nonEmptyString
        description      <- Gen.option(nonEmptyString)
        referenceNumber  <- nonEmptyString
        uuid             <- arbitrary[UUID]
      } yield Document(attachToAllItems, Transport, code, description, referenceNumber, uuid)
    }

  lazy val arbitraryPreviousDocument: Arbitrary[Document] =
    Arbitrary {
      for {
        attachToAllItems <- arbitrary[Boolean]
        code             <- nonEmptyString
        description      <- Gen.option(nonEmptyString)
        referenceNumber  <- nonEmptyString
        uuid             <- arbitrary[UUID]
      } yield Document(attachToAllItems, Previous, code, description, referenceNumber, uuid)
    }

  implicit lazy val arbitraryLocalReferenceNumber: Arbitrary[LocalReferenceNumber] =
    Arbitrary {
      for {
        lrn <- stringsWithMaxLength(22: Int, Gen.alphaNumChar)
      } yield new LocalReferenceNumber(lrn)
    }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(17: Int)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryDynamicAddress: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- Gen.option(stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar))
      } yield DynamicAddress(numberAndStreet, city, postalCode)
    }

  lazy val arbitraryDynamicAddressWithRequiredPostalCode: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar)
      } yield DynamicAddress(numberAndStreet, city, Some(postalCode))
    }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit def arbitrarySelectableList[T <: Selectable](implicit arbitrary: Arbitrary[T]): Arbitrary[SelectableList[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield SelectableList(values.distinctBy(_.value))
  }

  implicit def arbitraryRadioableList[T <: Radioable[T]](implicit arbitrary: Arbitrary[T]): Arbitrary[Seq[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield values.distinctBy(_.code)
  }

  implicit lazy val arbitraryIndex: Arbitrary[Index] = Arbitrary {
    for {
      position <- Gen.choose(0: Int, 10: Int)
    } yield Index(position)
  }

  implicit lazy val arbitraryCall: Arbitrary[Call] = Arbitrary {
    for {
      method <- Gen.oneOf(GET, POST)
      url    <- nonEmptyString
    } yield Call(method, url)
  }

  implicit lazy val arbitraryPackingType: Arbitrary[PackingType] = Arbitrary {
    Gen.oneOf(PackingType.values)
  }

  implicit lazy val arbitraryPackageType: Arbitrary[PackageType] = Arbitrary {
    for {
      packageType <- arbitrary[PackingType]
      value       <- arbitrary[PackageType](arbitraryPackageType(packageType))
    } yield value
  }

  private def arbitraryPackageType(packingType: PackingType): Arbitrary[PackageType] =
    Arbitrary {
      for {
        code <- nonEmptyString
        desc <- nonEmptyString
      } yield PackageType(code, desc, packingType)
    }

  lazy val arbitraryUnpackedPackageType: Arbitrary[PackageType] = arbitraryPackageType(PackingType.Unpacked)
  lazy val arbitraryBulkPackageType: Arbitrary[PackageType]     = arbitraryPackageType(PackingType.Bulk)
  lazy val arbitraryOtherPackageType: Arbitrary[PackageType]    = arbitraryPackageType(PackingType.Other)

  implicit lazy val arbitraryAdditionalReference: Arbitrary[AdditionalReference] =
    Arbitrary {
      for {
        documentType <- nonEmptyString
        description  <- nonEmptyString
      } yield AdditionalReference(documentType, description)
    }

  val arbitraryC651OrC658AdditionalReference: Arbitrary[AdditionalReference] =
    Arbitrary {
      for {
        documentType <- Gen.oneOf(C651, C658)
        description  <- nonEmptyString
      } yield AdditionalReference(documentType, description)
    }

  val arbitraryNonC651OrC658AdditionalReference: Arbitrary[AdditionalReference] = arbitraryAdditionalReference

  implicit lazy val arbitraryAdditionalInformation: Arbitrary[AdditionalInformation] =
    Arbitrary {
      for {
        code        <- nonEmptyString
        description <- nonEmptyString
      } yield AdditionalInformation(code, description)
    }

  val arbitraryAdditionalInformation30600: Arbitrary[AdditionalInformation] =
    Arbitrary {
      for {
        description <- nonEmptyString
      } yield AdditionalInformation(Type30600, description)
    }

  val arbitraryAdditionalInformationNon30600: Arbitrary[AdditionalInformation] = arbitraryAdditionalInformation

  lazy val arbitraryIncompleteTaskStatus: Arbitrary[TaskStatus] = Arbitrary {
    Gen.oneOf(TaskStatus.InProgress, TaskStatus.NotStarted, TaskStatus.CannotStartYet)
  }

  lazy val arbitrarySecurityDetailsType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("0", "1", "2", "3")
    }

  lazy val arbitrarySomeSecurityDetailsType: Arbitrary[String] =
    Arbitrary {
      Gen.oneOf("1", "2", "3")
    }

  implicit lazy val arbitraryJsObject: Arbitrary[JsObject] = Arbitrary {
    Gen.oneOf(
      Json.obj(),
      Json.obj("foo" -> "bar")
    )
  }

  implicit lazy val arbitraryLockCheck: Arbitrary[LockCheck] =
    Arbitrary {
      Gen.oneOf(Locked, Unlocked, LockCheckFailure)
    }

  lazy val arbitraryAmendedmentSubmissionState: Arbitrary[SubmissionState] = Arbitrary {
    val values = Seq(
      SubmissionState.GuaranteeAmendment,
      SubmissionState.RejectedPendingChanges,
      SubmissionState.Amendment
    )
    Gen.oneOf(values)
  }

  implicit lazy val arbitraryNonAmendmentSubmissionState: Arbitrary[SubmissionState] = Arbitrary {
    val values = Seq(
      SubmissionState.NotSubmitted,
      SubmissionState.Submitted
    )
    Gen.oneOf(values)
  }

}
