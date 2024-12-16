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

package pages.item.documents.index

import controllers.item.documents.index.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.documents.DocumentSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.util.UUID

case class DocumentPage(itemIndex: Index, documentIndex: Index) extends QuestionPage[UUID] {

  override def path: JsPath = DocumentSection(itemIndex, documentIndex).path \ toString

  override def toString: String = "document"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.DocumentController.onPageLoad(userAnswers.lrn, mode, itemIndex, documentIndex))
}

// Tracks whether we are forced to add a previous document to an item as per rule C0035
case class MandatoryDocumentPage(itemIndex: Index, documentIndex: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = DocumentSection(itemIndex, documentIndex).path \ toString

  override def toString: String = "mandatory"
}
