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

import cats.data.ReaderT
import cats.implicits._
import play.api.libs.json.{JsArray, Reads}
import queries.Gettable

package object journeyDomain {

  type EitherType[A]        = Either[ReaderError, A]
  type UserAnswersReader[A] = ReaderT[EitherType, UserAnswers, A]

  object UserAnswersReader {
    def apply[A](implicit ev: UserAnswersReader[A]): UserAnswersReader[A] = ev

    def apply[A](fn: UserAnswers => EitherType[A]): UserAnswersReader[A] =
      ReaderT[EitherType, UserAnswers, A](fn)

    def apply[A](a: A): UserAnswersReader[A] = {
      val fn: UserAnswers => EitherType[A] = _ => Right(a)
      apply(fn)
    }

    def fail[A](page: Gettable[_], message: Option[String] = None): UserAnswersReader[A] = {
      val fn: UserAnswers => EitherType[A] = _ => Left(ReaderError(page, message))
      apply(fn)
    }
  }

  implicit class GettableAsFilterForNextReaderOps[A: Reads](a: Gettable[A]) {

    /**
      * Returns UserAnswersReader[B], where UserAnswersReader[B] which is run only if UserAnswerReader[A]
      * is defined and satisfies the predicate, if it defined and does not satisfy the predicate overall reader will
      * will fail returning a ReaderError. If the result of UserAnswerReader[A] is not defined then the overall reader will fail and
      * `next` will not be run
      */

    def filterMandatoryDependent[B](predicate: A => Boolean)(next: => UserAnswersReader[B]): UserAnswersReader[B] =
      a.reader(s"Reader for ${a.path} failed before reaching predicate")
        .flatMap {
          x =>
            if (predicate(x)) {
              next
            } else {
              UserAnswersReader.fail[B](a, Some(s"Mandatory predicate failed for ${a.path}"))
            }
        }

    /**
      * Returns UserAnswersReader[Option[B]], where UserAnswersReader[B] which is run only if UserAnswerReader[A]
      * is defined and satisfies the predicate, if it defined and does not satisfy the predicate overall reader will
      * will return None. If the result of UserAnswerReader[A] is not defined then the overall reader will fail and
      * `next` will not be run
      */
    def filterOptionalDependent[B](predicate: A => Boolean)(next: => UserAnswersReader[B]): UserAnswersReader[Option[B]] =
      a.reader(s"Reader for ${a.path} failed before reaching predicate")
        .flatMap {
          x =>
            if (predicate(x)) {
              next.map(Option(_))
            } else {
              none[B].pure[UserAnswersReader]
            }
        }
  }

  implicit class GettableAsReaderOps[A](a: Gettable[A]) {

    /**
      * Returns a reader for [[Gettable]], which will succeed with an [[A]]  if the value is defined
      * and will fail if it is not defined
      */

    def reader(implicit reads: Reads[A]): UserAnswersReader[A] = reader(None)

    def reader(message: String)(implicit reads: Reads[A]): UserAnswersReader[A] = reader(Some(message))

    private def reader(message: Option[String])(implicit reads: Reads[A]): UserAnswersReader[A] = {
      val fn: UserAnswers => EitherType[A] = _.get(a) match {
        case Some(value) => Right(value)
        case None        => Left(ReaderError(a, message))
      }
      UserAnswersReader(fn)
    }

    def mandatoryReader(predicate: A => Boolean)(implicit reads: Reads[A]): UserAnswersReader[A] = {
      val fn: UserAnswers => EitherType[A] = _.get(a) match {
        case Some(value) if predicate(value) => Right(value)
        case _                               => Left(ReaderError(a))
      }
      UserAnswersReader(fn)
    }

    def optionalReader(implicit reads: Reads[A]): UserAnswersReader[Option[A]] = {
      val fn: UserAnswers => EitherType[Option[A]] = ua => Right(ua.get(a))
      UserAnswersReader(fn)
    }
  }

  implicit class JsArrayGettableAsReaderOps(jsArray: Gettable[JsArray]) {

    def arrayReader(implicit reads: Reads[JsArray]): UserAnswersReader[JsArray] = {
      val fn: UserAnswers => EitherType[JsArray] = ua => Right(ua.get(jsArray).getOrElse(JsArray()))
      UserAnswersReader(fn)
    }

    def fieldReader[T](page: Index => Gettable[T])(implicit rds: Reads[T]): UserAnswersReader[Seq[T]] = {
      val fn: UserAnswers => EitherType[Seq[T]] = ua => {
        Right {
          ua.get(jsArray).getOrElse(JsArray()).value.indices.foldLeft[Seq[T]](Nil) {
            (acc, i) =>
              ua.get(page(Index(i))) match {
                case Some(value) => acc :+ value
                case None        => acc
              }
          }
        }
      }
      UserAnswersReader(fn)
    }
  }
}
