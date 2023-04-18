package controllers.$package$

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.SelectableFormProvider
import models.{Mode, LocalReferenceNumber}
import navigation.{$navRoute$NavigatorProvider, UserAnswersNavigator}
import pages.$package$.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.$serviceName$
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.$package$.$className$View

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class $className$Controller @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: $navRoute$NavigatorProvider,
  actions: Actions,
  formProvider: SelectableFormProvider,
  service: $serviceName$,
  val controllerComponents: MessagesControllerComponents,
  view: $className$View
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val prefix: String = "$package$.$className;format="decap"$"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.$lookupReferenceListMethod$.map {
        $referenceClass;format="decap"$List =>
          val form = formProvider(prefix, $referenceClass;format="decap"$List)
          val preparedForm = request.userAnswers.get($className$Page) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, $referenceClass;format="decap"$List.values, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.$lookupReferenceListMethod$.flatMap {
        $referenceClass;format="decap"$List =>
          val form = formProvider(prefix, $referenceClass;format="decap"$List)
          form.bindFromRequest().fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, $referenceClass;format="decap"$List.values, mode))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
              $className$Page.writeToUserAnswers(value).updateTask().writeToSession().navigate()
            }
        )
      }
  }
}
