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

package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Field, Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

trait StringFieldBehaviours extends FieldBehaviours {

  def fieldWithExactLength(form: Form[?], fieldName: String, exactLength: Int, lengthError: FormError): Unit =
    s"must not bind strings where the length is not equal to $exactLength" in {

      forAll(stringsWithLengthNotEqual(exactLength, Gen.numChar) -> "incorrectLength") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLength(form: Form[?], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors must contain only lengthError
      }
    }

  def fieldWithMaxLength(
    form: Form[?],
    fieldName: String,
    maxLength: Int,
    lengthError: FormError,
    gen: Gen[String]
  ): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(gen -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMinLength(form: Form[?], fieldName: String, minLength: Int, lengthError: FormError): Unit =
    s"must not bind strings shorter than $minLength characters" in {

      forAll(stringsWithLength(minLength - 1) -> "shortString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithInvalidCharacters(form: Form[?], fieldName: String, error: FormError, length: Int = 100): Unit =
    "must not bind strings with invalid characters" in {

      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~<>,±üçñèé]{$length}")

      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(error)
      }
    }

  def fieldWithInvalidInputCL234(form: Form[?], fieldName: String, error: FormError): Unit =
    "must not bind strings with value '0' when in CL234" in {

      val result: Field = form.bind(Map(fieldName -> "0")).apply(fieldName)
      result.errors must contain(error)
    }

  def fieldThatBindsUniqueData(form: Form[?], fieldName: String, values: Seq[String], uniqueError: FormError): Unit = {

    "bind unique data" in {

      forAll(nonEmptyString.retryUntil(!values.contains(_))) {
        value =>
          val result = form.bind(Map(fieldName -> value)).apply(fieldName)
          result.value.value mustEqual value
      }
    }

    "not bind non-unique data" in {

      forAll(Gen.oneOf(values)) {
        value =>
          val result = form.bind(Map(fieldName -> value)).apply(fieldName)
          result.errors mustEqual Seq(uniqueError)
      }
    }
  }
}
