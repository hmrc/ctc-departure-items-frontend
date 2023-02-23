package pages.item

import controllers.item.routes
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.ItemSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object CustomsUnionAndStatisticsCodePage extends QuestionPage[String] {

  override def path: JsPath = ItemSection.path \ toString

  override def toString: String = "customsUnionAndStatisticsCode"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.CustomsUnionAndStatisticsCodeController.onPageLoad(userAnswers.lrn, mode))
}
