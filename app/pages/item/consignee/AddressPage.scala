package pages.item.consignee

import controllers.item.consignee.routes
import models.{DynamicAddress, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.Consignee
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object AddressPage extends QuestionPage[DynamicAddress] {

  override def path: JsPath = Consignee.path \ toString

  override def toString: String = "address"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddressController.onPageLoad(userAnswers.lrn, mode))
}
