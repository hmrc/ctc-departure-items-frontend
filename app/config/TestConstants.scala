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

package config

import config.Constants.{T, T1, T2, T2F, TIR}
import models.DeclarationTypeItemLevel

object TestConstants {

  val declarationTypeT1: DeclarationTypeItemLevel  = DeclarationTypeItemLevel(T1, "t1 description")
  val declarationTypeT2: DeclarationTypeItemLevel  = DeclarationTypeItemLevel(T2, "t2 description")
  val declarationTypeT2F: DeclarationTypeItemLevel = DeclarationTypeItemLevel(T2F, "t2f description")
  val declarationTypeT: DeclarationTypeItemLevel   = DeclarationTypeItemLevel(T, "t description")
  val declarationTypeTIR: DeclarationTypeItemLevel = DeclarationTypeItemLevel(TIR, "tir description")

  val declarationTypeValues: Seq[DeclarationTypeItemLevel] = Seq(
    declarationTypeT1,
    declarationTypeT2,
    declarationTypeT2F,
    declarationTypeT,
    declarationTypeTIR
  )

  val declarationTypeItemValues: Seq[DeclarationTypeItemLevel] = Seq(
    declarationTypeT1,
    declarationTypeT2,
    declarationTypeT2F
  )
}
