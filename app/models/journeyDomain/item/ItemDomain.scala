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
import models.DocumentType.{Previous, Transport}
import models.Phase.{PostTransition, Transition}
import models.SecurityDetailsType.NoSecurityDetails
import models._
import models.journeyDomain.item.additionalInformation.AdditionalInformationListDomain
import models.journeyDomain.item.additionalReferences.AdditionalReferencesDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsListDomain
import models.journeyDomain.item.documents.DocumentsDomain
import models.journeyDomain.item.packages.PackagesDomain
import models.journeyDomain.item.supplyChainActors.SupplyChainActorsDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, JsArrayGettableAsReaderOps, Stage, UserAnswersReader}
import models.reference.{Country, TransportChargesMethodOfPayment}
import pages.external._
import pages.item._
import pages.sections.external.{ConsignmentConsigneeSection, DocumentsSection, TransportEquipmentsSection}
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
  consignee: Option[ConsigneeDomain],
  supplyChainActors: Option[SupplyChainActorsDomain],
  documents: Option[DocumentsDomain],
  additionalReferences: Option[AdditionalReferencesDomain],
  additionalInformation: Option[AdditionalInformationListDomain],
  transportCharges: Option[TransportChargesMethodOfPayment]
)(index: Index)
    extends JourneyDomainModel {

  def label(implicit messages: Messages): String = messages("item.label", index.display, itemDescription)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage, phase: Phase): Option[Call] = Some(
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
      consigneeReader(itemIndex),
      supplyChainActorsReader(itemIndex),
      documentsReader(itemIndex),
      additionalReferencesReader(itemIndex),
      additionalInformationListReader(itemIndex),
      transportChargesReader(itemIndex)
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

  def ucrReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[String]] =
    for {
      isUCRDefined <- ConsignmentUCRPage.isDefined
      documents    <- DocumentsSection.arrayReader.map(_.validateAsListOf[Document])
      isConsignmentTransportDocDefined = documents.exists(
        x => x.attachToAllItems && x.`type` == Transport
      )
      result <- {
        (isUCRDefined, isConsignmentTransportDocDefined, phaseConfig.phase) match {
          case (true, _, _) => UserAnswersReader(None)
          case (false, false, PostTransition) =>
            UniqueConsignmentReferencePage(itemIndex).reader.map(Some(_))
          case _ =>
            AddUCRYesNoPage(itemIndex).filterOptionalDependent(identity)(UniqueConsignmentReferencePage(itemIndex).reader)
        }
      }
    } yield result

  def cusCodeReader(itemIndex: Index): UserAnswersReader[Option[String]] =
    AddCUSCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
      CustomsUnionAndStatisticsCodePage(itemIndex).reader
    }

  def commodityCodeReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[String]] =
    for {
      isTransitOperationTIRDefined <- TransitOperationTIRCarnetNumberPage.isDefined
      result <- {
        (isTransitOperationTIRDefined, phaseConfig.phase) match {
          case (false, PostTransition) => CommodityCodePage(itemIndex).reader.map(Some(_))
          case _                       => AddCommodityCodeYesNoPage(itemIndex).filterOptionalDependent(identity)(CommodityCodePage(itemIndex).reader)
        }
      }
    } yield result

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

  def packagesReader(itemIndex: Index): UserAnswersReader[PackagesDomain] =
    PackagesDomain.userAnswersReader(itemIndex)

  def consigneeReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[ConsigneeDomain]] =
    phaseConfig.phase match {
      case Phase.Transition =>
        for {
          consignmentConsigneePresent <- ConsignmentConsigneeSection.isDefined
          countryOfDestinationInCL009 <- ConsignmentCountryOfDestinationInCL009Page.readerWithDefault(false)
          reader <- (consignmentConsigneePresent, countryOfDestinationInCL009) match {
            case (true, true) => none[ConsigneeDomain].pure[UserAnswersReader]
            case _            => ConsigneeDomain.userAnswersReader(itemIndex).map(Some(_))
          }
        } yield reader
      case Phase.PostTransition =>
        none[ConsigneeDomain].pure[UserAnswersReader]
    }

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

  def transportChargesReader(itemIndex: Index)(implicit phaseConfig: PhaseConfig): UserAnswersReader[Option[TransportChargesMethodOfPayment]] =
    for {
      securityDetails                      <- SecurityDetailsTypePage.reader
      isConsignmentTransportChargesDefined <- ConsignmentTransportChargesPage.isDefined
      result <- {
        (securityDetails, isConsignmentTransportChargesDefined, phaseConfig.phase) match {
          case (NoSecurityDetails, _, Transition) | (_, false, Transition) =>
            AddTransportChargesYesNoPage(itemIndex).filterOptionalDependent(identity)(TransportChargesMethodOfPaymentPage(itemIndex).reader)
          case _ => UserAnswersReader(None)
        }
      }
    } yield result

}
