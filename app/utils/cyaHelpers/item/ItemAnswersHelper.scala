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
import models.reference.Country
import models.{DeclarationType, Index, Mode, UserAnswers}
import pages.item._
import pages.sections.additionalInformation.AdditionalInformationListSection
import pages.sections.additionalReference.AdditionalReferencesSection
import pages.sections.dangerousGoods.DangerousGoodsListSection
import pages.sections.documents.DocumentsSection
import pages.sections.packages.PackagesSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class ItemAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  itemIndex: Index
)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def itemDescription: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = DescriptionPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.description",
    id = Some(s"change-description"),
    args = itemIndex.display
  )

  def declarationType: Option[SummaryListRow] = getAnswerAndBuildRow[DeclarationType](
    page = DeclarationTypePage(itemIndex),
    formatAnswer = formatEnumAsText(DeclarationType.messageKeyPrefix),
    prefix = "item.declarationType",
    id = Some(s"change-declaration-type"),
    args = itemIndex.display
  )

  def countryOfDispatch: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDispatchPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDispatch",
    id = Some(s"change-country-of-dispatch"),
    args = itemIndex.display
  )

  def countryOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDestinationPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDestination",
    id = Some(s"change-country-of-destination"),
    args = itemIndex.display
  )

  def ucrYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddUCRYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addUCRYesNo",
    id = Some(s"change-add-ucr"),
    args = itemIndex.display
  )

  def uniqueConsignmentReference: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UniqueConsignmentReferencePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.uniqueConsignmentReference",
    id = Some(s"change-ucr"),
    args = itemIndex.display
  )

  def cusCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCUSCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCUSCodeYesNo",
    id = Some(s"change-add-cus-code"),
    args = itemIndex.display
  )

  def customsUnionAndStatisticsCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CustomsUnionAndStatisticsCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.customsUnionAndStatisticsCode",
    id = Some(s"change-cus-code"),
    args = itemIndex.display
  )

  def commodityCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCommodityCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCommodityCodeYesNo",
    id = Some(s"change-add-commodity-code"),
    args = itemIndex.display
  )

  def commodityCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.commodityCode",
    id = Some(s"change-commodity-code"),
    args = itemIndex.display
  )

  def combinedNomenclatureCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCombinedNomenclatureCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCombinedNomenclatureCodeYesNo",
    id = Some(s"change-add-combined-nomenclature-code"),
    args = itemIndex.display
  )

  def combinedNomenclatureCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CombinedNomenclatureCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.combinedNomenclatureCode",
    id = Some(s"change-combined-nomenclature-code"),
    args = itemIndex.display
  )

  def dangerousGoodsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addDangerousGoodsYesNo",
    id = Some(s"change-add-dangerous-goods"),
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

  def grossWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = GrossWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.grossWeight",
    id = Some(s"change-gross-weight"),
    args = itemIndex.display
  )

  def itemNetWeightYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddItemNetWeightYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addItemNetWeightYesNo",
    id = Some(s"change-add-item-net-weight"),
    args = itemIndex.display
  )

  def netWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = NetWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.netWeight",
    id = Some(s"change-net-weight"),
    args = itemIndex.display
  )

  def supplementaryUnitsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddSupplementaryUnitsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addSupplementaryUnitsYesNo",
    id = Some(s"change-add-supplementary-units"),
    args = itemIndex.display
  )

  def supplementaryUnits: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = SupplementaryUnitsPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.supplementaryUnits",
    id = Some(s"change-supplementary-units"),
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

  def documentsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDocumentsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addDocumentsYesNo",
    id = Some(s"change-add-documents"),
    args = itemIndex.display
  )

  def documents: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(DocumentsSection(itemIndex))(document)

  def document(documentIndex: Index): Option[SummaryListRow] =
    getAnswerAndBuildSectionRow[DocumentDomain](
      formatAnswer = formatAsText,
      prefix = "item.checkYourAnswers.document",
      id = Some(s"change-document-${documentIndex.display}"),
      args = documentIndex.display
    )(DocumentDomain.userAnswersReader(itemIndex, documentIndex))

  def additionalReferenceYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAdditionalReferenceYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addAdditionalReferenceYesNo",
    id = Some(s"change-add-additional-reference"),
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

  def additionalInformationYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAdditionalInformationYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addAdditionalInformationYesNo",
    id = Some(s"change-add-additional-information"),
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

}
