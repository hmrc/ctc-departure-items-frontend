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

package models

import config.FrontendAppConfig
import models.DocumentType.{Previous, Support, Transport}
import services.DocumentsService

case class ItemLevelDocuments(
  previous: Int,
  support: Int,
  transport: Int
) {

  def canAdd(documentType: DocumentType)(implicit config: FrontendAppConfig): Boolean = documentType match {
    case Previous  => previous < config.maxPreviousDocuments
    case Support   => support < config.maxSupportingDocuments
    case Transport => transport < config.maxTransportDocuments
  }

  def cannotAddAnyMore(implicit config: FrontendAppConfig): Boolean =
    !canAdd(Previous) && !canAdd(Support) && !canAdd(Transport)
}

object ItemLevelDocuments {

  def apply(): ItemLevelDocuments = ItemLevelDocuments(0, 0, 0)

  private def apply(values: (Int, Int, Int)): ItemLevelDocuments =
    ItemLevelDocuments(values._1, values._2, values._3)

  def apply(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Index)(implicit documentsService: DocumentsService): ItemLevelDocuments =
    ItemLevelDocuments(userAnswers, itemIndex, Some(documentIndex))

  def apply(userAnswers: UserAnswers, itemIndex: Index, documentIndex: Option[Index] = None)(implicit documentsService: DocumentsService): ItemLevelDocuments =
    (0 until documentsService.numberOfDocuments(userAnswers, itemIndex)).map(Index(_)).foldLeft(ItemLevelDocuments()) {
      case (ItemLevelDocuments(previous, support, transport), index) if !documentIndex.contains(index) =>
        lazy val documentType = documentsService.getDocument(userAnswers, itemIndex, index).map(_.`type`)
        val values = documentType match {
          case Some(Previous)  => (previous + 1, support, transport)
          case Some(Support)   => (previous, support + 1, transport)
          case Some(Transport) => (previous, support, transport + 1)
          case _               => (previous, support, transport)
        }
        ItemLevelDocuments(values)
      case (itemLevelDocuments, _) => itemLevelDocuments
    }
}
