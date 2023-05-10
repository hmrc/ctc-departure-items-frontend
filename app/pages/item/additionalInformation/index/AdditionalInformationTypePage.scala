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

package pages.item.additionalInformation.index

import controllers.item.additionalInformation.index.routes
import models.reference.AdditionalInformation
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.additionalInformation.AdditionalInformationSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AdditionalInformationTypePage(itemIndex: Index, additionalInformationIndex: Index) extends QuestionPage[AdditionalInformation] {

  override def path: JsPath = AdditionalInformationSection(itemIndex, additionalInformationIndex).path \ toString

  override def toString: String = "additionalInformationType"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AdditionalInformationTypeController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalInformationIndex))

  override def cleanup(value: Option[AdditionalInformation], userAnswers: UserAnswers): Try[UserAnswers] = value match {
    case Some(_) => userAnswers.remove(AdditionalInformationPage(itemIndex, additionalInformationIndex))
    case None    => super.cleanup(value, userAnswers)
  }
}
