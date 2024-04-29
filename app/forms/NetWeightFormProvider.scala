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

package forms

import config.PhaseConfig
import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.validation.Constraint

import javax.inject.Inject

sealed abstract class NetWeightFormProvider(implicit phaseConfig: PhaseConfig) extends Mappings {

  def maxValueConstraint(prefix: String, grossWeight: BigDecimal): Option[Constraint[BigDecimal]]

  def apply(prefix: String, grossWeight: BigDecimal): Form[BigDecimal] = {
    val decimalPlaces  = phaseConfig.decimalPlaces
    val characterCount = phaseConfig.characterCount
    Form(
      "value" -> bigDecimal(
        decimalPlaces,
        characterCount,
        s"$prefix.error.required",
        s"$prefix.error.invalidCharacters",
        s"$prefix.error.invalidFormat",
        s"$prefix.error.invalidValue.${phaseConfig.phase}",
        Seq(decimalPlaces.toString, characterCount.toString)
      ).verifying(
        maxValueConstraint(prefix, grossWeight).toSeq: _*
      )
    )
  }
}

class TransitionNetWeightFormProvider @Inject() (implicit phaseConfig: PhaseConfig) extends NetWeightFormProvider {

  override def maxValueConstraint(prefix: String, grossWeight: BigDecimal): Option[Constraint[BigDecimal]] =
    None
}

class PostTransitionNetWeightFormProvider @Inject() (implicit phaseConfig: PhaseConfig) extends NetWeightFormProvider {

  override def maxValueConstraint(prefix: String, grossWeight: BigDecimal): Option[Constraint[BigDecimal]] =
    Some(maximumValue(grossWeight, s"$prefix.error.maximum"))
}
