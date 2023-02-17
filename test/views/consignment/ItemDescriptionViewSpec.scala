package views.consignment

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.consignment.ItemDescriptionView

class ItemDescriptionViewSpec extends ViewBehaviours {

  override val urlContainsLrn: Boolean = true

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[ItemDescriptionView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "consignment.itemDescription"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
