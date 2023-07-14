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

package connectors

import config.FrontendAppConfig
import models.PackingType.{Bulk, Other, Unpacked}
import models.reference._
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.Reads
import sttp.model.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  def getCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/CountryCodesFullList"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  def getPackageTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/KindOfPackages"
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)(PackageType.httpReads(Other), hc, ec)
  }

  def getPackageTypesBulk()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/KindOfPackagesBulk"
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)(PackageType.httpReads(Bulk), hc, ec)
  }

  def getPackageTypesUnpacked()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/KindOfPackagesUnpacked"
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)(PackageType.httpReads(Unpacked), hc, ec)
  }

  def getAdditionalReferences()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[AdditionalReference]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/AdditionalReference"
    http.GET[Seq[AdditionalReference]](serviceUrl, headers = version2Header)
  }

  def getAdditionalInformationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[AdditionalInformation]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/AdditionalInformation"
    http.GET[Seq[AdditionalInformation]](serviceUrl, headers = version2Header)
  }

  def getMethodOfPaymentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[MethodOfPayment]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/MethodOfPayment"
    http.GET[Seq[MethodOfPayment]](serviceUrl, headers = version2Header)
  }

  private def version2Header: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/vnd.hmrc.2.0+json"
  )

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[Seq[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          val referenceData = (response.json \ "data").getOrElse(
            throw new IllegalStateException("[ReferenceDataConnector][responseHandlerGeneric] Reference data could not be parsed")
          )

          referenceData.as[Seq[A]]
        case NO_CONTENT =>
          Nil
        case NOT_FOUND =>
          logger.warn("[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned NOT_FOUND")
          throw new IllegalStateException("[ReferenceDataConnector][responseHandlerGeneric] Reference data could not be found")
        case other =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid downstream status $other")
          throw new IllegalStateException(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid Downstream Status $other")
      }
    }
}
