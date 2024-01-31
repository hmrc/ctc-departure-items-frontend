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

package models.journeyDomain.item.additionalReferences

import config.Constants.AdditionalReference._
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, Read, Stage, UserAnswersReader}
import models.reference.AdditionalReference
import models.{Index, Mode, Phase, UserAnswers}
import pages.item.additionalReference.index._
import pages.sections.additionalReference.AdditionalReferencesSection
import play.api.mvc.Call

case class AdditionalReferenceDomain(
  `type`: AdditionalReference,
  number: Option[String]
)(itemIndex: Index, additionalReferenceIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = AdditionalReferenceDomain.asString(`type`, number)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage, phase: Phase): Option[Call] = Some {
    stage match {
      case AccessingJourney =>
        controllers.item.additionalReference.index.routes.AdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex, additionalReferenceIndex)
      case CompletingJourney =>
        controllers.item.additionalReference.routes.AddAnotherAdditionalReferenceController.onPageLoad(userAnswers.lrn, mode, itemIndex)
    }
  }
}

object AdditionalReferenceDomain {

  def asString(`type`: AdditionalReference, number: Option[String]): String = `type`.toString + number.fold("") {
    value => s" - $value"
  }

  def userAnswersReader(itemIndex: Index, additionalReferenceIndex: Index): Read[AdditionalReferenceDomain] =
    AdditionalReferencePage(itemIndex, additionalReferenceIndex).reader.to {
      additionalReference =>
        otherAdditionalReferenceNumbers(itemIndex, additionalReferenceIndex, additionalReference).to {
          otherAdditionalReferenceNumbers =>
            val numberReader = {
              lazy val reader = AdditionalReferenceNumberPage(itemIndex, additionalReferenceIndex).reader
              additionalReference.value match {
                case C651 | C658 =>
                  reader.toOption
                case _ =>
                  if (isReferenceNumberRequired(otherAdditionalReferenceNumbers)) {
                    reader.toOption
                  } else {
                    AddAdditionalReferenceNumberYesNoPage(itemIndex, additionalReferenceIndex).filterOptionalDependent(identity) {
                      reader
                    }
                  }
              }
            }
            numberReader.map(AdditionalReferenceDomain(additionalReference, _)(itemIndex, additionalReferenceIndex))
        }
    }

  def otherAdditionalReferenceNumbers(
    itemIndex: Index,
    thisIndex: Index,
    typeAtThisIndex: AdditionalReference
  ): Read[Seq[Option[String]]] = {
    val fn: UserAnswers => Seq[Option[String]] = userAnswers => {
      val numberOfAdditionalReferences = userAnswers.get(AdditionalReferencesSection(itemIndex)).map(_.value.size).getOrElse(0)
      (0 until numberOfAdditionalReferences)
        .map(Index(_))
        .filterNot(_ == thisIndex)
        .foldLeft[Seq[Option[String]]](Nil) {
          (acc, index) =>
            if (userAnswers.get(AdditionalReferencePage(itemIndex, index)).contains(typeAtThisIndex)) {
              acc :+ userAnswers.get(AdditionalReferenceNumberPage(itemIndex, index))
            } else {
              acc
            }
        }
    }
    UserAnswersReader.success(fn)
  }

  def isReferenceNumberRequired(otherAdditionalReferenceNumbers: Seq[Option[String]]): Boolean =
    otherAdditionalReferenceNumbers.exists(_.isEmpty)
}
