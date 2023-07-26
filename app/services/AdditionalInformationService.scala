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

package services

import config.PhaseConfig
import connectors.ReferenceDataConnector
import models.{Phase, SelectableList}
import models.reference.AdditionalInformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdditionalInformationService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  private def getPostTransitionAdditionalInformationTypes()(implicit hc: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    referenceDataConnector
      .getAdditionalInformationTypes()
      .map {
        _.filter(_.code != "30600")
      }
      .map(sort)

  private def getTransitionAdditionalInformationTypes()(implicit hc: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    referenceDataConnector
      .getAdditionalInformationTypes()
      .map(sort)

  def getAdditionalInformationTypes(implicit phaseConfig: PhaseConfig, headerCarrier: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    phaseConfig.phase match {
      case Phase.Transition     => getTransitionAdditionalInformationTypes
      case Phase.PostTransition => getPostTransitionAdditionalInformationTypes
    }

  private def sort(additionalInformationTypes: Seq[AdditionalInformation]): SelectableList[AdditionalInformation] =
    SelectableList(additionalInformationTypes.sortBy(_.description.toLowerCase))
}
