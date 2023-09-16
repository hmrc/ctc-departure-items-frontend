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

import connectors.ReferenceDataConnector
import models.{DeclarationTypeItemLevel, UserAnswers}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationTypeService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getDeclarationTypeItemLevel()(implicit hc: HeaderCarrier): Future[Seq[DeclarationTypeItemLevel]] = referenceDataConnector
    .getDeclarationTypeItemLevel()
    .map(sort)

  private def sort(declarationType: Seq[DeclarationTypeItemLevel]): Seq[DeclarationTypeItemLevel] =
    declarationType.sortBy(_.toString.toLowerCase)

}