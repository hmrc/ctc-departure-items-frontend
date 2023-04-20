package views.item.additionalReference.index

import forms.SelectableFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.{NormalMode, SelectableList}
import models.reference.AdditionalReference
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.item.additionalReference.index.AdditionalReferenceView

class AdditionalReferenceViewSpec extends InputSelectViewBehaviours[AdditionalReference] {

  override def form: Form[AdditionalReference] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[AdditionalReference]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalReferenceView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[AdditionalReference] = arbitraryAdditionalReference

  override val prefix: String = "item.additionalReference.index.additionalReference"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("What type of additional reference do you want to add hint")

  behave like pageWithContent("label", "What type of additional reference do you want to add label")

  behave like pageWithSubmitButton("Save and continue")
}
