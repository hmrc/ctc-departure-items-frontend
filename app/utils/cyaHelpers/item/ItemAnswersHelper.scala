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
import models.journeyDomain.item.dangerousGoods.DangerousGoodsDomain
import models.journeyDomain.item.packages.PackageDomain
import models.reference.{Country, PackageType}
import models.{DeclarationType, Index, Mode, UserAnswers}
import pages.item._
import pages.item.packages.index.{AddShippingMarkYesNoPage, NumberOfPackagesPage, PackageTypePage, ShippingMarkPage}
import pages.sections.dangerousGoods.DangerousGoodsListSection
import pages.sections.packages.PackagesSection
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
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
    id = Some("change-description")
  )

  def declarationType: Option[SummaryListRow] = getAnswerAndBuildRow[DeclarationType](
    page = DeclarationTypePage(itemIndex),
    formatAnswer = formatEnumAsText(DeclarationType.messageKeyPrefix),
    prefix = "item.declarationType",
    id = Some("change-declaration-type")
  )

  def countryOfDispatch: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDispatchPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDispatch",
    id = Some("change-country-of-dispatch")
  )

  def countryOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDestinationPage(itemIndex),
    formatAnswer = formatAsCountry,
    prefix = "item.countryOfDestination",
    id = Some("change-country-of-destination")
  )

  def ucrYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddUCRYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addUCRYesNo",
    id = Some("change-add-ucr")
  )

  def uniqueConsignmentReference: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UniqueConsignmentReferencePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.uniqueConsignmentReference",
    id = Some("change-ucr")
  )

  def cusCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCUSCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCUSCodeYesNo",
    id = Some("change-add-cus-code")
  )

  def customsUnionAndStatisticsCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CustomsUnionAndStatisticsCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.customsUnionAndStatisticsCode",
    id = Some("change-cus-code")
  )

  def commodityCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCommodityCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCommodityCodeYesNo",
    id = Some("change-add-commodity-code")
  )

  def commodityCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CommodityCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.commodityCode",
    id = Some("change-commodity-code")
  )

  def combinedNomenclatureCodeYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddCombinedNomenclatureCodeYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addCombinedNomenclatureCodeYesNo",
    id = Some("change-add-combined-nomenclature-code")
  )

  def combinedNomenclatureCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = CombinedNomenclatureCodePage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.combinedNomenclatureCode",
    id = Some("change-combined-nomenclature-code")
  )

  def dangerousGoodsYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddDangerousGoodsYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addDangerousGoodsYesNo",
    id = Some("change-add-dangerous-goods")
  )

  def dangerousGoodsList: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(DangerousGoodsListSection(itemIndex))(dangerousGoods)

  def dangerousGoods(dangerousGoodsIndex: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[DangerousGoodsDomain](
    formatAnswer = formatAsText,
    prefix = "item.index.checkYourAnswers.dangerousGoods",
    id = Some(s"change-dangerous-goods-${dangerousGoodsIndex.display}"),
    args = dangerousGoodsIndex.display
  )(DangerousGoodsDomain.userAnswersReader(itemIndex, dangerousGoodsIndex))

  def grossWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = GrossWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.grossWeight",
    id = Some(s"change-gross-weight-${itemIndex.display}"),
    args = itemIndex.display
  )

  def itemNetWeightYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddItemNetWeightYesNoPage(itemIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.addItemNetWeightYesNo",
    id = Some("change-add-item-net-weight")
  )

  def netWeight: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = NetWeightPage(itemIndex),
    formatAnswer = formatAsText,
    prefix = "item.netWeight",
    id = Some(s"change-net-weight-${itemIndex.display}"),
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
    id = Some(s"change-supplementary-units-${itemIndex.display}"),
    args = itemIndex.display
  )

  def packageType(packageIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[PackageType](
    page = PackageTypePage(itemIndex, packageIndex),
    formatAnswer = formatAsText,
    prefix = "item.packages.index.packageType",
    id = Some(s"change-type-${packageIndex.display}"),
    args = packageIndex.display
  )

  def numberOfPackages(packageIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[Int](
    page = NumberOfPackagesPage(itemIndex, packageIndex),
    formatAnswer = formatAsText,
    prefix = "item.packages.index.numberOfPackages",
    id = Some(s"change-type-quantity-${packageIndex.display}"),
    args = packageIndex.display
  )

  def shippingMarkYesNo(packageIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddShippingMarkYesNoPage(itemIndex, packageIndex),
    formatAnswer = formatAsYesOrNo,
    prefix = "item.packages.index.addShippingMarkYesNo",
    id = Some(s"change-add-shipping-mark-${packageIndex.display}"),
    args = packageIndex.display
  )

  def shippingMark(packageIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ShippingMarkPage(itemIndex, packageIndex),
    formatAnswer = formatAsText,
    prefix = "item.packages.index.shippingMark",
    id = Some(s"change-shipping-mark-${packageIndex.display}"),
    args = packageIndex.display
  )

  def packages: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(PackagesSection(itemIndex))(`package`)

  def `package`(packageIndex: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[PackageDomain](
    formatAnswer = _.asString.toText,
    prefix = "item.index.checkYourAnswers.package",
    id = Some(s"change-package-${packageIndex.display}"),
    args = packageIndex.display
  )(PackageDomain.userAnswersReader(itemIndex, packageIndex))
}
