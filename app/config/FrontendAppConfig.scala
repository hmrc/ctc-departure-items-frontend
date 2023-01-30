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

package config

import com.google.inject.{Inject, Singleton}
import models.LocalReferenceNumber
import play.api.Configuration

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: MyServicesConfig) {

  val appName: String = configuration.get[String]("appName")

  val enrolmentProxyUrl: String            = servicesConfig.fullServiceUrl("enrolment-store-proxy")
  val eccEnrolmentSplashPage: String       = configuration.get[String]("urls.eccEnrolmentSplashPage")
  val legacyEnrolmentKey: String           = configuration.get[String]("keys.legacy.enrolmentKey")
  val legacyEnrolmentIdentifierKey: String = configuration.get[String]("keys.legacy.enrolmentIdentifierKey")
  val newEnrolmentKey: String              = configuration.get[String]("keys.enrolmentKey")
  val newEnrolmentIdentifierKey: String    = configuration.get[String]("keys.enrolmentIdentifierKey")

  val loginUrl: String         = configuration.get[String]("urls.login")
  val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")

  val nctsEnquiriesUrl: String = configuration.get[String]("urls.nctsEnquiries")
  val nctsGuidanceUrl: String  = configuration.get[String]("urls.nctsGuidance")

  val hubUrl: String     = servicesConfig.fullServiceUrl("manage-transit-movements-frontend")
  val serviceUrl: String = s"$hubUrl/what-do-you-want-to-do"

  val departureHubUrl: String = servicesConfig.fullServiceUrl("manage-transit-movements-departure-frontend")

  def taskListUrl(lrn: LocalReferenceNumber): String = s"$departureHubUrl/$lrn/task-list"

  val cacheUrl: String = servicesConfig.fullServiceUrl("manage-transit-movements-departure-cache")
}
