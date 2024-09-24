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

package models

import cats.Order
import play.api.libs.json.{Format, Json}

case class DeclarationTypeItemLevel(code: String, description: String) extends Radioable[DeclarationTypeItemLevel] {
  override def toString: String = s"$code - $description"

  override val messageKeyPrefix: String = "item.declarationType"

  def isOneOf(codes: String*): Boolean = codes.contains(code)
}

object DeclarationTypeItemLevel extends DynamicEnumerableType[DeclarationTypeItemLevel] {

  implicit val format: Format[DeclarationTypeItemLevel] = Json.format[DeclarationTypeItemLevel]

  implicit val order: Order[DeclarationTypeItemLevel] = (x: DeclarationTypeItemLevel, y: DeclarationTypeItemLevel) => x.toString.compareToIgnoreCase(y.toString)
}
