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

package models.journeyDomain.item

import config.Constants.DeclarationType.*
import models.*
import models.DeclarationTypeItemLevel.*
import models.journeyDomain.*
import models.journeyDomain.item.additionalInformation.AdditionalInformationListDomain
import models.journeyDomain.item.additionalReferences.AdditionalReferencesDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsListDomain
import models.journeyDomain.item.documents.DocumentsDomain
import models.journeyDomain.item.packages.PackagesDomain
import models.journeyDomain.item.supplyChainActors.SupplyChainActorsDomain
import models.reference.{Country, TransportChargesMethodOfPayment}
import pages.external.*
import pages.item.*
import pages.sections.external.{DocumentsSection, TransportEquipmentsSection}
import pages.sections.{ItemSection, Section}
import play.api.i18n.Messages
import java.util.UUID

case class ItemDomain(
  itemDescription: String,
  transportEquipment: Option[UUID],
  declarationType: Option[DeclarationTypeItemLevel],
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

  override def page(userAnswers: UserAnswers): Option[Section[?]] = Some(ItemSection(index))
}

object ItemDomain {

  implicit def userAnswersReader(itemIndex: Index): Read[ItemDomain] =
    RichTuple20(
      (DescriptionPage(itemIndex).reader,
       transportEquipmentReader(itemIndex),
       declarationTypeReader(itemIndex),
       countryOfDispatchReader(itemIndex),
       countryOfDestinationReader(itemIndex),
       ucrReader(itemIndex),
       cusCodeReader(itemIndex),
       commodityCodeReader(itemIndex),
       combinedNomenclatureCodeReader(itemIndex),
       dangerousGoodsReader(itemIndex),
       grossWeightWithZeroCheck(itemIndex),
       netWeightReader(itemIndex),
       supplementaryUnitsReader(itemIndex),
       packagesReader(itemIndex),
       consigneeReader(),
       supplyChainActorsReader(itemIndex),
       documentsReader(itemIndex),
       additionalReferencesReader(itemIndex),
       additionalInformationListReader(itemIndex),
       transportChargesReader()
      )
    ).mapAs(
      ItemDomain
        .apply(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _)(itemIndex)
    )

  def grossWeightWithZeroCheck(itemIndex: Index): Read[BigDecimal] =
    GrossWeightPage(itemIndex).reader.to {
      case grossWeight if grossWeight.signum == 0 =>
        GrossWeightBeforeYouContinuePage(itemIndex).reader.to(
          _ => UserAnswersReader.pure(grossWeight)
        )
      case grossWeight => UserAnswersReader.pure(grossWeight)
    }

  def transportEquipmentReader(itemIndex: Index): Read[Option[UUID]] =
    TransportEquipmentsSection.optionalReader.to {
      case Some(array) if array.nonEmpty =>
        TransportEquipmentPage(itemIndex).reader.toOption
      case _ =>
        UserAnswersReader.none
    }

  def declarationTypeReader(itemIndex: Index): Read[Option[DeclarationTypeItemLevel]] =
    TransitOperationDeclarationTypePage.filterOptionalDependent(_ == T) {
      DeclarationTypePage(itemIndex).reader
    }

  def countryOfDispatchReader(itemIndex: Index): Read[Option[Country]] =
    ConsignmentCountryOfDispatchPage.filterDependent(_.isEmpty) {
      CountryOfDispatchPage(itemIndex).reader
    }

  def countryOfDestinationReader(itemIndex: Index): Read[Option[Country]] =
    ConsignmentCountryOfDestinationPage.filterDependent(_.isEmpty) {
      CountryOfDestinationPage(itemIndex).reader
    }

  def ucrReader(itemIndex: Index): Read[Option[String]] = {
    lazy val optionalUcrReader: Read[Option[String]] =
      AddUCRYesNoPage(itemIndex).filterOptionalDependent(identity)(UniqueConsignmentReferencePage(itemIndex).reader)

    ConsignmentUCRPage.optionalReader.to {
      case Some(consignmentUcr) =>
        UserAnswersReader.none
      case None =>
        DocumentsSection.arrayReader.to {
          array =>
            val documents = array.validateAsListOf[Document]
            val isConsignmentTransportDocumentDefined = documents.exists(
              x => x.attachToAllItems && x.`type`.isTransport
            )
            if (isConsignmentTransportDocumentDefined) {
              optionalUcrReader
            } else {
              UniqueConsignmentReferencePage(itemIndex).reader.toOption
            }
        }
    }
  }

