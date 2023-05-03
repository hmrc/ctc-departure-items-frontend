package views.item.additionalInformation.index

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.item.additionalInformation.index.RemoveAdditionalInformationView

class RemoveAdditionalInformationViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[RemoveAdditionalInformationView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "item.additionalInformation.index.removeAdditionalInformation"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
