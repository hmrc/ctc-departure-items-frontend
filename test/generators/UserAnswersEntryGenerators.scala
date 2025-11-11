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

import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.external.ConsignmentAddDocumentsPage
import play.api.libs.json._
import queries.Gettable

import java.util.UUID

// scalastyle:off number.of.methods
// scalastyle:off cyclomatic.complexity
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[?], Gen[JsValue]] =
    generateExternalAnswer orElse
      generateItemAnswer

  private def generateExternalAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.external._
    {
      case CustomsOfficeOfDeparturePage               => Gen.alphaNumStr.map(JsString.apply)
      case CustomsOfficeOfDepartureInCL112Page        => arbitrary[Boolean].map(JsBoolean)
      case TransitOperationDeclarationTypePage        => arbitrary[String](arbitraryConsignmentDeclarationType).map(Json.toJson(_))
      case TransitOperationTIRCarnetNumberPage        => Gen.alphaNumStr.map(JsString.apply)
      case ConsignmentUCRPage                         => Gen.alphaNumStr.map(JsString.apply)
      case ConsignmentCountryOfDispatchPage           => arbitrary[Country].map(Json.toJson(_))
      case ConsignmentCountryOfDestinationPage        => arbitrary[Country].map(Json.toJson(_))
      case ApprovedOperatorPage                       => arbitrary[Boolean].map(JsBoolean)
      case SecurityDetailsTypePage                    => arbitrary[String](arbitrarySecurityDetailsType).map(Json.toJson(_))
      case MoreThanOneConsigneePage                   => arbitrary[JsObject]
      case ConsignmentCountryOfDestinationInCL009Page => arbitrary[Boolean].map(JsBoolean)
      case AddConsignmentTransportChargesYesNoPage    => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateItemAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.*
    val pf: PartialFunction[Gettable[?], Gen[JsValue]] = {
      case DescriptionPage(_)                      => Gen.alphaNumStr.map(JsString.apply)
      case DeclarationTypePage(_)                  => arbitrary[DeclarationTypeItemLevel].map(Json.toJson(_))
      case CountryOfDispatchPage(_)                => arbitrary[Country].map(Json.toJson(_))
      case CountryOfDestinationPage(_)             => arbitrary[Country].map(Json.toJson(_))
      case AddCombinedNomenclatureCodeYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case CombinedNomenclatureCodePage(_)         => Gen.alphaNumStr.map(JsString.apply)
      case AddCommodityCodeYesNoPage(_)            => arbitrary[Boolean].map(JsBoolean)
      case CommodityCodePage(_)                    => Gen.alphaNumStr.map(JsString.apply)
      case AddCUSCodeYesNoPage(_)                  => arbitrary[Boolean].map(JsBoolean)
      case CustomsUnionAndStatisticsCodePage(_)    => Gen.alphaNumStr.map(JsString.apply)
      case AddUCRYesNoPage(_)                      => arbitrary[Boolean].map(JsBoolean)
      case UniqueConsignmentReferencePage(_)       => Gen.alphaNumStr.map(JsString.apply)
      case AddDangerousGoodsYesNoPage(_)           => arbitrary[Boolean].map(JsBoolean)
      case GrossWeightBeforeYouContinuePage(_)     => arbitrary[Boolean].map(Json.toJson(_))
      case GrossWeightPage(_)                      => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddItemNetWeightYesNoPage(_)            => arbitrary[Boolean].map(JsBoolean)
      case NetWeightPage(_)                        => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddSupplementaryUnitsYesNoPage(_)       => arbitrary[Boolean].map(JsBoolean)
      case SupplementaryUnitsPage(_)               => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddSupplyChainActorYesNoPage(_)         => arbitrary[Boolean].map(JsBoolean)
      case AddDocumentsYesNoPage(_)                => arbitrary[Boolean].map(JsBoolean)
      case InferredAddDocumentsYesNoPage(_)        => arbitrary[Boolean].map(JsBoolean)
      case ConsignmentAddDocumentsPage             => arbitrary[Boolean].map(JsBoolean)
      case AddAdditionalReferenceYesNoPage(_)      => arbitrary[Boolean].map(JsBoolean)
      case AddAdditionalInformationYesNoPage(_)    => arbitrary[Boolean].map(JsBoolean)
      case AddTransportChargesYesNoPage(_)         => arbitrary[Boolean].map(JsBoolean)
      case TransportChargesMethodOfPaymentPage(_)  => arbitrary[TransportChargesMethodOfPayment].map(Json.toJson(_))
    }
    pf orElse
      generateDangerousGoodsAnswer orElse
      generatePackageAnswer orElse
      generateConsigneeAnswer orElse
      generateSupplyChainActorAnswer orElse
      generateDocumentsAnswer orElse
      generateAdditionalReferenceAnswer orElse
      generateAdditionalInformationAnswer orElse
      generateSupplyChainActorAnswers
  }

  private def generateDangerousGoodsAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.dangerousGoods.index._
    {
      case UNNumberPage(_, _) => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generatePackageAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.packages.index._
    {
      case PackageTypePage(_, _)          => arbitrary[PackageType].map(Json.toJson(_))
      case NumberOfPackagesPage(_, _)     => Gen.posNum[Int].map(Json.toJson(_))
      case AddShippingMarkYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case ShippingMarkPage(_, _)         => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateConsigneeAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.consignee._
    {
      case AddConsigneeEoriNumberYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case IdentificationNumberPage(_)        => Gen.alphaNumStr.map(JsString.apply)
      case NamePage(_)                        => Gen.alphaNumStr.map(JsString.apply)
      case CountryPage(_)                     => arbitrary[Country].map(Json.toJson(_))
      case AddressPage(_)                     => arbitrary[DynamicAddress].map(Json.toJson(_))
    }
  }

  private def generateSupplyChainActorAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.supplyChainActors.index._
    {
      case SupplyChainActorTypePage(_, _) => arbitrary[SupplyChainActorType].map(Json.toJson(_))
      case IdentificationNumberPage(_, _) => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateDocumentsAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.documents._
    val pf: PartialFunction[Gettable[?], Gen[JsValue]] = {
      case AddAnotherDocumentPage(_) => arbitrary[Boolean].map(JsBoolean)
    }
    pf orElse generateDocumentAnswer
  }

  private def generateDocumentAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.documents.index._
    {
      case DocumentPage(_, _) => arbitrary[UUID].map(Json.toJson(_))
    }
  }

  private def generateAdditionalReferenceAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.additionalReference.index._
    {
      case AdditionalReferencePage(_, _)               => arbitrary[AdditionalReference].map(Json.toJson(_))
      case AddAdditionalReferenceNumberYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case AdditionalReferenceNumberPage(_, _)         => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateAdditionalInformationAnswer: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.additionalInformation.index._
    {
      case AdditionalInformationTypePage(_, _) => arbitrary[AdditionalInformation].map(Json.toJson(_))
      case AdditionalInformationPage(_, _)     => Gen.alphaNumStr.map(JsString.apply)
    }
  }

  private def generateSupplyChainActorAnswers: PartialFunction[Gettable[?], Gen[JsValue]] = {
    import pages.item.supplyChainActors.index._
    {
      case SupplyChainActorTypePage(_, _) => arbitrary[SupplyChainActorType].map(Json.toJson(_))
      case IdentificationNumberPage(_, _) => Gen.alphaNumStr.map(JsString.apply)
    }
  }

}
// scalastyle:on number.of.methods
// scalastyle:on cyclomatic.complexity
