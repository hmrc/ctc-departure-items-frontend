package pages.transport

import controllers.transport.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.PreTaskListSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object IdentificationNumberPage extends QuestionPage[String] {

  override def path: JsPath = PreTaskListSection.path \ toString

  override def toString: String = "identificationNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode))
}
