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

import models.Phase
import models.Phase.{PostTransition, Transition}

trait PhaseConfig {
  val phase: Phase
  val maxItemDescriptionLength: Int
  val maxShippingMarkLength: Int
  val maxAdditionalReferenceNumLength: Int
  val maxNumberOfPackages: Int
  val decimalPlaces: Int
  val characterCount: Int
}

class TransitionConfig() extends PhaseConfig {
  override val phase: Phase                         = Transition
  override val maxItemDescriptionLength: Int        = 280
  override val maxShippingMarkLength: Int           = 42
  override val maxAdditionalReferenceNumLength: Int = 35
  override val maxNumberOfPackages: Int             = 99999
  override val decimalPlaces: Int                   = 3
  override val characterCount: Int                  = 11
}

class PostTransitionConfig() extends PhaseConfig {
  override val phase: Phase                         = PostTransition
  override val maxItemDescriptionLength: Int        = 512
  override val maxShippingMarkLength: Int           = 512
  override val maxAdditionalReferenceNumLength: Int = 70
  override val maxNumberOfPackages: Int             = 99999999
  override val decimalPlaces: Int                   = 6
  override val characterCount: Int                  = 16
}
