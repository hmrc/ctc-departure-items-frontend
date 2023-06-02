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

package viewmodels.item

import config.FrontendAppConfig
import models.{Index, UserAnswers}
import play.api.i18n.Messages
import services.DocumentsService
import utils.cyaHelpers.item.ItemAnswersHelper
import viewmodels.sections.Section

import javax.inject.Inject

case class ItemAnswersViewModel(sections: Seq[Section])

object ItemAnswersViewModel {

  def apply(
    userAnswers: UserAnswers,
    itemIndex: Index
  )(implicit messages: Messages, config: FrontendAppConfig, documentsService: DocumentsService): ItemAnswersViewModel =
    new ItemAnswersViewModelProvider().apply(userAnswers, itemIndex)

  class ItemAnswersViewModelProvider @Inject() (implicit documentsService: DocumentsService) {

    // scalastyle:off method.length
    def apply(userAnswers: UserAnswers, itemIndex: Index)(implicit messages: Messages, config: FrontendAppConfig): ItemAnswersViewModel = {
      val helper = new ItemAnswersHelper(userAnswers, itemIndex)

      val firstItemSection = Section(
        rows = Seq(
          helper.itemDescription,
          helper.transportEquipment,
          helper.declarationType,
          helper.countryOfDispatch,
          helper.countryOfDestination,
          helper.ucrYesNo,
          helper.uniqueConsignmentReference,
          helper.cusCodeYesNo,
          helper.customsUnionAndStatisticsCode,
          helper.commodityCodeYesNo,
          helper.commodityCode,
          helper.combinedNomenclatureCodeYesNo,
          helper.combinedNomenclatureCode
        ).flatten
      )

      val dangerousGoodsSection = Section(
        sectionTitle = messages("item.checkYourAnswers.dangerousGoods"),
        rows = helper.dangerousGoodsYesNo.toList ++ helper.dangerousGoodsList,
        addAnotherLink = helper.addOrRemoveDangerousGoods
      )

      val measurementSection = Section(
        sectionTitle = messages("item.checkYourAnswers.measurement"),
        rows = Seq(
          helper.grossWeight,
          helper.itemNetWeightYesNo,
          helper.netWeight,
          helper.supplementaryUnitsYesNo,
          helper.supplementaryUnits
        ).flatten
      )

      val packagesSection = Section(
        sectionTitle = messages("item.checkYourAnswers.packages"),
        rows = helper.packages,
        addAnotherLink = helper.addOrRemovePackages
      )

      val documentsSection = Section(
        sectionTitle = messages("item.checkYourAnswers.documents"),
        rows = helper.documentsYesNo.toList ++ helper.documents,
        addAnotherLink = helper.addOrRemoveDocuments
      )

      val additionalReferencesSection = Section(
        sectionTitle = messages("item.checkYourAnswers.additionalReferences"),
        rows = helper.additionalReferenceYesNo.toList ++ helper.additionalReferences,
        addAnotherLink = helper.addOrRemoveAdditionalReferences
      )

      val additionalInformationSection = Section(
        sectionTitle = messages("item.checkYourAnswers.additionalInformation"),
        rows = helper.additionalInformationYesNo.toList ++ helper.additionalInformationList,
        addAnotherLink = helper.addOrRemoveAdditionalInformation
      )

      val sections = firstItemSection.toSeq ++
        dangerousGoodsSection.toSeq ++
        measurementSection.toSeq ++
        packagesSection.toSeq ++
        documentsSection.toSeq ++
        additionalReferencesSection.toSeq ++
        additionalInformationSection.toSeq

      new ItemAnswersViewModel(sections)
    }
    // scalastyle:on method.length
  }
}
