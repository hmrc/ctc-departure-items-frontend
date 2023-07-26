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

sealed trait AdditionalInformationService {

  val referenceDataConnector: ReferenceDataConnector

  implicit val ec: ExecutionContext

  def getAdditionalInformationTypes()(implicit phaseConfig: PhaseConfig, hc: HeaderCarrier): Future[SelectableList[AdditionalInformation]]

  def sort(additionalInformationTypes: Seq[AdditionalInformation]): SelectableList[AdditionalInformation] =
    SelectableList(additionalInformationTypes.sortBy(_.description.toLowerCase))
}

class TransitionAdditionalInformationService @Inject() (
  override val referenceDataConnector: ReferenceDataConnector
)(implicit override val ec: ExecutionContext)
    extends AdditionalInformationService {

  override def getAdditionalInformationTypes()(implicit phaseConfig: PhaseConfig, headerCarrier: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    referenceDataConnector
      .getAdditionalInformationTypes()
      .map(sort)
}

class PostTransitionAdditionalInformationService @Inject() (
  override val referenceDataConnector: ReferenceDataConnector
)(implicit override val ec: ExecutionContext)
    extends AdditionalInformationService {

  override def getAdditionalInformationTypes()(implicit phaseConfig: PhaseConfig, headerCarrier: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    referenceDataConnector
      .getAdditionalInformationTypes()
      .map {
        _.filter(_.code != "30600")
      }
      .map(sort)
}
