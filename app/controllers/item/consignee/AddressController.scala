package controllers.item.consignee

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DynamicAddressFormProvider
import models.reference.Country
import models.requests.SpecificDataRequestProvider2
import models.{DynamicAddress, LocalReferenceNumber, Mode}
import navigation.{ItemNavigatorProvider, UserAnswersNavigator}
import pages.item.consignee._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.item.consignee.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   implicit val sessionRepository: SessionRepository,
                                   navigatorProvider: ItemNavigatorProvider,
                                   actions: Actions,
                                   getMandatoryPage: SpecificDataRequiredActionProvider,
                                   formProvider: DynamicAddressFormProvider,
                                   countriesService: CountriesService,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: AddressView
                                 )(implicit ec: ExecutionContext)
  extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider2[String, Country]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def country(implicit request: Request): Country = request.arg._2

  private def form(isPostalCodeRequired: Boolean)(implicit request: Request): Form[DynamicAddress] =
    formProvider("item.consignee.address", isPostalCodeRequired, name)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(NamePage))
    .andThen(getMandatoryPage.getSecond(CountryPage))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).map {
          isPostalCodeRequired =>
            val preparedForm = request.userAnswers.get(AddressPage) match {
              case None => form(isPostalCodeRequired)
              case Some(value) => form(isPostalCodeRequired).fill(value)
            }

            Ok(view(preparedForm, lrn, mode, name, isPostalCodeRequired))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage.getFirst(NamePage))
    .andThen(getMandatoryPage.getSecond(CountryPage))
    .async {
      implicit request =>
        countriesService.doesCountryRequireZip(country).flatMap {
          isPostalCodeRequired =>
            form(isPostalCodeRequired)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, name, isPostalCodeRequired))),
                value => {
                  implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  AddressPage.writeToUserAnswers(value).updateTask().writeToSession().navigate()
                }
              )
        }
    }
}
