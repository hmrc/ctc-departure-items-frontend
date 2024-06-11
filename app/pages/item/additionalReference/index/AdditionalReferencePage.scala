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

package pages.item.additionalReference.index

import controllers.item.additionalReference.index.routes
import models.reference.AdditionalReference
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AdditionalReferencePage(itemIndex: Index, additionalReferenceIndex: Index) extends QuestionPage[AdditionalReference] {

  override def path: JsPath = AdditionalReferenceSection(itemIndex, additionalReferenceIndex).path \ toString

  override def toString: String = "additionalReference"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex))

  override def cleanup(value: Option[AdditionalReference], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers
          .remove(AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex))
          .flatMap(_.remove(AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex)))
      case _ =>
        super.cleanup(value, userAnswers)
    }
}

case class AdditionalReferenceInCL234Page(itemIndex: Index, additionalReferenceIndex: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = AdditionalReferencePage(itemIndex, additionalReferenceIndex).path \ toString

  override def toString: String = "isInCL234"
}
