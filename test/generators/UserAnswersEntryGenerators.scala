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

package generators

import models.DeclarationType
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.libs.json._
import queries.Gettable

// scalastyle:off number.of.methods
trait UserAnswersEntryGenerators {
  self: Generators =>

  def generateAnswer: PartialFunction[Gettable[_], Gen[JsValue]] =
    generateItemAnswer

  private def generateItemAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item._
    val pf: PartialFunction[Gettable[_], Gen[JsValue]] = {
      case DescriptionPage(_)          => Gen.alphaNumStr.map(JsString)
      case DeclarationTypePage(_)      => arbitrary[DeclarationType].map(Json.toJson(_))
      case CountryOfDispatchPage(_)    => arbitrary[Country].map(Json.toJson(_))
      case CountryOfDestinationPage(_) => arbitrary[Country].map(Json.toJson(_))
    }
    pf orElse
      generateDangerousGoodsAnswer
  }

  private def generateDangerousGoodsAnswer: PartialFunction[Gettable[_], Gen[JsValue]] = {
    import pages.item.dangerousGoods.index._
    {
      case UNNumberPage(_, _) => Gen.alphaNumStr.map(JsString)
    }
  }

}
// scalastyle:on number.of.methods
