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
import config.Constants.GB
import config.PhaseConfig
import models.DeclarationType._
import models.DocumentType.Previous
import models.journeyDomain.item.additionalInformation.AdditionalInformationListDomain
import models.journeyDomain.item.additionalReferences.AdditionalReferencesDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsListDomain
import models.journeyDomain.item.documents.DocumentsDomain
import models.journeyDomain.item.packages.PackagesDomain
import models.journeyDomain.item.supplyChainActors.SupplyChainActorsDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, JsArrayGettableAsReaderOps, Stage, UserAnswersReader}
import models.reference.Country
import models._
import pages.external._
import pages.item._
import pages.sections.external.{DocumentsSection, TransportEquipmentsSection}
import play.api.i18n.Messages
import play.api.mvc.Call

import java.util.UUID
import scala.language.implicitConversions

case class ItemDomain(
  itemDescription: String,
  transportEquipment: Option[UUID],
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
  packages: PackagesDomain,
  supplyChainActors: Option[SupplyChainActorsDomain],
  documents: Option[DocumentsDomain],
  additionalReferences: Option[AdditionalReferencesDomain],
  additionalInformation: Option[AdditionalInformationListDomain]
)(index: Index)
    extends JourneyDomainModel {

  def label(implicit messages: Messages): String = messages("item.label", index.display, itemDescription)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some(
    controllers.item.routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, index)
  )
}

object ItemDomain {

  implicit def userAnswersReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[ItemDomain] =
    (
      DescriptionPage(itemIndex).reader,
      transportEquipmentReader(itemIndex),
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
      packagesReader(itemIndex),
      supplyChainActorsReader(itemIndex),
      documentsReader(itemIndex),
      additionalReferencesReader(itemIndex),
      additionalInformationListReader(itemIndex)
    ).tupled.map((ItemDomain.apply _).tupled).map(_(itemIndex))

  def transportEquipmentReader(itemIndex: Index): UserAnswersReader[Option[UUID]] =
    TransportEquipmentsSection.optionalReader.flatMap {
      case Some(array) if array.nonEmpty =>
        val reader = InferredTransportEquipmentPage(itemIndex).reader orElse TransportEquipmentPage(itemIndex).reader
        reader.map(Some(_))
      case _ =>
        none[UUID].pure[UserAnswersReader]
    }

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

  def netWeightReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[BigDecimal]] =
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

  def packagesReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[PackagesDomain] =
    PackagesDomain.userAnswersReader(itemIndex)

  def supplyChainActorsReader(itemIndex: Index): UserAnswersReader[Option[SupplyChainActorsDomain]] =
    AddSupplyChainActorYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(SupplyChainActorsDomain.userAnswersReader(itemIndex))

  def documentsReader(itemIndex: Index): UserAnswersReader[Option[DocumentsDomain]] =
    for {
      transitOperationDeclarationType <- TransitOperationDeclarationTypePage.reader
      isGBOfficeOfDeparture           <- CustomsOfficeOfDeparturePage.reader.map(_.startsWith(GB))
      itemDeclarationType             <- DeclarationTypePage(itemIndex).optionalReader
      isT2OrT2FItemDeclarationType = itemDeclarationType.exists(_.isOneOf(T2, T2F))
      documents <- DocumentsSection.arrayReader.map(_.validateAsListOf[Document])
      consignmentLevelPreviousDocumentPresent = documents.exists(
        x => x.attachToAllItems && x.`type` == Previous
      )
      reader <- (transitOperationDeclarationType, isGBOfficeOfDeparture, isT2OrT2FItemDeclarationType, consignmentLevelPreviousDocumentPresent) match {
        case (T, true, true, true) =>
          AddDocumentsYesNoPage(itemIndex).filterOptionalDependent(identity)(DocumentsDomain.userAnswersReader(itemIndex))
        case _ =>
          DocumentsDomain.userAnswersReader(itemIndex).map(Some(_))
      }
    } yield reader

  def additionalReferencesReader(itemIndex: Index): UserAnswersReader[Option[AdditionalReferencesDomain]] =
    AddAdditionalReferenceYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(AdditionalReferencesDomain.userAnswersReader(itemIndex))

  def additionalInformationListReader(itemIndex: Index): UserAnswersReader[Option[AdditionalInformationListDomain]] =
    AddAdditionalInformationYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(AdditionalInformationListDomain.userAnswersReader(itemIndex))
}
