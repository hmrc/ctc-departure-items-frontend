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
import play.api.Configuration
import play.api.i18n.Messages
import play.api.mvc.Request

@Singleton
class RenderConfig @Inject() (configuration: Configuration) {

  val timeoutSeconds: Int   = configuration.get[Int]("session.timeoutSeconds")
  val countdownSeconds: Int = configuration.get[Int]("session.countdownSeconds")

  val showUserResearchBanner: Boolean = configuration.get[Boolean]("banners.showUserResearch")
  val userResearchUrl: String         = configuration.get[String]("urls.userResearch")

  val isTraderTest: Boolean = configuration.get[Boolean]("trader-test.enabled")
  val feedbackEmail: String = configuration.get[String]("trader-test.feedback.email")
  val feedbackForm: String  = configuration.get[String]("trader-test.feedback.link")

  def mailto(implicit request: Request[?], messages: Messages): String = {
    val subject = messages("site.email.subject")
    val body = {
      val newLine      = "%0D%0A"
      val newParagraph = s"$newLine$newLine"
      s"""
         |URL: ${request.uri}$newParagraph
         |Tell us how we can help you here.$newParagraph
         |Give us a brief description of the issue or question, including details like…$newLine
         | - The screens where you experienced the issue$newLine
         | - What you were trying to do at the time$newLine
         | - The information you entered$newParagraph
         |Please include your name and phone number and we’ll get in touch.
         |""".stripMargin
    }

    s"mailto:$feedbackEmail?subject=$subject&body=$body"
  }
}
