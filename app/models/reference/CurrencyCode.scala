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

package models.reference

import models.Selectable
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

import java.util.Currency
import scala.util.Try

case class CurrencyCode(currency: String, description: Option[String]) extends Selectable {

  override def toString: String = currency + description.fold("")(
    x => s" - $x"
  )

  override def toSelectItem(selected: Boolean): SelectItem = SelectItem(Some(currency), this.toString, selected)

  val symbol: String = Try(Currency.getInstance(currency).getSymbol).getOrElse(currency)
}

object CurrencyCode {
  implicit val format: OFormat[CurrencyCode] = Json.format[CurrencyCode]
}
