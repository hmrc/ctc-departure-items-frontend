package controllers.item

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CUSCodeFormProvider
import models.{Mode, LocalReferenceNumber}
import navigation.UserAnswersNavigator
import pages.item.CustomsUnionAndStatisticsCodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.CustomsUnionAndStatisticsCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsUnionAndStatisticsCodeController @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  formProvider: CUSCodeFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CustomsUnionAndStatisticsCodeView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("item.customsUnionAndStatisticsCode")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CustomsUnionAndStatisticsCodePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, lrn, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      form
       .bindFromRequest()
       .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          CustomsUnionAndStatisticsCodePage.writeToUserAnswers(value).updateTask().writeToSession().navigate()
        }
    )
  }
}
