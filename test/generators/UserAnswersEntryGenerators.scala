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

import models.{DeclarationType, Document}
import models.reference.{Country, PackageType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

// scalastyle:off number.of.methods
// scalastyle:off cyclomatic.complexity
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateExternalAnswer orElse
      generateItemAnswer

  private def generateExternalAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.external._
    {
      case CustomsOfficeOfDepartureInCL112Page => arbitrary[Boolean].map(JsBoolean)
      case TransitOperationDeclarationTypePage => arbitrary[DeclarationType].map(Json.toJson(_))
      case TransitOperationTIRCarnetNumberPage => Gen.alphaNumStr.map(JsString)
      case ConsignmentUCRPage                  => Gen.alphaNumStr.map(JsString)
      case ConsignmentCountryOfDispatchPage    => arbitrary[Country].map(Json.toJson(_))
      case ConsignmentCountryOfDestinationPage => arbitrary[Country].map(Json.toJson(_))
      case ApprovedOperatorPage                => arbitrary[Boolean].map(JsBoolean)
    }
  }

  private def generateItemAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case DescriptionPage(_)                      => Gen.alphaNumStr.map(JsString)
      case DeclarationTypePage(_)                  => arbitrary[DeclarationType].map(Json.toJson(_))
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
      case AddDocumentsYesNoPage(_)                => arbitrary[Boolean].map(JsBoolean)
    }
    pf orElse
      generateDangerousGoodsAnswer orElse
      generatePackagesAnswer orElse
      generateDocumentsAnswer
  }

  private def generateDangerousGoodsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.dangerousGoods.index._
    {
      case UNNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generatePackagesAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.packages.index._
    {
      case PackageTypePage(_, _)          => arbitrary[PackageType].map(Json.toJson(_))
      case NumberOfPackagesPage(_, _)     => Gen.posNum[Int].map(Json.toJson(_))
      case AddShippingMarkYesNoPage(_, _) => arbitrary[Boolean].map(JsBoolean)
      case ShippingMarkPage(_, _)         => Gen.alphaNumStr.map(JsString)
    }
  }

  private def generateDocumentsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.documents.index._
    {
      case DocumentPage(_, _) => arbitrary[Document].map(Json.toJson(_))
    }
  }

}
// scalastyle:on number.of.methods
// scalastyle:on cyclomatic.complexity
