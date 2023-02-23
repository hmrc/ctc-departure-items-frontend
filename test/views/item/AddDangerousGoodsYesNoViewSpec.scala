package views.item

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.item.AddDangerousGoodsYesNoView

class AddDangerousGoodsYesNoViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddDangerousGoodsYesNoView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "item.addDangerousGoodsYesNo"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
