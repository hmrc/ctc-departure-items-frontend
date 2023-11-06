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
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.DeclarationTypeItemLevel
import models.PackingType.{Bulk, Other, Unpacked}
import models.reference._
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  def getCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/CountryCodesFullList"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  def getCountryCodesForAddress()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/CountryCodesForAddress"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CountryCode]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/CountryWithoutZip"
    http.GET[Seq[CountryCode]](serviceUrl, headers = version2Header)
  }

  def getPackageTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl                         = s"${config.referenceDataUrl}/lists/KindOfPackages"
    implicit val reads: Reads[PackageType] = PackageType.reads(Other)
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)
  }

  def getPackageTypesBulk()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl                         = s"${config.referenceDataUrl}/lists/KindOfPackagesBulk"
    implicit val reads: Reads[PackageType] = PackageType.reads(Bulk)
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)
  }

  def getPackageTypesUnpacked()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[PackageType]] = {
    val serviceUrl                         = s"${config.referenceDataUrl}/lists/KindOfPackagesUnpacked"
    implicit val reads: Reads[PackageType] = PackageType.reads(Unpacked)
    http.GET[Seq[PackageType]](serviceUrl, headers = version2Header)
  }

  def getAdditionalReferences()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[AdditionalReference]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/AdditionalReference"
    http.GET[Seq[AdditionalReference]](serviceUrl, headers = version2Header)
  }

  def getAdditionalInformationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[AdditionalInformation]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/AdditionalInformation"
    http.GET[Seq[AdditionalInformation]](serviceUrl, headers = version2Header)
  }

  def getTransportChargesMethodOfPaymentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[TransportChargesMethodOfPayment]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/TransportChargesMethodOfPayment"
    http.GET[Seq[TransportChargesMethodOfPayment]](serviceUrl, headers = version2Header)
  }

  def getDeclarationTypeItemLevel()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[DeclarationTypeItemLevel]] = {
    val serviceUrl = s"${config.referenceDataUrl}/lists/DeclarationTypeItemLevel"
    http.GET[Seq[DeclarationTypeItemLevel]](serviceUrl, headers = version2Header)
  }

  def getSupplyChainActorTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[SupplyChainActorType]] = {
    val url = s"${config.referenceDataUrl}/lists/AdditionalSupplyChainActorRoleCode"
    http.GET[Seq[SupplyChainActorType]](url, headers = version2Header)
  }

  private def version2Header: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/vnd.hmrc.2.0+json"
  )

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[Seq[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          (response.json \ "data").validate[Seq[A]] match {
            case JsSuccess(Nil, _) =>
              throw new NoReferenceDataFoundException
            case JsSuccess(value, _) =>
              value
            case JsError(errors) =>
              throw JsResultException(errors)
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          throw new Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}")
      }
    }
}

object ReferenceDataConnector {

  class NoReferenceDataFoundException extends Exception("The reference data call was successful but the response body is empty.")
}
