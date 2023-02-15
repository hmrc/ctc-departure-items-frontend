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

import models.reference.UnLocode

case class UnLocodeList(unLocodes: Seq[UnLocode]) {

  def getAll: Seq[UnLocode] =
    unLocodes

  def getUnLocode(unLocodeExtendedCode: String): Option[UnLocode] =
    unLocodes.find(_.unLocodeExtendedCode == unLocodeExtendedCode)

  override def equals(obj: Any): Boolean = obj match {
    case x: UnLocodeList => x.getAll == getAll
    case _               => false
  }

}

object UnLocodeList {

  def apply(unLocodes: Seq[UnLocode]): UnLocodeList =
    new UnLocodeList(unLocodes)
}
