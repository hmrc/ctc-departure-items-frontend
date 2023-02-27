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

import cats.implicits.catsSyntaxTuple2Semigroupal
import models.DeclarationType.T
import models.{DeclarationType, Index}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.external.TransitOperationDeclarationTypePage
import pages.item.{DeclarationTypePage, DescriptionPage}

import scala.language.implicitConversions

case class ItemDomain(
  itemDescription: String,
  declarationType: Option[DeclarationType]
)(index: Index)
    extends JourneyDomainModel

object ItemDomain {

  implicit def userAnswersReader(itemIndex: Index): UserAnswersReader[ItemDomain] = {

    lazy val declarationTypeReads: UserAnswersReader[Option[DeclarationType]] =
      TransitOperationDeclarationTypePage.reader.flatMap {
        case T => DeclarationTypePage(itemIndex).reader.map(Some(_))
      }

    (
      DescriptionPage(itemIndex).reader,
      declarationTypeReads
    ).tupled.map((ItemDomain.apply _).tupled).map(_(itemIndex))

  }
}
