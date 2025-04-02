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

package pages.sections

import models.{Mode, UserAnswers}
import pages.{AddAnotherPage, QuestionPage, ReadOnlyPage}
import play.api.libs.json.{JsArray, JsValue}
import play.api.mvc.Call

trait Section[T <: JsValue] extends QuestionPage[T]

trait ReadOnlySection[T <: JsValue] extends ReadOnlyPage[T]

trait AddAnotherSection extends Section[JsArray] {

  val addAnotherPage: AddAnotherPage

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    addAnotherPage.route(userAnswers, mode)
}
