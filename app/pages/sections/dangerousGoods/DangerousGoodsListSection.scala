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

package pages.sections.dangerousGoods

import models.Index
import pages.AddAnotherPage
import pages.item.dangerousGoods.index.AddAnotherDangerousGoodsPage
import pages.sections.{AddAnotherSection, ItemSection}
import play.api.libs.json.JsPath

case class DangerousGoodsListSection(itemIndex: Index) extends AddAnotherSection {

  override def path: JsPath = ItemSection(itemIndex).path \ toString

  override def toString: String = "dangerousGoodsList"

  override val addAnotherPage: AddAnotherPage = AddAnotherDangerousGoodsPage(itemIndex)
}
