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

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases._

trait RadioModel[T] extends Enumerable.Implicits {

  val messageKeyPrefix: String

  val values: Seq[T]

  def radioItems(formKey: String = "value", checkedValue: Option[T] = None)(implicit messages: Messages): Seq[RadioItem] =
    radioItems(values, formKey, checkedValue)

  def radioItems(values: Seq[T], formKey: String, checkedValue: Option[T])(implicit messages: Messages): Seq[RadioItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        RadioItem(
          content = Text(messages(s"$messageKeyPrefix.$value")),
          id = Some(if (index == 0) formKey else s"${formKey}_$index"),
          value = Some(value.toString),
          checked = checkedValue.contains(value),
          hint = {
            val hintKey = s"$messageKeyPrefix.$value.hint"
            if (messages.isDefinedAt(hintKey)) Some(Hint(content = Text(messages(hintKey)))) else None
          }
        )
    }

  implicit def enumerable: Enumerable[T] =
    Enumerable(
      values.map(
        v => v.toString -> v
      ): _*
    )
}