  def cusCodeReader(itemIndex: Index): Read[Option[String]] =
    AddCUSCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
      CustomsUnionAndStatisticsCodePage(itemIndex).reader
    }

  def commodityCodeReader(itemIndex: Index): Read[Option[String]] =
    UserAnswersReader.success(_.status).to {
      case models.SubmissionState.Amendment =>
        UserAnswersReader.none
      case _ =>
        TransitOperationTIRCarnetNumberPage.optionalReader.to {
          case None =>
            CommodityCodePage(itemIndex).reader.toOption
          case _ =>
            AddCommodityCodeYesNoPage(itemIndex).filterOptionalDependent(identity)(CommodityCodePage(itemIndex).reader)
        }
    }

  def combinedNomenclatureCodeReader(itemIndex: Index): Read[Option[String]] =
    CommodityCodePage(itemIndex).optionalReader.to {
      case Some(_) =>
        CustomsOfficeOfDepartureInCL112Page
          .filterOptionalDependent(!_) {
            AddCombinedNomenclatureCodeYesNoPage(itemIndex).filterOptionalDependent(identity) {
              CombinedNomenclatureCodePage(itemIndex).reader
            }
          }
          .flatten
      case _ =>
        UserAnswersReader.none
    }

  def dangerousGoodsReader(itemIndex: Index): Read[Option[DangerousGoodsListDomain]] =
    AddDangerousGoodsYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(DangerousGoodsListDomain.userAnswersReader(itemIndex))

  def netWeightReader(itemIndex: Index): Read[Option[BigDecimal]] =
    ApprovedOperatorPage.optionalReader.to {
      case Some(true) =>
        UserAnswersReader.none
      case _ =>
        AddItemNetWeightYesNoPage(itemIndex).filterOptionalDependent(identity) {
          NetWeightPage(itemIndex).reader
        }
    }

  def supplementaryUnitsReader(itemIndex: Index): Read[Option[BigDecimal]] =
    AddSupplementaryUnitsYesNoPage(itemIndex).filterOptionalDependent(identity) {
      SupplementaryUnitsPage(itemIndex).reader
    }

  def packagesReader(itemIndex: Index): Read[PackagesDomain] =
    PackagesDomain.userAnswersReader(itemIndex)

  def consigneeReader(): Read[Option[ConsigneeDomain]] =
    UserAnswersReader.none

  def supplyChainActorsReader(itemIndex: Index): Read[Option[SupplyChainActorsDomain]] =
    AddSupplyChainActorYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(SupplyChainActorsDomain.userAnswersReader(itemIndex))

  def documentsReader(itemIndex: Index): Read[Option[DocumentsDomain]] = {

    def isConsignmentPreviousDocDefined(itemIndex: Index): Read[Boolean] = {
      import models.Document.RichDocuments
      DocumentsSection.arrayReader
        .to {
          arr =>
            val documents = arr.validateAsListOf[Document]
            Read(documents.isConsignmentPreviousDocumentPresent)
        }
    }

    def optionalDocumentsReader: Read[Option[DocumentsDomain]] =
      UserAnswersReader
        .readInferred(AddDocumentsYesNoPage(itemIndex), InferredAddDocumentsYesNoPage(itemIndex))
        .to {
          case true  => mandatoryDocumentsReader.toOption
          case false => UserAnswersReader.none
        }

    def mandatoryDocumentsReader: Read[DocumentsDomain] =
      DocumentsDomain.userAnswersReader(itemIndex)

    (
      TransitOperationDeclarationTypePage.reader,
      CustomsOfficeOfDepartureInCL112Page.reader,
      DeclarationTypePage(itemIndex).optionalReader.flatMapTo(_.code),
      isConsignmentPreviousDocDefined(itemIndex)
    ).to {
      case (T2 | T2F, true, _, false) =>
        mandatoryDocumentsReader.toOption
      case (_, true, Some(T2 | T2F), false) =>
        mandatoryDocumentsReader.toOption
      case _ =>
        ConsignmentAddDocumentsPage.optionalReader.to {
          case Some(false) =>
            UserAnswersReader.none
          case _ =>
            optionalDocumentsReader
        }
    }
  }

  def additionalReferencesReader(itemIndex: Index): Read[Option[AdditionalReferencesDomain]] =
    AddAdditionalReferenceYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(AdditionalReferencesDomain.userAnswersReader(itemIndex))

  def additionalInformationListReader(itemIndex: Index): Read[Option[AdditionalInformationListDomain]] =
    AddAdditionalInformationYesNoPage(itemIndex)
      .filterOptionalDependent(identity)(AdditionalInformationListDomain.userAnswersReader(itemIndex))

  def transportChargesReader(): Read[Option[TransportChargesMethodOfPayment]] =
    UserAnswersReader.none

}
