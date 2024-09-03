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

import cats.implicits._
import models.journeyDomain.{Pages, Read, ReaderSuccess, UserAnswersReader}
import play.api.libs.json._

import scala.annotation.nowarn

package object models {

  implicit class RichJsObject(jsObject: JsObject) {

    def setObject(path: JsPath, value: JsValue): JsResult[JsObject] =
      jsObject.set(path, value).flatMap(_.validate[JsObject])

    def removeObject(path: JsPath): JsResult[JsObject] =
      jsObject.remove(path).flatMap(_.validate[JsObject])
  }

  implicit class RichJsArray(arr: JsArray) {

    def zipWithIndex: List[(JsValue, Index)] = arr.value.toList.zipWithIndex.map(
      x => (x._1, Index(x._2))
    )

    def filterWithIndex(f: (JsValue, Index) => Boolean): Seq[(JsValue, Index)] =
      arr.zipWithIndex.filter {
        case (value, i) => f(value, i)
      }

    def isEmpty: Boolean = arr.value.isEmpty

    def nonEmpty: Boolean = !isEmpty

    def traverse[T](implicit userAnswersReader: (Index, Pages) => UserAnswersReader[T]): Read[Seq[T]] = pages =>
      arr.zipWithIndex
        .foldLeft[UserAnswersReader[Seq[T]]](UserAnswersReader.success[Seq[T]](Nil).apply(pages))({
          case (acc, (_, index)) =>
            acc.flatMap {
              case ReaderSuccess(ts, pages) =>
                userAnswersReader(index, pages).map {
                  case ReaderSuccess(t, pages) =>
                    ReaderSuccess(ts :+ t, pages)
                }
            }
        })

    def validateAsListOf[T](implicit reads: Reads[T]): Seq[T] =
      arr.value.flatMap(_.validate[T].asOpt).toSeq

    def validateAsAListOf[T](implicit reads: Int => Reads[T]): Seq[T] =
      arr.value.zipWithIndex.flatMap {
        case (value, index) => value.validate[T](reads(index)).asOpt
      }.toSeq
  }

  implicit class RichOptionalJsArray(arr: Option[JsArray]) {

    def mapWithIndex[T](f: (JsValue, Index) => Option[T]): Seq[T] =
      arr
        .map {
          _.zipWithIndex.flatMap {
            case (value, i) => f(value, i)
          }
        }
        .getOrElse(Nil)

    def validate[T](implicit rds: Reads[T]): Option[T] =
      arr.flatMap(_.validate[T].asOpt)

    def length: Int = arr.getOrElse(JsArray()).value.length

  }

  implicit class RichJsValue(jsValue: JsValue) {

    def set(path: JsPath, value: JsValue): JsResult[JsValue] =
      (path.path, jsValue) match {

        case (Nil, _) =>
          JsError("path cannot be empty")

        case ((_: RecursiveSearch) :: _, _) =>
          JsError("recursive search not supported")

        case ((n: IdxPathNode) :: Nil, _) =>
          setIndexNode(n, jsValue, value)

        case ((n: KeyPathNode) :: Nil, _) =>
          setKeyNode(n, jsValue, value)

        case (first :: second :: rest, oldValue) =>
          Reads
            .optionNoError(Reads.at[JsValue](JsPath(first :: Nil)))
            .reads(oldValue)
            .flatMap {
              opt =>
                opt
                  .map(JsSuccess(_))
                  .getOrElse {
                    second match {
                      case _: KeyPathNode =>
                        JsSuccess(Json.obj())
                      case _: IdxPathNode =>
                        JsSuccess(Json.arr())
                      case _: RecursiveSearch =>
                        JsError("recursive search is not supported")
                    }
                  }
                  .flatMap {
                    _.set(JsPath(second :: rest), value).flatMap {
                      newValue =>
                        oldValue.set(JsPath(first :: Nil), newValue)
                    }
                  }
            }
      }

    private def setIndexNode(node: IdxPathNode, oldValue: JsValue, newValue: JsValue): JsResult[JsValue] = {

      val index: Int = node.idx

      oldValue match {
        case oldValue: JsArray if index >= 0 && index <= oldValue.value.length =>
          if (index == oldValue.value.length) {
            JsSuccess(oldValue.append(newValue))
          } else {
            JsSuccess(JsArray(oldValue.value.updated(index, newValue)))
          }
        case oldValue: JsArray =>
          JsError(s"array index out of bounds: $index, $oldValue")
        case _ =>
          JsError(s"cannot set an index on $oldValue")
      }
    }

    private def removeIndexNode(node: IdxPathNode, valueToRemoveFrom: JsArray): JsResult[JsValue] = {
      val index: Int = node.idx

      valueToRemoveFrom match {
        case valueToRemoveFrom: JsArray if index >= 0 && index < valueToRemoveFrom.value.length =>
          val updatedJsArray = valueToRemoveFrom.value.slice(0, index) ++ valueToRemoveFrom.value.slice(index + 1, valueToRemoveFrom.value.size)
          JsSuccess(JsArray(updatedJsArray))
        case valueToRemoveFrom: JsArray => JsError(s"array index out of bounds: $index, $valueToRemoveFrom")
      }
    }

    private def setKeyNode(node: KeyPathNode, oldValue: JsValue, newValue: JsValue): JsResult[JsValue] = {

      val key = node.key

      oldValue match {
        case oldValue: JsObject =>
          JsSuccess(oldValue + (key -> newValue))
        case _ =>
          JsError(s"cannot set a key on $oldValue")
      }
    }

    @nowarn("msg=match may not be exhaustive")
    // scalastyle:off cyclomatic.complexity
    def remove(path: JsPath): JsResult[JsValue] =
      (path.path, jsValue) match {
        case (Nil, _)                                                                  => JsError("path cannot be empty")
        case ((n: KeyPathNode) :: Nil, value: JsObject) if value.keys.contains(n.key)  => JsSuccess(value - n.key)
        case ((n: KeyPathNode) :: Nil, value: JsObject) if !value.keys.contains(n.key) => JsError("cannot find value at path")
        case ((n: IdxPathNode) :: Nil, value: JsArray)                                 => removeIndexNode(n, value)
        case ((_: KeyPathNode) :: Nil, _)                                              => JsError(s"cannot remove a key on $jsValue")
        case (first :: second :: rest, oldValue) =>
          Reads
            .optionNoError(Reads.at[JsValue](JsPath(first :: Nil)))
            .reads(oldValue)
            .flatMap {
              (opt: Option[JsValue]) =>
                opt
                  .map(JsSuccess(_))
                  .getOrElse {
                    second match {
                      case _: KeyPathNode =>
                        JsSuccess(Json.obj())
                      case _: IdxPathNode =>
                        JsSuccess(Json.arr())
                      case _: RecursiveSearch =>
                        JsError("recursive search is not supported")
                    }
                  }
                  .flatMap {
                    _.remove(JsPath(second :: rest)).flatMap {
                      newValue =>
                        oldValue.set(JsPath(first :: Nil), newValue)
                    }
                  }
            }
      }
    // scalastyle:on cyclomatic.complexity
  }

  implicit class RichString(string: String) {

    def removeSpaces(): String =
      string.foldLeft("") {
        (acc, c) =>
          acc + c.toString.trim
      }

    def capitalise(n: Int): String = string.take(n).toUpperCase + string.drop(n)

  }

  implicit def successfulReads[T](value: T): Reads[T] = Reads {
    _ => JsSuccess(value)
  }

  implicit class RichBigDecimal(value: BigDecimal) {

    def isMoreThan(that: BigDecimal): Boolean = value.compareTo(that) > 0
    def isEqualTo(that: BigDecimal): Boolean  = value.compareTo(that) == 0
  }
}
