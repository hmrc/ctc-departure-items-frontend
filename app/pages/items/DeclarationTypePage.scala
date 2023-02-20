package pages.items

import controllers.items.routes
import models.items.DeclarationType
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.ItemSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object DeclarationTypePage extends QuestionPage[DeclarationType] {

  override def path: JsPath = ItemSection.path \ toString

  override def toString: String = "declarationType"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.DeclarationTypeController.onPageLoad(userAnswers.lrn, mode))
}
