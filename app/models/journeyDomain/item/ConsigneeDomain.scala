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

import models.journeyDomain._
import models.reference.Country
import models.{DynamicAddress, Index}
import pages.item.consignee._

sealed trait ConsigneeDomain extends JourneyDomainModel {
  val itemIndex: Index
}

object ConsigneeDomain {

  def userAnswersReader(itemIndex: Index): Read[ConsigneeDomain] =
    AddConsigneeEoriNumberYesNoPage(itemIndex).reader.to {
      case true  => ConsigneeDomainWithIdentificationNumber.userAnswersReader(itemIndex)
      case false => ConsigneeDomainWithNameAndAddress.userAnswersReader(itemIndex)
    }
}

case class ConsigneeDomainWithIdentificationNumber(
  identificationNumber: String
)(override val itemIndex: Index)
    extends ConsigneeDomain

object ConsigneeDomainWithIdentificationNumber {

  def userAnswersReader(itemIndex: Index): Read[ConsigneeDomain] =
    IdentificationNumberPage(itemIndex).reader.map(ConsigneeDomainWithIdentificationNumber(_)(itemIndex))
}

case class ConsigneeDomainWithNameAndAddress(
  name: String,
  country: Country,
  address: DynamicAddress
)(override val itemIndex: Index)
    extends ConsigneeDomain

object ConsigneeDomainWithNameAndAddress {

  def userAnswersReader(itemIndex: Index): Read[ConsigneeDomain] = (
    NamePage(itemIndex).reader,
    CountryPage(itemIndex).reader,
    AddressPage(itemIndex).reader
  ).map(ConsigneeDomainWithNameAndAddress.apply(_, _, _)(itemIndex))
}
