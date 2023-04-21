package pages.item.additionalReference.index

import controllers.item.additionalReference.index.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.additionalReference.AdditionalReferenceSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object AdditionalReferenceNumberPage extends QuestionPage[String] {

  override def path: JsPath = AdditionalReferenceSection.path \ toString

  override def toString: String = "additionalReferenceNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AdditionalReferenceNumberController.onPageLoad(userAnswers.lrn, mode))
}
