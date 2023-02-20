package views.items

import forms.EnumerableFormProvider
import models.NormalMode
import models.items.DeclarationType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.items.DeclarationTypeView

class DeclarationTypeViewSpec extends RadioViewBehaviours[DeclarationType] {

  override def form: Form[DeclarationType] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[DeclarationType]): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationTypeView].apply(form, lrn, DeclarationType.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "items.declarationType"

  override def radioItems(fieldId: String, checkedValue: Option[DeclarationType] = None): Seq[RadioItem] =
    DeclarationType.radioItems(fieldId, checkedValue)

  override def values: Seq[DeclarationType] = DeclarationType.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
