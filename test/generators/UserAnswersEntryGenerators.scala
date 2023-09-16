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

import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

import java.util.UUID

// scalastyle:off number.of.methods
// scalastyle:off cyclomatic.complexity
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateExternalAnswer orElse
      generateItemAnswer

  private def generateExternalAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.external._
    import pages.sections.external._
    {
      case CustomsOfficeOfDeparturePage               => Gen.alphaNumStr.map(JsString)
      case CustomsOfficeOfDepartureInCL112Page        => arbitrary[Boolean].map(JsBoolean)
      case TransitOperationDeclarationTypePage        => arbitrary[DeclarationType].map(Json.toJson(_))
      case TransitOperationTIRCarnetNumberPage        => Gen.alphaNumStr.map(JsString)
      case ConsignmentUCRPage                         => Gen.alphaNumStr.map(JsString)
      case ConsignmentCountryOfDispatchPage           => arbitrary[Country].map(Json.toJson(_))
      case ConsignmentCountryOfDestinationPage        => arbitrary[Country].map(Json.toJson(_))
      case ApprovedOperatorPage                       => arbitrary[Boolean].map(JsBoolean)
      case SecurityDetailsTypePage                    => arbitrary[SecurityDetailsType].map(Json.toJson(_))
      case ConsignmentConsigneeSection                => arbitrary[JsObject]
      case ConsignmentCountryOfDestinationInCL009Page => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateItemAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case DescriptionPage(_)                      => Gen.alphaNumStr.map(JsString)
      case DeclarationTypePage(_)                  => arbitrary[DeclarationTypeItemLevel].map(Json.toJson(_))
      case CountryOfDispatchPage(_)                => arbitrary[Country].map(Json.toJson(_))
      case CountryOfDestinationPage(_)             => arbitrary[Country].map(Json.toJson(_))
      case AddCombinedNomenclatureCodeYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case CombinedNomenclatureCodePage(_)         => Gen.alphaNumStr.map(JsString)
      case AddCommodityCodeYesNoPage(_)            => arbitrary[Boolean].map(JsBoolean)
      case CommodityCodePage(_)                    => Gen.alphaNumStr.map(JsString)
      case AddCUSCodeYesNoPage(_)                  => arbitrary[Boolean].map(JsBoolean)
      case CustomsUnionAndStatisticsCodePage(_)    => Gen.alphaNumStr.map(JsString)
      case AddUCRYesNoPage(_)                      => arbitrary[Boolean].map(JsBoolean)
      case UniqueConsignmentReferencePage(_)       => Gen.alphaNumStr.map(JsString)
      case AddDangerousGoodsYesNoPage(_)           => arbitrary[Boolean].map(JsBoolean)
      case GrossWeightPage(_)                      => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddItemNetWeightYesNoPage(_)            => arbitrary[Boolean].map(JsBoolean)
      case NetWeightPage(_)                        => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddSupplementaryUnitsYesNoPage(_)       => arbitrary[Boolean].map(JsBoolean)
      case SupplementaryUnitsPage(_)               => arbitrary[BigDecimal].map(Json.toJson(_))
      case AddSupplyChainActorYesNoPage(_)         => arbitrary[Boolean].map(JsBoolean)
      case AddDocumentsYesNoPage(_)                => arbitrary[Boolean].map(JsBoolean)
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

  private def generateDangerousGoodsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.dangerousGoods.index._
    {
      case UNNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generatePackageAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.packages.index._
    {
      case PackageTypePage(_, _)          => arbitrary[PackageType].map(Json.toJson(_))
      case NumberOfPackagesPage(_, _)     => Gen.posNum[Int].map(Json.toJson(_))
      case AddShippingMarkYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case ShippingMarkPage(_, _)         => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateConsigneeAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.consignee._
    {
      case AddConsigneeEoriNumberYesNoPage(_) => arbitrary[Boolean].map(JsBoolean)
      case IdentificationNumberPage(_)        => Gen.alphaNumStr.map(JsString)
      case NamePage(_)                        => Gen.alphaNumStr.map(JsString)
      case CountryPage(_)                     => arbitrary[Country].map(Json.toJson(_))
      case AddressPage(_)                     => arbitrary[DynamicAddress].map(Json.toJson(_))
    }
  }

  private def generateSupplyChainActorAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.supplyChainActors.index._
    {
      case SupplyChainActorTypePage(_, _) => arbitrary[SupplyChainActorType].map(Json.toJson(_))
      case IdentificationNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateDocumentsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.documents._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case DocumentsInProgressPage(_) => arbitrary[Boolean].map(JsBoolean)
    }
    pf orElse generateDocumentAnswer
  }

  private def generateDocumentAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.documents.index._
    {
      case DocumentPage(_, _) => arbitrary[UUID].map(Json.toJson(_))
    }
  }

  private def generateAdditionalReferenceAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.additionalReference.index._
    {
      case AdditionalReferencePage(_, _)               => arbitrary[AdditionalReference].map(Json.toJson(_))
      case AddAdditionalReferenceNumberYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case AdditionalReferenceNumberPage(_, _)         => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateAdditionalInformationAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.additionalInformation.index._
    {
      case AdditionalInformationTypePage(_, _) => arbitrary[AdditionalInformation].map(Json.toJson(_))
      case AdditionalInformationPage(_, _)     => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateSupplyChainActorAnswers: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.supplyChainActors.index._
    {
      case SupplyChainActorTypePage(_, _) => arbitrary[SupplyChainActorType].map(Json.toJson(_))
      case IdentificationNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

}
// scalastyle:on number.of.methods
// scalastyle:on cyclomatic.complexity
