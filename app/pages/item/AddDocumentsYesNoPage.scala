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

package pages.item

import controllers.item.routes
import models.{Index, Mode, UserAnswers}
import pages.sections.ItemSection
import pages.sections.documents.DocumentsSection
import pages.{InferredPage, QuestionPage}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

sealed abstract class BaseAddDocumentsYesNoPage(itemIndex: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = ItemSection(itemIndex).path \ toString

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddDocumentsYesNoController.onPageLoad(userAnswers.lrn, mode, itemIndex))

  def cleanup(userAnswers: UserAnswers): Try[UserAnswers]

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(DocumentsSection(itemIndex)).flatMap(cleanup)
      case Some(true)  => cleanup(userAnswers)
      case _           => super.cleanup(value, userAnswers)
    }
}

case class AddDocumentsYesNoPage(itemIndex: Index) extends BaseAddDocumentsYesNoPage(itemIndex) {

  override def toString: String = "addDocumentsYesNo"

  override def cleanup(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.remove(InferredAddDocumentsYesNoPage(itemIndex))
}

case class InferredAddDocumentsYesNoPage(itemIndex: Index) extends BaseAddDocumentsYesNoPage(itemIndex) with InferredPage[Boolean] {

  override def toString: String = "inferredAddDocumentsYesNo"

  override def cleanup(userAnswers: UserAnswers): Try[UserAnswers] =
    userAnswers.remove(AddDocumentsYesNoPage(itemIndex))
}
