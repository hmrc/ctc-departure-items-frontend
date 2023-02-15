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

package models

import models.reference.CustomsOffice
import play.api.Logging
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsArray, JsError, JsSuccess, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

case class CustomsOfficeList(customsOffices: Seq[CustomsOffice]) {

  def getAll: Seq[CustomsOffice] =
    customsOffices

  def getCustomsOffice(id: String): Option[CustomsOffice] =
    customsOffices.find(_.id == id)

  def filterNot(customOfficeIds: Seq[String]): Seq[CustomsOffice] =
    customsOffices.filterNot(
      office => customOfficeIds.contains(office.id)
    )
}

object CustomsOfficeList extends Logging {

  def apply(customsOffices: Seq[CustomsOffice]): CustomsOfficeList =
    new CustomsOfficeList(customsOffices)

  implicit val responseHandlerCustomsOfficeList: HttpReads[CustomsOfficeList] =
    (_: String, _: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          CustomsOfficeList(
            response.json
              .as[Seq[CustomsOffice]]
          )
        case NOT_FOUND =>
          CustomsOfficeList(Nil)
        case other =>
          logger.info(s"[ReferenceDataConnector][getCustomsOfficesForCountry] Invalid downstream status $other")
          throw new IllegalStateException(s"Invalid Downstream Status $other")
      }

  implicit class OptionalCustomsOfficeList(customsOfficeList: Option[CustomsOfficeList]) {

    def getCustomsOffices: Seq[CustomsOffice] = customsOfficeList.map(_.customsOffices).getOrElse(Nil)
  }

  private def customsOfficeListReads(key: String): Reads[CustomsOfficeList] = Reads[CustomsOfficeList] {
    case JsArray(values) =>
      JsSuccess(
        CustomsOfficeList(
          values.flatMap {
            value => (value \ key).validate[CustomsOffice].asOpt
          }.toSeq
        )
      )
    case _ => JsError("CustomsOfficeList::customReads: Failed to read customs office list from cache")
  }

  val officesOfExitReads: Reads[CustomsOfficeList]    = customsOfficeListReads("officeOfExit")
  val officesOfTransitReads: Reads[CustomsOfficeList] = customsOfficeListReads("officeOfTransit")

}
