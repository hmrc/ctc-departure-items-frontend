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

package generators

import config.Constants._
import models.AddressLine.{Country => _, _}
import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs._

trait ModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values)
    }

  lazy val arbitraryNonTDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values.filterNot(_ == DeclarationType.T))
    }

  lazy val arbitraryNonTIRDeclarationType: Arbitrary[DeclarationType] =
    Arbitrary {
      Gen.oneOf(DeclarationType.values.filterNot(_ == DeclarationType.TIR))
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen
        .pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z')
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

  implicit lazy val arbitraryDocument: Arbitrary[Document] =
    Arbitrary {
      for {
        documentType    <- nonEmptyString
        code            <- nonEmptyString
        description     <- Gen.option(nonEmptyString)
        referenceNumber <- nonEmptyString
      } yield Document(documentType, code, description, referenceNumber)
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
        desc <- Gen.option(nonEmptyString)
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

  val arbitraryNonC651OrC658AdditionalReference: Arbitrary[AdditionalReference] =
    Arbitrary {
      for {
        documentType <- nonEmptyString
        description  <- nonEmptyString
      } yield AdditionalReference(documentType, description)
    }

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
      } yield AdditionalInformation(additionalInformationType30600, description)
    }

  val arbitraryAdditionalInformationNon30600: Arbitrary[AdditionalInformation] = arbitraryAdditionalInformation

  lazy val arbitraryIncompleteTaskStatus: Arbitrary[TaskStatus] = Arbitrary {
    Gen.oneOf(TaskStatus.InProgress, TaskStatus.NotStarted, TaskStatus.CannotStartYet)
  }

}
