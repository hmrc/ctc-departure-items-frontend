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

import play.api.libs.json.{JsArray, JsError, JsSuccess, Reads}

case class SelectableList[T <: Selectable](values: Seq[T])

object SelectableList {

  implicit val documentsReads: Reads[SelectableList[Document]] = Reads[SelectableList[Document]] {
    case JsArray(values) =>
      JsSuccess(
        SelectableList(
          values.zipWithIndex.flatMap {
            case (value, index) => value.validate[Document](Document.reads(index)).asOpt
          }.toSeq
        )
      )
    case _ => JsError("SelectableList::documentsReads: Failed to read documents from cache")
  }
}
