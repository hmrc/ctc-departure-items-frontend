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
import models.DeclarationType._
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import models.{DeclarationType, Index}
import pages.external._
import pages.item._

import scala.language.implicitConversions

case class ItemDomain(
  itemDescription: String,
  declarationType: Option[DeclarationType],
  countryOfDispatch: Option[Country],
  countryOfDestination: Option[Country]
)(index: Index)
    extends JourneyDomainModel

object ItemDomain {

  implicit def userAnswersReader(itemIndex: Index): UserAnswersReader[ItemDomain] =
    (
      DescriptionPage(itemIndex).reader,
      declarationTypeReader(itemIndex),
      countryOfDispatchReader(itemIndex),
      countryOfDestinationReader(itemIndex)
    ).tupled.map((ItemDomain.apply _).tupled).map(_(itemIndex))

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
}
