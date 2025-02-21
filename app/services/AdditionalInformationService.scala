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

package services

import config.Constants.AdditionalInformation._
import connectors.ReferenceDataConnector
import models.SelectableList
import models.reference.AdditionalInformation
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

sealed trait AdditionalInformationService {

  val referenceDataConnector: ReferenceDataConnector

  implicit val ec: ExecutionContext

  def predicate: AdditionalInformation => Boolean

  def getAdditionalInformationTypes()(implicit hc: HeaderCarrier): Future[SelectableList[AdditionalInformation]] =
    referenceDataConnector
      .getAdditionalInformationTypes()
      .map(_.resolve())
      .map(_.toSeq)
      .map(_.filter(predicate))
      .map(SelectableList(_))
}

class TransitionAdditionalInformationService @Inject() (
  override val referenceDataConnector: ReferenceDataConnector
)(implicit override val ec: ExecutionContext)
    extends AdditionalInformationService {

  override def predicate: AdditionalInformation => Boolean = _ => true
}

class PostTransitionAdditionalInformationService @Inject() (
  override val referenceDataConnector: ReferenceDataConnector
)(implicit override val ec: ExecutionContext)
    extends AdditionalInformationService {
  override def predicate: AdditionalInformation => Boolean = _.code != Type30600

}
