package views.$package$

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
