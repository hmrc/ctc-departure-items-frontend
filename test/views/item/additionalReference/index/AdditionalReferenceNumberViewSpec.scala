package views.item.additionalReference.index

import forms.AdditionalReferenceNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.item.additionalReference.index.AdditionalReferenceNumberView

class AdditionalReferenceNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "item.additionalReference.index.additionalReferenceNumber"

  override def form: Form[String] = new AdditionalReferenceNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalReferenceNumberView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
