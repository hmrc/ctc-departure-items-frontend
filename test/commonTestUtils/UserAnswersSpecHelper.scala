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

package commonTestUtils

import models.{Index, UserAnswers}
import org.scalactic.source.Position
import org.scalatest.exceptions.TestFailedException
import pages.QuestionPage
import play.api.libs.json.{JsResultException, Json, Reads, Writes}
import queries.Gettable

trait UserAnswersSpecHelper {

  implicit class UserAnswersSpecHelperOps(userAnswers: UserAnswers) {

    import models.RichJsObject

    private def unsafeSetWithOutCleanup[A: Writes](page: QuestionPage[A], value: A): UserAnswers =
      userAnswers.data
        .setObject(page.path, Json.toJson(value))
        .fold(
          errors => throw new JsResultException(errors),
          jsValue => userAnswers.copy(data = jsValue)
        )

    def unsafeSetVal[A: Writes](page: QuestionPage[A])(value: A): UserAnswers =
      unsafeSetWithOutCleanup(page, value)

    def unsafeSetOpt[A: Writes](page: QuestionPage[A])(value: Option[A]): UserAnswers =
      value.fold(userAnswers)(unsafeSetVal(page))

    def unsafeSetSeq[A: Writes](pageFn: Int => QuestionPage[A])(value: Seq[A]): UserAnswers =
      value.zipWithIndex
        .map {
          case (value, index) => (pageFn(index), value)
        }
        .foldLeft(userAnswers) {
          case (ua, (page, value)) =>
            ua.unsafeSetVal(page)(value)
        }

    def unsafeSetSeqIndex[A: Writes](pageFn: Index => QuestionPage[A])(value: Seq[A]): UserAnswers =
      value.zipWithIndex
        .map {
          case (value, index) => (pageFn(Index(index)), value)
        }
        .foldLeft(userAnswers) {
          case (ua, (page, value)) =>
            ua.unsafeSetVal(page)(value)
        }

    def unsafeSetPFn[A, B: Writes](page: QuestionPage[B])(value: A)(pf: PartialFunction[A, B]): UserAnswers =
      unsafeSetOpt(page)(pf.lift(value))

    def unsafeSetPFnOpt[A, B: Writes](page: QuestionPage[B])(value: A)(pf: PartialFunction[A, Option[B]]): UserAnswers =
      unsafeSetOpt(page)(pf.lift(value).flatten)

    // Should this call the remove and then unsafe unwrap the Try?
    def unsafeRemove[A](page: QuestionPage[A]): UserAnswers =
      userAnswers.data
        .removeObject(page.path)
        .fold(
          _ => userAnswers,
          jsValue => userAnswers.copy(data = jsValue)
        )

    def assert(description: String)(assertion: UserAnswers => Boolean)(implicit pos: Position): UserAnswers =
      if (assertion(userAnswers)) {
        userAnswers
      } else {

        val msg = s"Validation failed - $description. Validation is run on user answers before this assertion."
        throw UserAnswersNoErrorException("checkValidity", msg)
      }

    def unsafeGet[A: Reads](gettable: Gettable[A]): A =
      userAnswers.get(gettable).getOrElse(throw UserAnswersNoErrorException("unsafeGet", "Value not defined. It must be set prior to this get call."))

  }

  class UserAnswersNoErrorException(method: String, message: String, pos: Position)
      extends TestFailedException(_ => Some(s"Failed while calling $method. Message: $message"), None, pos)

  object UserAnswersNoErrorException {
    def apply(method: String, message: String)(implicit pos: Position) = new UserAnswersNoErrorException(method, message, pos)
  }

}
