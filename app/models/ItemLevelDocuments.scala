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

  def apply(itemLevelDocuments: Seq[Document]): ItemLevelDocuments =
    new ItemLevelDocuments(
      previous = itemLevelDocuments.count(_.`type` == Previous),
      support = itemLevelDocuments.count(_.`type` == Support),
      transport = itemLevelDocuments.count(_.`type` == Transport)
    )
}
