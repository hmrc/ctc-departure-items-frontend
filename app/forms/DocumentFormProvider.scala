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

package forms

import config.FrontendAppConfig
import forms.mappings.Mappings
import models.{Document, ItemLevelDocuments, SelectableList}
import play.api.data.Form

import javax.inject.Inject

class DocumentFormProvider @Inject() extends Mappings {

  def apply(
    prefix: String,
    selectableList: SelectableList[Document],
    itemLevelDocuments: ItemLevelDocuments,
    args: Any*
  )(implicit config: FrontendAppConfig): Form[Document] =
    Form(
      "document" -> selectable[Document](selectableList, s"$prefix.error.required", args)
        .verifying(
          maxLimit(itemLevelDocuments, s"$prefix.error.maxLimitReached")
        )
    )
}
