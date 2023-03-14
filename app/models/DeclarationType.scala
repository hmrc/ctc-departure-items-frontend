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

sealed trait DeclarationType extends Radioable[DeclarationType] {
  override val messageKeyPrefix: String = DeclarationType.messageKeyPrefix
}

object DeclarationType extends EnumerableType[DeclarationType] {

  case object T1 extends WithName("T1") with DeclarationType
  case object T2 extends WithName("T2") with DeclarationType
  case object T2F extends WithName("T2F") with DeclarationType
  case object TIR extends WithName("TIR") with DeclarationType
  case object T extends WithName("T") with DeclarationType

  val messageKeyPrefix: String = "item.declarationType"

  val values: Seq[DeclarationType] = Seq(
    T1,
    T2,
    T2F,
    TIR,
    T
  )

  val itemValues: Seq[DeclarationType] = Seq(
    T1,
    T2,
    T2F
  )
}
