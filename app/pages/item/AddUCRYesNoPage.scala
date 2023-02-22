package pages.item

import controllers.item.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.ItemSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object AddUCRYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = ItemSection.path \ toString

  override def toString: String = "addUCRYesNo"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddUCRYesNoController.onPageLoad(userAnswers.lrn, mode))
}
