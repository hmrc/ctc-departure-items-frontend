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

package models.journeyDomain.item.dangerousGoods

import models.{Index, RichJsArray}
import models.journeyDomain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import pages.sections.dangerousGoods.DangerousGoodsListSection

case class DangerousGoodsListDomain(value: Seq[DangerousGoodsDomain])

object DangerousGoodsListDomain {

  implicit def userAnswersReader(itemIndex: Index): UserAnswersReader[DangerousGoodsListDomain] =
    DangerousGoodsListSection(itemIndex).arrayReader
      .flatMap {
        case x if x.isEmpty =>
          UserAnswersReader(DangerousGoodsDomain.userAnswersReader(itemIndex, Index(0))).map(Seq(_))
        case x =>
          x.traverse[DangerousGoodsDomain](DangerousGoodsDomain.userAnswersReader(itemIndex, _))
      }
      .map(DangerousGoodsListDomain(_))
}
