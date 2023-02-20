package controllers.items

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.{Mode, LocalReferenceNumber}
import models.items.DeclarationType
import navigation.UserAnswersNavigator
import pages.items.DeclarationTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.items.DeclarationTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationTypeController @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationTypeView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider[DeclarationType]("items.declarationType")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(DeclarationTypePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, DeclarationType.radioItems, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, DeclarationType.radioItems, mode))),
        value => {
          implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
          DeclarationTypePage.writeToUserAnswers(value).updateTask().writeToSession().navigate()
        }
      )
  }
}
