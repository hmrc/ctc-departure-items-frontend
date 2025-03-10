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

package utils

import viewmodels.ListItem

package object cyaHelpers {

  implicit class RichListItems(value: Seq[Either[ListItem, ListItem]]) {

    /** @param predicate
      *   If true, the section is mandatory. If false, the section is optional.
      * @return
      *   The original list with removeUrl removed if there is only one list item and the section is mandatory.
      */
    def checkRemoveLinks(predicate: Boolean): Seq[Either[ListItem, ListItem]] =
      value match {
        case Right(value) :: Nil if predicate => Seq(Right(value.copy(removeUrl = None)))
        case Left(value) :: Nil if predicate  => Seq(Left(value.copy(removeUrl = None)))
        case _                                => value
      }
  }
}
