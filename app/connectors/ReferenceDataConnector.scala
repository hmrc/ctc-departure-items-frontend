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

import cats.Order
import cats.data.NonEmptySet
import config.FrontendAppConfig
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.DeclarationTypeItemLevel
import models.PackingType.{Bulk, Other, Unpacked}
import models.reference._
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClientV2) extends Logging {

  def getCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[Country]] = {
    val url = url"${config.referenceDataUrl}/lists/CountryCodesFullList"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[Country]]
  }

  def getCountryCodesForAddress()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[Country]] = {
    val url = url"${config.referenceDataUrl}/lists/CountryCodesForAddress"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[Country]]
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[CountryCode]] = {
    val url = url"${config.referenceDataUrl}/lists/CountryWithoutZip"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[CountryCode]]
  }

  def getPackageTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackages"
    implicit val reads: Reads[PackageType] = PackageType.reads(Other)
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[PackageType]]
  }

  def getPackageTypesBulk()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackagesBulk"
    implicit val reads: Reads[PackageType] = PackageType.reads(Bulk)
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[PackageType]]
  }

  def getPackageTypesUnpacked()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[PackageType]] = {
    val url                                = url"${config.referenceDataUrl}/lists/KindOfPackagesUnpacked"
    implicit val reads: Reads[PackageType] = PackageType.reads(Unpacked)
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[PackageType]]
  }

  def getAdditionalReferences()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[AdditionalReference]] = {
    val url = url"${config.referenceDataUrl}/lists/AdditionalReference"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[AdditionalReference]]
  }

  def getDocumentTypeExcise(docType: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[DocTypeExcise] = {
    val url = url"${config.referenceDataUrl}/lists/DocumentTypeExcise"
    http
      .get(url)
      .transform(_.withQueryStringParameters("data.code" -> docType))
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[DocTypeExcise]]
      .map(_.head)
  }

  def getCUSCode(cusCode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CUSCode] = {
    val url = url"${config.referenceDataUrl}/lists/CUSCode"
    http
      .get(url)
      .transform(_.withQueryStringParameters("data.code" -> cusCode))
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[CUSCode]]
      .map(_.head)
  }

  def getAdditionalInformationTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[AdditionalInformation]] = {
    val url = url"${config.referenceDataUrl}/lists/AdditionalInformation"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[AdditionalInformation]]
  }

  def getTransportChargesMethodOfPaymentTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[TransportChargesMethodOfPayment]] = {
    val url = url"${config.referenceDataUrl}/lists/TransportChargesMethodOfPayment"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[TransportChargesMethodOfPayment]]
  }

  def getDeclarationTypeItemLevel()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[DeclarationTypeItemLevel]] = {
    val url = url"${config.referenceDataUrl}/lists/DeclarationTypeItemLevel"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[DeclarationTypeItemLevel]]
  }

  def getSupplyChainActorTypes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[SupplyChainActorType]] = {
    val url = url"${config.referenceDataUrl}/lists/AdditionalSupplyChainActorRoleCode"
    http
      .get(url)
      .setHeader(version2Header: _*)
      .execute[NonEmptySet[SupplyChainActorType]]
  }

  private def version2Header: Seq[(String, String)] = Seq(
    HeaderNames.Accept -> "application/vnd.hmrc.2.0+json"
  )

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A], order: Order[A]): HttpReads[NonEmptySet[A]] =
    (_: String, url: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          (response.json \ "data").validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              throw new NoReferenceDataFoundException(url)
            case JsSuccess(head :: tail, _) =>
              NonEmptySet.of(head, tail: _*)
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

  class NoReferenceDataFoundException(url: String) extends Exception(s"The reference data call was successful but the response body is empty: $url")
}
