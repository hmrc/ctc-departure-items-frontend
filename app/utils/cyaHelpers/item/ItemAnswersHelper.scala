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

package utils.cyaHelpers.item

import config.FrontendAppConfig
import models.journeyDomain.item.additionalInformation.AdditionalInformationDomain
import models.journeyDomain.item.additionalReferences.AdditionalReferenceDomain
import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.journeyDomain.item.documents.DocumentDomain
import models.journeyDomain.item.packages.PackageDomain
import models.journeyDomain.item.supplyChainActors.SupplyChainActorDomain
import models.reference.Country
import models.{CheckMode, DeclarationType, Index, UserAnswers}
import pages.item._
import pages.sections.additionalInformation.AdditionalInformationListSection
import pages.sections.additionalReference.AdditionalReferencesSection
import pages.sections.dangerousGoods.DangerousGoodsListSection
import pages.sections.documents.DocumentsSection
import pages.sections.packages.PackagesSection
import pages.sections.supplyChainActors.SupplyChainActorsSection
import play.api.i18n.Messages
import services.{DocumentsService, TransportEquipmentService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.cyaHelpers.AnswersHelper
import viewmodels.Link

import java.util.UUID

// scalastyle:off number.of.methods
class ItemAnswersHelper(
  userAnswers: UserAnswers,
  itemIndex: Index
)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, CheckMode) {

  def itemDescription: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = DescriptionPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.description",
    id = Some("change-description"),
    args = itemIndex.display
  )

  def transportEquipment(implicit transportEquipmentService: TransportEquipmentService): Option[SummaryListRow] =
    transportEquipmentService.getTransportEquipment(userAnswers, itemIndex).flatMap {
      transportEquipment =>
        getAnswerAndBuildRow[UUID](
          page = TransportEquipmentPage(itemIndex),
          formatAnswer = _ => formatAsText(transportEquipment),
          prefix = "item.transportEquipment",
          id = Some("change-transport-equipment"),
          args = itemIndex.display
        )
    }

  def declarationType: Option[SummaryListRow] = getAnswerAndBuildRow[DeclarationType](
    page = DeclarationTypePage(itemIndex),
    formatAnswer = formatEnumAsText(DeclarationType.messageKeyPrefix),
    prefix = "item.declarationType",
    id = Some("change-declaration-type"),
    args = itemIndex.display
  )

  def countryOfDispatch: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDispatchPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDispatch",
    id = Some("change-country-of-dispatch"),
    args = itemIndex.display
  )

  def countryOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDestinationPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDestination",
    id = Some("change-country-of-destination"),
    args = itemIndex.display
  )

  def ucrYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddUCRYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addUCRYesNo",
    id = Some("change-add-ucr"),
    args = itemIndex.display
  )

  def uniqueConsignmentReference: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UniqueConsignmentReferencePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.uniqueConsignmentReference",
    id = Some("change-ucr"),
    args = itemIndex.display
  )

  def cusCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCUSCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCUSCodeYesNo",
    id = Some("change-add-cus-code"),
    args = itemIndex.display
  )

  def customsUnionAndStatisticsCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CustomsUnionAndStatisticsCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.customsUnionAndStatisticsCode",
    id = Some("change-cus-code"),
    args = itemIndex.display
  )

  def commodityCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCommodityCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCommodityCodeYesNo",
    id = Some("change-add-commodity-code"),
    args = itemIndex.display
  )

  def commodityCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.commodityCode",
    id = Some("change-commodity-code"),
    args = itemIndex.display
  )

  def combinedNomenclatureCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCombinedNomenclatureCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCombinedNomenclatureCodeYesNo",
    id = Some("change-add-combined-nomenclature-code"),
    args = itemIndex.display
  )

  def combinedNomenclatureCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CombinedNomenclatureCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.combinedNomenclatureCode",
    id = Some("change-combined-nomenclature-code"),
    args = itemIndex.display
  )

  def dangerousGoodsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addDangerousGoodsYesNo",
    id = Some("change-add-dangerous-goods"),
    args = itemIndex.display
  )

  def dangerousGoodsList: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(DangerousGoodsListSection(itemIndex))(dangerousGoods)

  def dangerousGoods(dangerousGoodsIndex: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[DangerousGoodsDomain](
      formatAnswer = formatAsText,
      prefix = "item.checkYourAnswers.dangerousGoods",
      id = Some(s"change-dangerous-goods-${dangerousGoodsIndex.display}"),
      args = dangerousGoodsIndex.display
    )(DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex))

  def addOrRemoveDangerousGoods: Option[Link] = buildLink(DangerousGoodsListSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-dangerous-goods",
        text = messages("item.checkYourAnswers.dangerousGoods.addOrRemove"),
        href = controllers.item.dangerousGoods.routes.AddAnotherDangerousGoodsController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

  def grossWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = GrossWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.grossWeight",
    id = Some("change-gross-weight"),
    args = itemIndex.display
  )

  def itemNetWeightYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddItemNetWeightYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addItemNetWeightYesNo",
    id = Some("change-add-item-net-weight"),
    args = itemIndex.display
  )

  def netWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = NetWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.netWeight",
    id = Some("change-net-weight"),
    args = itemIndex.display
  )

  def supplementaryUnitsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddSupplementaryUnitsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addSupplementaryUnitsYesNo",
    id = Some("change-add-supplementary-units"),
    args = itemIndex.display
  )

  def supplementaryUnits: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = SupplementaryUnitsPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.supplementaryUnits",
    id = Some("change-supplementary-units"),
    args = itemIndex.display
  )

  def packages: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(PackagesSection(itemIndex))(`package`)

  def `package`(packageIndex: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[PackageDomain](
      formatAnswer = formatAsText,
      prefix = "item.checkYourAnswers.package",
      id = Some(s"change-package-${packageIndex.display}"),
      args = packageIndex.display
    )(PackageDomain.userAnswersReader(itemIndex, packageIndex))

  def addOrRemovePackages: Option[Link] = buildLink(PackagesSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-packages",
        text = messages("item.checkYourAnswers.packages.addOrRemove"),
        href = controllers.item.packages.routes.AddAnotherPackageController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

  def supplyChainActorYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddSupplyChainActorYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addSupplyChainActorYesNo",
    id = Some("change-add-supply-chain-actors"),
    args = itemIndex.display
  )

  def supplyChainActors: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(SupplyChainActorsSection(itemIndex))(supplyChainActor)

  def supplyChainActor(actorIndex: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[SupplyChainActorDomain](
      formatAnswer = _.asString.toText,
      prefix = "item.checkYourAnswers.supplyChainActor",
      id = Some(s"change-supply-chain-actor-${actorIndex.display}"),
      args = actorIndex.display
    )(SupplyChainActorDomain.userAnswersReader(itemIndex, actorIndex))

  def addOrRemoveSupplyChainActors: Option[Link] = buildLink(SupplyChainActorsSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-supply-chain-actors",
        text = messages("item.checkYourAnswers.supplyChainActors.addOrRemove"),
        href = controllers.item.supplyChainActors.routes.AddAnotherSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

  def documentsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDocumentsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addDocumentsYesNo",
    id = Some("change-add-documents"),
    args = itemIndex.display
  )

  def documents(implicit documentsService: DocumentsService): Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(DocumentsSection(itemIndex))(document)

  def document(documentIndex: Index)(implicit documentsService: DocumentsService): Option[SummaryListRow] =
    documentsService.getDocument(userAnswers, itemIndex, documentIndex).flatMap {
      document =>
        getAnswerAndBuildSectionRow[DocumentDomain](
          formatAnswer = _ => formatAsText(document),
          prefix = "item.checkYourAnswers.document",
          id = Some(s"change-document-${documentIndex.display}"),
          args = documentIndex.display
        )(DocumentDomain.userAnswersReader(itemIndex, documentIndex))
    }

  def addOrRemoveDocuments: Option[Link] = buildLink(DocumentsSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-documents",
        text = messages("item.checkYourAnswers.documents.addOrRemove"),
        href = controllers.item.documents.routes.AddAnotherDocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

  def additionalReferenceYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAdditionalReferenceYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addAdditionalReferenceYesNo",
    id = Some("change-add-additional-reference"),
    args = itemIndex.display
  )

  def additionalReferences: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(AdditionalReferencesSection(itemIndex))(additionalReference)

  def additionalReference(additionalReferenceIndex: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[AdditionalReferenceDomain](
      formatAnswer = formatAsText,
      prefix = "item.checkYourAnswers.additionalReference",
      id = Some(s"change-additional-reference-${additionalReferenceIndex.display}"),
      args = additionalReferenceIndex.display
    )(AdditionalReferenceDomain.userAnswersReader(itemIndex, additionalReferenceIndex))

  def addOrRemoveAdditionalReferences: Option[Link] = buildLink(AdditionalReferencesSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-additional-references",
        text = messages("item.checkYourAnswers.additionalReferences.addOrRemove"),
        href = controllers.item.additionalReference.routes.AddAnotherAdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

  def additionalInformationYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAdditionalInformationYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addAdditionalInformationYesNo",
    id = Some("change-add-additional-information"),
    args = itemIndex.display
  )

  def additionalInformationList: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(AdditionalInformationListSection(itemIndex))(additionalInformation)

  def additionalInformation(additionalInformationIndex: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[AdditionalInformationDomain](
    formatAnswer = formatAsText,
    prefix = "item.checkYourAnswers.additionalInformation",
    id = Some(s"change-additional-information-${additionalInformationIndex.display}"),
    args = additionalInformationIndex.display
  )(AdditionalInformationDomain.userAnswersReader(itemIndex, additionalInformationIndex))

  def addOrRemoveAdditionalInformation: Option[Link] = buildLink(AdditionalInformationListSection(itemIndex)) {
    mode =>
      Link(
        id = "add-or-remove-additional-information",
        text = messages("item.checkYourAnswers.additionalInformation.addOrRemove"),
        href = controllers.item.additionalInformation.routes.AddAnotherAdditionalInformationController.onPageLoad(userAnswers.lrn, mode, itemIndex).url
      )
  }

}
// scalastyle:on number.of.methods
