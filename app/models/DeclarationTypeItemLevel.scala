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

import config.Constants.{T1, T2, T2F}
import play.api.libs.json.{Format, Json}

case class DeclarationTypeItemLevel(code: String, description: String) extends Radioable[DeclarationTypeItemLevel] {
  override def toString: String         = s"$code - $description"
  override val messageKeyPrefix: String = DeclarationTypeItemLevel.messageKeyPrefix

}

object DeclarationTypeItemLevel extends DynamicEnumerableType[DeclarationTypeItemLevel] {

  implicit val format: Format[DeclarationTypeItemLevel] = Json.format[DeclarationTypeItemLevel]
  val messageKeyPrefix: String                          = "item.declarationType"

  def itemValues(declarationTypes: Seq[DeclarationTypeItemLevel]): Seq[DeclarationTypeItemLevel] = {
    val allowedTypes = List(T1, T2, T2F)
    declarationTypes.filter(
      item => allowedTypes.contains(item.code)
    )

  }

}
