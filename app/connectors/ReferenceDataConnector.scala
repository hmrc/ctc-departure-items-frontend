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

package connectors

import cats.Order
import cats.data.NonEmptySet
import config.FrontendAppConfig
import connectors.ReferenceDataConnector.*
import models.DeclarationTypeItemLevel
import models.PackingType.{Bulk, Other, Unpacked}
import models.reference.*
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClientV2) extends Logging {

  private def get[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Responses[T]] =
    http
      .get(url)
      .setHeader(HeaderNames.Accept -> {
        val version = if (config.isPhase6Enabled) "2.0" else "1.0"
        s"application/vnd.hmrc.$version+json"
      })
      .execute[Responses[T]]

  private def getOne[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Response[T]] =
    get[T](url).map(_.map(_.head))

  def getCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[Country]] = {
    implicit val reads: Reads[Country] = Country.reads(config)
    val url                            = url"${config.referenceDataUrl}/lists/CountryCodesFullList"
    get[Country](url)
  }

  def getCountryCodesForAddress()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[Country]] = {
    implicit val reads: Reads[Country] = Country.reads(config)
    val url                            = url"${config.referenceDataUrl}/lists/CountryCodesForAddress"
    get[Country](url)
  }

  def getCountryCodeCommonTransit(country: Country)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[Country]] = {
    val queryParameters                = Country.queryParams(country.code.code)(config)
    implicit val reads: Reads[Country] = Country.reads(config)
    val url                            = url"${config.referenceDataUrl}/lists/CountryCodesCommonTransit?$queryParameters"
    getOne[Country](url)
  }

  def getCountriesWithoutZipCountry(code: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[CountryCode]] = {
    val queryParameters                    = CountryCode.queryParams(code)(config)
    implicit val reads: Reads[CountryCode] = CountryCode.reads(config)
    val url                                = url"${config.referenceDataUrl}/lists/CountryWithoutZip?$queryParameters"
    getOne[CountryCode](url)
  }

  def getPackageTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackages"
    implicit val reads: Reads[PackageType] = PackageType.reads(Other)(config)
    get[PackageType](url)
  }

  def getPackageTypesBulk()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackagesBulk"
    implicit val reads: Reads[PackageType] = PackageType.reads(Bulk)(config)
    get[PackageType](url)
  }

  def getPackageTypesUnpacked()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackagesUnpacked"
    implicit val reads: Reads[PackageType] = PackageType.reads(Unpacked)(config)
    get[PackageType](url)
  }

  def getAdditionalReferences()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[AdditionalReference]] = {
    val url                                        = url"${config.referenceDataUrl}/lists/AdditionalReference"
    implicit val reads: Reads[AdditionalReference] = AdditionalReference.reads(config)
    get[AdditionalReference](url)
  }

  def getDocumentTypeExcise(docType: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[DocTypeExcise]] = {
    val queryParameters                      = DocTypeExcise.queryParams(docType)(config)
    implicit val reads: Reads[DocTypeExcise] = DocTypeExcise.reads(config)
    val url                                  = url"${config.referenceDataUrl}/lists/DocumentTypeExcise?$queryParameters"
    getOne[DocTypeExcise](url)
  }

  def getCUSCode(cusCode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[CUSCode]] = {
    val queryParameters                = CUSCode.queryParams(cusCode)(config)
    implicit val reads: Reads[CUSCode] = CUSCode.reads(config)
    val url                            = url"${config.referenceDataUrl}/lists/CUSCode?$queryParameters"
    getOne[CUSCode](url)
  }

  def getAdditionalInformationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[AdditionalInformation]] = {
    val url                                          = url"${config.referenceDataUrl}/lists/AdditionalInformation"
    implicit val reads: Reads[AdditionalInformation] = AdditionalInformation.reads(config)
    get[AdditionalInformation](url)
  }

  def getTransportChargesMethodOfPaymentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[TransportChargesMethodOfPayment]] = {
    val url                                                    = url"${config.referenceDataUrl}/lists/TransportChargesMethodOfPayment"
    implicit val reads: Reads[TransportChargesMethodOfPayment] = TransportChargesMethodOfPayment.reads(config)
    get[TransportChargesMethodOfPayment](url)
  }

  def getDeclarationTypeItemLevel()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[DeclarationTypeItemLevel]] = {
    val url                                             = url"${config.referenceDataUrl}/lists/DeclarationTypeItemLevel"
    implicit val reads: Reads[DeclarationTypeItemLevel] = DeclarationTypeItemLevel.reads(config)
    get[DeclarationTypeItemLevel](url)
  }

  def getSupplyChainActorTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[SupplyChainActorType]] = {
    val url                                         = url"${config.referenceDataUrl}/lists/AdditionalSupplyChainActorRoleCode"
    implicit val reads: Reads[SupplyChainActorType] = SupplyChainActorType.reads(config)
    get[SupplyChainActorType](url)
  }

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A], order: Order[A]): HttpReads[Responses[A]] =
    (_: String, url: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          val json = if (config.isPhase6Enabled) response.json else response.json \ "data"
          json.validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              Left(NoReferenceDataFoundException(url))
            case JsSuccess(head :: tail, _) =>
              Right(NonEmptySet.of(head, tail*))
            case JsError(errors) =>
              Left(JsResultException(errors))
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          Left(Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}"))
      }
}

object ReferenceDataConnector {

  type Responses[T] = Either[Exception, NonEmptySet[T]]
  type Response[T]  = Either[Exception, T]

  class NoReferenceDataFoundException(url: String) extends Exception(s"The reference data call was successful but the response body is empty: $url")
}
