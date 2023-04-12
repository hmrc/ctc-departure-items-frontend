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

package models.journeyDomain.item

import cats.implicits._
import models.DeclarationType._
import models.journeyDomain.item.dangerousGoods.DangerousGoodsListDomain
import models.journeyDomain.item.packages.PackagesDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DeclarationType, Index}
import pages.external._
import pages.item._

import scala.language.implicitConversions

case class ItemDomain(
  itemDescription: String,
  declarationType: Option[DeclarationType],
  countryOfDispatch: Option[Country],
  countryOfDestination: Option[Country],
  ucr: Option[String],
  cusCode: Option[String],
  commodityCode: Option[String],
  combinedNomenclatureCode: Option[String],
  dangerousGoods: Option[DangerousGoodsListDomain],
  grossWeight: BigDecimal,
  netWeight: Option[BigDecimal],
  supplementaryUnits: Option[BigDecimal],
  packages: PackagesDomain
)(index: Index)
    extends JourneyDomainModel

object ItemDomain {

  implicit def userAnswersReader(itemIndex: Index): UserAnswersReader[ItemDomain] =
    (
      DescriptionPage(itemIndex).reader,
      declarationTypeReader(itemIndex),
      countryOfDispatchReader(itemIndex),
      countryOfDestinationReader(itemIndex),
      ucrReader(itemIndex),
      cusCodeReader(itemIndex),
      commodityCodeReader(itemIndex),
      combinedNomenclatureCodeReader(itemIndex),
      dangerousGoodsReader(itemIndex),
      GrossWeightPage(itemIndex).reader,
      netWeightReader(itemIndex),
      supplementaryUnitsReader(itemIndex),
      packagesReader(itemIndex)
    ).tupled.map((ItemDomain.apply _).tupled).map(_(itemIndex))

  def declarationTypeReader(itemIndex: Index): UserAnswersReader[Option[DeclarationType]] =
    TransitOperationDeclarationTypePage.filterOptionalDependent(_ == T) {
      DeclarationTypePage(itemIndex).reader
    }

  def countryOfDispatchReader(itemIndex: Index): UserAnswersReader[Option[Country]] =
    TransitOperationDeclarationTypePage
      .filterOptionalDependent(_ == TIR) {
        ConsignmentCountryOfDispatchPage.filterDependent(_.isEmpty) {
          CountryOfDispatchPage(itemIndex).reader
        }
      }
      .map(_.flatten)

  def countryOfDestinationReader(itemIndex: Index): UserAnswersReader[Option[Country]] =
    ConsignmentCountryOfDestinationPage.filterDependent(_.isEmpty) {
      CountryOfDestinationPage(itemIndex).reader
    }

  // TODO - will need updating once documents has been built
  def ucrReader(itemIndex: Index): UserAnswersReader[Option[String]] =
    ConsignmentUCRPage.filterDependent(_.isEmpty) {
      UniqueConsignmentReferencePage(itemIndex).reader
    }

  def cusCodeReader(itemIndex: Index): UserAnswersReader[Option[String]] =
    AddCUSCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
      CustomsUnionAndStatisticsCodePage(itemIndex).reader
    }

  def commodityCodeReader(itemIndex: Index): UserAnswersReader[Option[String]] =
    TransitOperationTIRCarnetNumberPage.isDefined.flatMap {
      case true =>
        AddCommodityCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
          CommodityCodePage(itemIndex).reader
        }
      case false =>
        CommodityCodePage(itemIndex).reader.map(Some(_))
    }

  def combinedNomenclatureCodeReader(itemIndex: Index): UserAnswersReader[Option[String]] =
    CommodityCodePage(itemIndex).isDefined.flatMap {
      case true =>
        CustomsOfficeOfDepartureInCL112Page
          .filterOptionalDependent(!_) {
            AddCombinedNomenclatureCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
              CombinedNomenclatureCodePage(itemIndex).reader
            }
          }
          .map(_.flatten)
      case false =>
        none[String].pure[UserAnswersReader]
    }

  def dangerousGoodsReader(itemIndex: Index): UserAnswersReader[Option[DangerousGoodsListDomain]] =
    AddDangerousGoodsYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(DangerousGoodsListDomain.userAnswersReader(itemIndex))

  def netWeightReader(itemIndex: Index): UserAnswersReader[Option[BigDecimal]] =
    ApprovedOperatorPage.optionalReader.flatMap {
      case Some(true) =>
        none[BigDecimal].pure[UserAnswersReader]
      case _ =>
        AddItemNetWeightYesNoPage(itemIndex).filterOptionalDependent(identity) {
          NetWeightPage(itemIndex).reader
        }
    }

  def supplementaryUnitsReader(itemIndex: Index): UserAnswersReader[Option[BigDecimal]] =
    AddSupplementaryUnitsYesNoPage(itemIndex).filterOptionalDependent(identity) {
      SupplementaryUnitsPage(itemIndex).reader
    }

  def packagesReader(itemIndex: Index): UserAnswersReader[PackagesDomain] =
    PackagesDomain.userAnswersReader(itemIndex)
}
