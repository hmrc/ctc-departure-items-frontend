package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DateFormProvider
import models.{Mode, LocalReferenceNumber}
import navigation.{$navRoute$NavigatorProvider, UserAnswersNavigator}
import pages.$package$.$className$Page
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DateTimeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: $navRoute$NavigatorProvider,
  formProvider: DateFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: $className$View,
  dateTimeService: DateTimeService
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[LocalDate] = {
    val minDate: LocalDate = dateTimeService.plusMinusDays(-1)
    val maxDate: LocalDate = dateTimeService.today
    formProvider("$package$.$className;format="decap"$", minDate, maxDate)
  }

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get($className$Page) match {
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
          val navigator: UserAnswersNavigator = navigatorProvider(mode)
          $className$Page.writeToUserAnswers(value).updateTask().writeToSession(sessionRepository).navigateWith(navigator)
        }
      )
  }
}
