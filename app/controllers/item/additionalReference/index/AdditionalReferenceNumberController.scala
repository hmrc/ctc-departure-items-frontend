package controllers.item.additionalReference.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AdditionalReferenceNumberFormProvider
import models.{Mode, LocalReferenceNumber}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.additionalReference.index.AdditionalReferenceNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.additionalReference.index.AdditionalReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdditionalReferenceNumberController @Inject()(
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ItemNavigatorProvider,
  formProvider: AdditionalReferenceNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: AdditionalReferenceNumberView
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider("item.additionalReference.index.additionalReferenceNumber")

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AdditionalReferenceNumberPage) match {
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
          AdditionalReferenceNumberPage.writeToUserAnswers(value).updateTask().writeToSession().navigate()
        }
    )
  }
}
