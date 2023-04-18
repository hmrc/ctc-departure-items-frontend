package views.$package$

import forms.SelectableFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.{NormalMode, SelectableList}
import models.reference.$referenceClass$
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.$package$.$className$View

class $className$ViewSpec extends InputSelectViewBehaviours[$referenceClass$] {

  override def form: Form[$referenceClass$] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[$referenceClass$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[$referenceClass$] = arbitrary$referenceClass$

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("$title$ hint")

  behave like pageWithContent("label", "$title$ label")

  behave like pageWithSubmitButton("Save and continue")
}
